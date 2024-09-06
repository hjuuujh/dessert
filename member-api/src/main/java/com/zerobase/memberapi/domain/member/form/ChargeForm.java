package com.zerobase.memberapi.domain.member.form;

import com.zerobase.memberapi.aop.BalanceLockIdInterface;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ChargeForm implements BalanceLockIdInterface {
    private Long customerId;
    @NotNull(message = "충전 금액은 필수입니다.")
    private int amount;
}
