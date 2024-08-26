package com.zerobase.memberapi.client.from;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderForm {
    private String orderId;
    private Integer totalPrice;
    private LocalDate date;
}
