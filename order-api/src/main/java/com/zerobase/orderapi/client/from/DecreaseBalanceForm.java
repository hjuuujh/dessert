package com.zerobase.orderapi.client.from;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DecreaseBalanceForm {
    private Long customerId;
    private Integer totalPrice;
}
