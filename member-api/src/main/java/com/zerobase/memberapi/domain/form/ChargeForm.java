package com.zerobase.memberapi.domain.form;

import com.zerobase.memberapi.aop.BalanceLockIdInterface;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ChargeForm implements BalanceLockIdInterface {
    @NotBlank(message = "이메일은 필수입니다.")
    private String email;
    @NotNull(message = "충전 금액은 필수입니다.")
    private int amount;
}
