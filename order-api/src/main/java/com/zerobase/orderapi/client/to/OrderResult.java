package com.zerobase.orderapi.client.to;

import com.zerobase.orderapi.domain.Orders;
import com.zerobase.orderapi.domain.type.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderResult {
    private Long orderId;
    private String itemName;
    private String optionName;
    private Integer optionPrice;
    private Integer optionQuantity;
    private Status status;
    private LocalDateTime createdAt;

    public static OrderResult from(Orders order) {
        return OrderResult.builder()
                .orderId(order.getId())
                .itemName(order.getItemName())
                .optionName(order.getOptionName())
                .optionPrice(order.getPrice())
                .optionQuantity(order.getQuantity())
                .status(order.getStatus())
                .createdAt(order.getCreatedAt())
                .build();
    }

}
