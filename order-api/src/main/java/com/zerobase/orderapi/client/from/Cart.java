package com.zerobase.orderapi.client.from;

import lombok.*;

import javax.persistence.Id;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class Cart {

    private Long customerId;
    private List<Item> items;
    private int totalPrice;
    @Getter
    @NoArgsConstructor
    @Builder
    @AllArgsConstructor
    public static class Item  {

        private Long id;
        private Long storeId;
        private Long sellerId;
        private String storeName;
        private String name;
        private List<Option> options;

    }

    @Getter
    @NoArgsConstructor
    @Builder
    @AllArgsConstructor
    public static class Option {
        private Long id;
        private String name;
        private Integer quantity;
        private Integer price;
    }
}
