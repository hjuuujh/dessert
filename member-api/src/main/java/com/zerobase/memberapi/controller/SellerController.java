package com.zerobase.memberapi.controller;

import com.zerobase.memberapi.aop.BalanceLock;
import com.zerobase.memberapi.client.from.*;
import com.zerobase.memberapi.security.TokenProvider;
import com.zerobase.memberapi.service.CustomerService;
import com.zerobase.memberapi.service.SellerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member/seller")
public class SellerController {
    private final CustomerService customerService;
    private final SellerService sellerService;
    private final TokenProvider tokenProvider;
    private final ValidationErrorResponse validationErrorResponse;

    @PostMapping("/income")
    @BalanceLock
    public void income(@RequestHeader(name = "Authorization") String token,
                @RequestBody IncreaseBalanceForm form){
        sellerService.income(tokenProvider.getUserIdFromToken(token), form);
    }

    @PostMapping("/refund")
    @BalanceLock
    public void refund(@RequestHeader(name = "Authorization") String token,
                @RequestBody DecreaseBalanceForm form){
        sellerService.refund(tokenProvider.getUserIdFromToken(token), form);
    }
}