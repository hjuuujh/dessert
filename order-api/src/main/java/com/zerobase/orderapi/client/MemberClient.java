package com.zerobase.orderapi.client;

import com.zerobase.orderapi.client.from.IncreaseBalanceForm;
import com.zerobase.orderapi.client.from.RefundForm;
import com.zerobase.orderapi.client.from.IncomeForm;
import com.zerobase.orderapi.client.from.DecreaseBalanceForm;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(value = "member", url = "${external-api.member.url}")
public interface MemberClient {

    // 요청한 유저의 id 가져옴
    @GetMapping("/id")
    Long getMemberId(@RequestHeader(name = "Authorization") String token);

    @PostMapping("/customer/order")
    void decreaseBalance(@RequestHeader(name = "Authorization") String token,
                         @RequestBody DecreaseBalanceForm form);

    @PostMapping("/seller/income")
    void income(@RequestHeader(name = "Authorization") String token,
                         @RequestBody IncreaseBalanceForm form);

    @PostMapping("/customer/refund")
    void increaseBalance(@RequestHeader(name = "Authorization") String token,
                         @RequestBody IncreaseBalanceForm form);

    @PostMapping("/seller/refund")
    void refund(@RequestHeader(name = "Authorization") String token,
                @RequestBody DecreaseBalanceForm form);
}
