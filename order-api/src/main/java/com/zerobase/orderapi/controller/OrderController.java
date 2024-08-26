package com.zerobase.orderapi.controller;

import com.zerobase.orderapi.client.MemberClient;
import com.zerobase.orderapi.client.from.Cart;
import com.zerobase.orderapi.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    private final MemberClient memberClient;

    @PostMapping
    public ResponseEntity<?> order(@RequestHeader(name = "Authorization") String token,
                                   @RequestBody Cart cart){
        orderService.order(token, cart);
        return ResponseEntity.ok().build();
    }

    // 고객이 주문현황 기간별 확인
    @GetMapping("/customer")
    public ResponseEntity<?> getOrders(@RequestHeader(name = "Authorization") String token,
                                       @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate start,
                                       @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end,
                                       Pageable pageable){
        return ResponseEntity.ok(orderService.getOrders(memberClient.getMemberId(token), start, end, pageable));
    }

    // 판매자가 주문현황 날짜별 확인
    @GetMapping("/seller/{storeId}")
    public ResponseEntity<?> getOrdersByStore(@RequestHeader(name = "Authorization") String token,
                                       @PathVariable Long storeId,
                                       @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate start,
                                       @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end,
                                       Pageable pageable){
        return ResponseEntity.ok(orderService.getOrdersByStore(memberClient.getMemberId(token), storeId, start, end, pageable));
    }

    // 환불 신청
    @PatchMapping("/customer/refund/{id}")
    public ResponseEntity<?> requestRefund(@RequestHeader(name = "Authorization") String token,
                                       @PathVariable Long id){
        return ResponseEntity.ok(orderService.requestRefund(memberClient.getMemberId(token), id));
    }

    // 환불 수락
    @PatchMapping("/seller/refund/approve/{id}")
    public ResponseEntity<?> approveRequestRefund(@RequestHeader(name = "Authorization") String token,
                                           @PathVariable Long id){
        return ResponseEntity.ok(orderService.approveRequestRefund(token, memberClient.getMemberId(token), id));
    }

    // 환불 거절
    @PatchMapping("/seller/refund/reject/{id}")
    public ResponseEntity<?> rejectRequestRefund(@RequestHeader(name = "Authorization") String token,
                                                  @PathVariable Long id){
        return ResponseEntity.ok(orderService.rejectRequestRefund(memberClient.getMemberId(token), id));
    }

    // 스프링 배치 물어보고 해

    // 도커

    // restdocs

    // 금요일에 사진찍고 이력서 작성
}
