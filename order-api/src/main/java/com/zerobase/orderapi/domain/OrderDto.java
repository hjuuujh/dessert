package com.zerobase.orderapi.domain;

import com.zerobase.orderapi.domain.type.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderDto {
    private Long id;

    private Long sellerId;
    private Long customerId;
    private Long storeId;

    private Long itemId;
    private Long optionId;
    private Integer price;
    private Integer quantity;

    @Enumerated(EnumType.STRING)
    private Status status;

    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public static OrderDto from(Orders order) {
        return OrderDto.builder()
                .id(order.getId())
                .sellerId(order.getSellerId())
                .customerId(order.getCustomerId())
                .storeId(order.getStoreId())
                .itemId(order.getItemId())
                .optionId(order.getOptionId())
                .price(order.getPrice())
                .quantity(order.getPrice())
                .status(order.getStatus())
                .createdAt(order.getCreatedAt())
                .modifiedAt(order.getModifiedAt())
                .build();
    }
}


