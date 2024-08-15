package com.zerobase.memberapi.client;

import com.zerobase.memberapi.client.from.FollowForm;
import com.zerobase.memberapi.client.from.StoresForm;
import com.zerobase.memberapi.domain.store.StoreDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(value = "store", url = "${external-api.store.url}")
public interface StoreClient {
    // store에 팔로우수 증가
    @PostMapping("/follow")
    boolean increaseFollow(@RequestBody FollowForm form);
    @PostMapping("/unfollow")
    boolean decreaseFollow(@RequestBody FollowForm request);

    @PostMapping("/list")
    List<StoreDto> getStores(@RequestBody StoresForm request);
}
