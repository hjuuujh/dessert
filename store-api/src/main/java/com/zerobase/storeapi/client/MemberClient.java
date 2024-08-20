package com.zerobase.storeapi.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(value = "member", url = "${external-api.member.url}")
public interface MemberClient {

    // 요청한 유저의 id 가져옴
    @GetMapping("/id")
    Long getMemberId(@RequestHeader(name = "Authorization") String token);

    @PostMapping("/delete/heart")
    void deleteHeartItem(@RequestParam("itemId") Long id);

    @PostMapping("/delete/follow")
    void deleteFollowStore(@RequestParam("storeId") Long id);

    @GetMapping("/balance")
    Long getBalance(@RequestHeader(name = "Authorization") String token);
}
