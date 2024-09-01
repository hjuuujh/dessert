package com.zerobase.orderapi.service;

import com.zerobase.orderapi.client.from.*;
import com.zerobase.orderapi.client.MemberClient;
import com.zerobase.orderapi.client.StoreClient;
import com.zerobase.orderapi.client.to.OrderResult;
import com.zerobase.orderapi.domain.CancelOrder;
import com.zerobase.orderapi.domain.CancelRefundOrder;
import com.zerobase.orderapi.domain.OrderDto;
import com.zerobase.orderapi.domain.Orders;
import com.zerobase.orderapi.domain.type.Status;
import com.zerobase.orderapi.exception.OrderException;
import com.zerobase.orderapi.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.zerobase.orderapi.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final MemberClient memberClient;
    private final StoreClient storeClient;

    public List<OrderResult> order(String token, Cart cart) {
        // member point 감소
        OrderForm request = OrderForm.builder()
                .totalPrice(cart.getTotalPrice())
                .date(LocalDate.now())
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
                        .status(Status.ORDERED)
                        .build();

                IncomeForm incomeRequest = IncomeForm.builder()
                        .sellerId(item.getSellerId())
                        .price(totalPrice).build();
                memberClient.income(token, incomeRequest);

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

        if (Status.REFUND_REQUEST.equals(orders.getStatus())) {
            throw new OrderException(ALREADY_REFUND_REQUEST);
        } else if (Status.REFUND_APPROVED.equals(orders.getStatus())) {
            throw new OrderException(ALREADY_REFUND_APPROVED);
        } else if (Status.REFUND_REJECTED.equals(orders.getStatus())) {
            throw new OrderException(ALREADY_REFUND_REJECTED);
        }

        orders.updateStatus(Status.REFUND_REQUEST);

        return OrderDto.from(orders);
    }

    @Transactional
    public OrderDto approveRequestRefund(String token, Long memberId, Long id) {
        Orders orders = getOrders(memberId, id);

        orders.updateStatus(Status.REFUND_APPROVED);

        RefundForm request = RefundForm.builder()
                .amount(orders.getPrice()).build();

        // 고객 잔액 증가
        memberClient.increaseBalance(token, request);

        // 셀러 인컴 감소
        // 정산 시스템 구현후 수정
        memberClient.refund(token, request);
        return OrderDto.from(orders);
    }


    @Transactional
    public OrderDto rejectRequestRefund(Long memberId, Long id) {
        Orders orders = getOrders(memberId, id);

        orders.updateStatus(Status.REFUND_REJECTED);
        return OrderDto.from(orders);
    }

    private Orders getOrders(Long memberId, Long id) {
        Orders orders = orderRepository.findByIdAndSellerId(id, memberId)
                .orElseThrow(() -> new OrderException(UNMATCHED_MEMBER_ORDER));
        if (Status.ORDERED.equals(orders.getStatus())) {
            throw new OrderException(NO_REFUND_REQUEST);
        } else if (Status.REFUND_APPROVED.equals(orders.getStatus())) {
            throw new OrderException(ALREADY_REFUND_APPROVED);
        } else if (Status.REFUND_REJECTED.equals(orders.getStatus())) {
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
        if (Status.REFUND_APPROVED.equals(orders.getStatus())) {
            throw new OrderException(ALREADY_REFUND_APPROVED);
        } else if (Status.REFUND_REJECTED.equals(orders.getStatus())) {
            throw new OrderException(ALREADY_REFUND_REJECTED);
        }else if (Status.REFUND_REQUEST.equals(orders.getStatus())) {
            throw new OrderException(ALREADY_REFUND_REQUEST);
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
        if (Status.REFUND_APPROVED.equals(orders.getStatus())) {
            throw new OrderException(ALREADY_REFUND_APPROVED);
        } else if (Status.REFUND_REJECTED.equals(orders.getStatus())) {
            throw new OrderException(ALREADY_REFUND_REJECTED);
        }else if (Status.ORDERED.equals(orders.getStatus())) {
            throw new OrderException(NO_REFUND_REQUEST);
        }

        orders.updateStatus(Status.ORDERED);
        return OrderDto.from(orders);
    }
}
