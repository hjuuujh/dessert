package com.zerobase.memberapi.client.from;

import com.zerobase.memberapi.aop.BalanceLockIdInterface;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DecreaseBalanceForm implements BalanceLockIdInterface {
    private Long customerId;
    private Integer totalPrice;
}
