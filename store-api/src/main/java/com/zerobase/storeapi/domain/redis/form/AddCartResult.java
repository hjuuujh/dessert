package com.zerobase.storeapi.domain.redis.form;

import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AddCartResult {
    private String storeName;
    private String name;
    private int price;
}
