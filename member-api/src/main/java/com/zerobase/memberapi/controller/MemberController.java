package com.zerobase.memberapi.controller;

import com.zerobase.memberapi.domain.form.SignUp;
import com.zerobase.memberapi.security.TokenProvider;
import com.zerobase.memberapi.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member")
public class MemberController {
    private final MemberService memberService;
    private final TokenProvider tokenProvider;
    private final ValidationErrorResponse validationErrorResponse;

    /**
     * 사용자 정보 등록
     *
     * @param form   : email, name, password, phone, roles
     * @param errors : form의 validation 체크후 잘못된 형식의 메세지 리턴
     * @return 저장된 유저 정보
     */
    @PostMapping("/signup")
    public ResponseEntity<?> registerMember(@RequestBody @Valid SignUp form, Errors errors) {
        List<ResponseError> responseErrors = validationErrorResponse.checkValidation(errors);
        if (!responseErrors.isEmpty()) {
            return new ResponseEntity<>(responseErrors, HttpStatus.BAD_REQUEST);
        }

        return ResponseEntity.ok(memberService.registerMember(form));
    }


}