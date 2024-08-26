package com.zerobase.storeapi.client;

import com.zerobase.storeapi.domain.redis.Cart;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "order", url = "${external-api.order.url}")
public interface OrderClient {

    @PostMapping
    void order(@RequestHeader(name = "Authorization") String token,
                         @RequestBody Cart cart);
}
