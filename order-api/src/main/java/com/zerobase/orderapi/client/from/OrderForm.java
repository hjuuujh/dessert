package com.zerobase.orderapi.client.from;

import com.zerobase.orderapi.domain.type.Status;
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
