package com.zerobase.orderapi.service;

import com.zerobase.orderapi.client.from.RefundForm;
import com.zerobase.orderapi.client.MemberClient;
import com.zerobase.orderapi.client.StoreClient;
import com.zerobase.orderapi.client.from.Cart;
import com.zerobase.orderapi.client.from.IncomeForm;
import com.zerobase.orderapi.client.from.MatchForm;
import com.zerobase.orderapi.client.from.OrderForm;
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

import static com.zerobase.orderapi.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final MemberClient memberClient;
    private final StoreClient storeClient;

    public void order(String token, Cart cart) {
        // member point 감소
        OrderForm request = OrderForm.builder()
                .totalPrice(cart.getTotalPrice())
                .date(LocalDate.now())
                .build();
        memberClient.decreaseBalance(token, request);

        // 옵션별 주문 내용 저장
        for (Cart.Item item : cart.getItems()) {
            for (Cart.Option option : item.getOptions()) {
                int price = option.getPrice() * option.getQuantity();
                Orders order = Orders.builder()
                        .customerId(cart.getCustomerId())
                        .storeId(item.getStoreId())
                        .sellerId(item.getSellerId())
                        .itemId(item.getId())
                        .optionId(option.getId())
                        .price(price)
                        .quantity(option.getQuantity())
                        .status(Status.ORDERED)
                        .build();
                IncomeForm incomeRequest = IncomeForm.builder()
                        .sellerId(item.getSellerId())
                        .price(price).build();
                memberClient.income(token, incomeRequest);

                orderRepository.save(order);
            }
        }

    }

    public Page<OrderDto> getOrders(Long customerId, LocalDate start, LocalDate end, Pageable pageable) {

        System.out.println("##################");
        System.out.println(customerId);
        return orderRepository.findAllByCustomerIdAndModifiedAtBetween(customerId, start.atStartOfDay(), end.atStartOfDay(), pageable)
                .map(OrderDto::from);
    }

    public Page<OrderDto> getOrdersByStore(Long sellerId, Long storeId, LocalDate start, LocalDate end, Pageable pageable) {
        // 확인하려는 셀러의 매장인지 확인
        MatchForm request = MatchForm.builder()
                .sellerId(sellerId)
                .storeId(storeId)
                .build();
        if (!storeClient.isMatchedStoreAndSeller(request)) {
            throw new OrderException(UNMATCHED_SELLER_STORE);
        }

        return orderRepository.findAllByStoreIdAndModifiedAtBetween(storeId, start.atStartOfDay(), end.atStartOfDay(), pageable)
                .map(OrderDto::from);
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
        memberClient.refund(token, request);
        return OrderDto.from(orders);
    }


    @Transactional
    public Object rejectRequestRefund(Long memberId, Long id) {
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
}
