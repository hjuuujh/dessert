package com.zerobase.orderapi.service;

import com.zerobase.orderapi.client.MemberClient;
import com.zerobase.orderapi.client.StoreClient;
import com.zerobase.orderapi.client.from.*;
import com.zerobase.orderapi.client.to.OrderResult;
import com.zerobase.orderapi.domain.CancelOrder;
import com.zerobase.orderapi.domain.order.OrderDto;
import com.zerobase.orderapi.domain.order.Orders;
import com.zerobase.orderapi.domain.order.Settlement;
import com.zerobase.orderapi.domain.order.SettlementResult;
import com.zerobase.orderapi.domain.type.OrderStatus;
import com.zerobase.orderapi.domain.type.SettlementStatus;
import com.zerobase.orderapi.exception.OrderException;
import com.zerobase.orderapi.repository.order.OrderRepository;
import com.zerobase.orderapi.repository.order.SettlementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.zerobase.orderapi.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final StoreClient storeClient;
    private final SettlementRepository settlementRepository;
    private final MemberClient memberClient;

    @Transactional
    public List<OrderResult> order(String token, Cart cart) {
        // member point 감소
        DecreaseBalanceForm request = DecreaseBalanceForm.builder()
                .customerId(cart.getCustomerId())
                .totalPrice(cart.getTotalPrice())
                .build();
        memberClient.decreaseBalance(token, request);
        List<OrderResult> results = new ArrayList<>();
        // 옵션별 주문 내용 저장
        for (Cart.Item item : cart.getItems()) {
            for (Cart.Option option : item.getOptions()) {
                int totalPrice = option.getPrice() * option.getQuantity();
                Orders order = Orders.builder()
                        .customerId(cart.getCustomerId())
                        .storeId(item.getStoreId())
                        .sellerId(item.getSellerId())
                        .itemId(item.getId())
                        .itemName(item.getName())
                        .optionId(option.getId())
                        .optionName(option.getName())
                        .price(option.getPrice())
                        .totalPrice(totalPrice)
                        .quantity(option.getQuantity())
                        .orderStatus(OrderStatus.ORDERED)
                        .build();

                orderRepository.save(order);
                results.add(OrderResult.from(order));
            }
        }
        return results;
    }

    public Page<OrderResult> getOrders(Long customerId, LocalDate start, LocalDate end, Pageable pageable) {

        return orderRepository.findAllByCustomerIdAndModifiedAtBetween(customerId, start.atStartOfDay(), end.plusDays(1).atStartOfDay(), pageable)
                .map(OrderResult::from);
    }

    public Page<OrderResult> getOrdersByStore(Long sellerId, Long storeId, LocalDate start, LocalDate end, Pageable pageable) {
        // 확인하려는 셀러의 매장인지 확인
        MatchForm request = MatchForm.builder()
                .sellerId(sellerId)
                .storeId(storeId)
                .build();
        if (!storeClient.isMatchedStoreAndSeller(request)) {
            throw new OrderException(UNMATCHED_SELLER_STORE);
        }

        return orderRepository.findAllByStoreIdAndModifiedAtBetween(storeId, start.atStartOfDay(), end.plusDays(1).atStartOfDay(), pageable)
                .map(OrderResult::from);
    }

    @Transactional
    public OrderDto requestRefund(Long memberId, Long id) {
        Orders orders = orderRepository.findByIdAndCustomerId(id, memberId)
                .orElseThrow(() -> new OrderException(UNMATCHED_MEMBER_ORDER));

        if (OrderStatus.REFUND_REQUEST.equals(orders.getOrderStatus())) {
            throw new OrderException(ALREADY_REFUND_REQUEST);
        } else if (OrderStatus.REFUND_APPROVED.equals(orders.getOrderStatus())) {
            throw new OrderException(ALREADY_REFUND_APPROVED);
        } else if (OrderStatus.REFUND_REJECTED.equals(orders.getOrderStatus())) {
            throw new OrderException(ALREADY_REFUND_REJECTED);
        }

        orders.updateStatus(OrderStatus.REFUND_REQUEST);

        return OrderDto.from(orders);
    }

    @Transactional
    public OrderDto approveRequestRefund(String token, Long sellerId, RefundForm form) {
        Orders orders = getOrders(sellerId, form.getId());

        orders.updateStatus(OrderStatus.REFUND_APPROVED);

        // 고객 잔액 증가
        IncreaseBalanceForm request = IncreaseBalanceForm.builder()
                .totalPrice(orders.getTotalPrice())
                .build();
        memberClient.increaseBalance(token, request);

        // 셀러 인컴 감소
        // 정산 시스템 구현후 수정
        Optional<Settlement> optionalSettlement = settlementRepository.findBySellerIdAndDate(sellerId, form.getDate());
        if (optionalSettlement.isPresent()) {
            Settlement settlement = optionalSettlement.get();
            if (SettlementStatus.YET.equals(settlement.getStatus())) {
                settlement.decreaseSettlementAmount(orders.getTotalPrice());
            } else {
                DecreaseBalanceForm requestSeller = DecreaseBalanceForm.builder()
                        .totalPrice(orders.getTotalPrice())
                        .build();
                memberClient.refund(token, requestSeller);
            }
        }
        return OrderDto.from(orders);
    }


    @Transactional
    public OrderDto rejectRequestRefund(Long memberId, Long id) {
        Orders orders = getOrders(memberId, id);

        orders.updateStatus(OrderStatus.REFUND_REJECTED);
        return OrderDto.from(orders);
    }

    private Orders getOrders(Long memberId, Long id) {
        Orders orders = orderRepository.findByIdAndSellerId(id, memberId)
                .orElseThrow(() -> new OrderException(UNMATCHED_MEMBER_ORDER));
        if (OrderStatus.ORDERED.equals(orders.getOrderStatus())) {
            throw new OrderException(NO_REFUND_REQUEST);
        } else if (OrderStatus.REFUND_APPROVED.equals(orders.getOrderStatus())) {
            throw new OrderException(ALREADY_REFUND_APPROVED);
        } else if (OrderStatus.REFUND_REJECTED.equals(orders.getOrderStatus())) {
            throw new OrderException(ALREADY_REFUND_REJECTED);
        }
        return orders;
    }

    public OrderDto getOrderById(Long memberId, Long id) {
        Orders orders = orderRepository.findByIdAndCustomerId(id, memberId)
                .orElseThrow(() -> new OrderException(UNMATCHED_MEMBER_ORDER));
        return OrderDto.from(orders);
    }

    public CancelOrder cancelOrder(Long memberId, Long id) {
        Orders orders = orderRepository.findByIdAndCustomerId(id, memberId)
                .orElseThrow(() -> new OrderException(UNMATCHED_MEMBER_ORDER));
        if (OrderStatus.REFUND_APPROVED.equals(orders.getOrderStatus())) {
            throw new OrderException(ALREADY_REFUND_APPROVED);
        } else if (OrderStatus.REFUND_REJECTED.equals(orders.getOrderStatus())) {
            throw new OrderException(ALREADY_REFUND_REJECTED);
        }
        orderRepository.delete(orders);

        return CancelOrder.builder()
                .itemName(orders.getItemName())
                .optionName(orders.getOptionName())
                .cancelTime(LocalDateTime.now())
                .build();
    }

    @Transactional
    public OrderDto cancelRequestRefund(Long memberId, Long id) {
        Orders orders = orderRepository.findByIdAndCustomerId(id, memberId)
                .orElseThrow(() -> new OrderException(UNMATCHED_MEMBER_ORDER));
        if (OrderStatus.REFUND_APPROVED.equals(orders.getOrderStatus())) {
            throw new OrderException(ALREADY_REFUND_APPROVED);
        } else if (OrderStatus.REFUND_REJECTED.equals(orders.getOrderStatus())) {
            throw new OrderException(ALREADY_REFUND_REJECTED);
        } else if (OrderStatus.ORDERED.equals(orders.getOrderStatus())) {
            throw new OrderException(NO_REFUND_REQUEST);
        }

        orders.updateStatus(OrderStatus.ORDERED);
        return OrderDto.from(orders);
    }


    @Transactional
    public SettlementResult requestSettlement(String token, Long sellerId, LocalDate start, LocalDate end) {
        List<Settlement> settlements = settlementRepository.findBySellerIdAndStatusAndDateBetween(sellerId, SettlementStatus.YET, start, end);
        int settlementAmount = 0;
        for (Settlement settlement : settlements) {
            System.out.println(settlement.getDate());
            settlementAmount += settlement.getSettlementAmount();
            settlement.updateStatus();
        }

        IncreaseBalanceForm request = IncreaseBalanceForm.builder()
                .totalPrice(settlementAmount)
                .build();
        memberClient.income(token, request);
        return SettlementResult.builder()
                .sellerId(sellerId)
                .settlementAmount(settlementAmount)
                .build();
    }
}
