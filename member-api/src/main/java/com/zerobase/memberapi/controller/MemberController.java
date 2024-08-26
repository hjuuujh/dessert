package com.zerobase.memberapi.controller;

import com.zerobase.memberapi.aop.BalanceLock;
import com.zerobase.memberapi.client.from.FollowForm;
import com.zerobase.memberapi.client.from.HeartForm;
import com.zerobase.memberapi.client.from.OrderForm;
import com.zerobase.memberapi.domain.member.dto.MemberDto;
import com.zerobase.memberapi.domain.member.form.*;
import com.zerobase.memberapi.security.TokenProvider;
import com.zerobase.memberapi.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
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

    /**
     * 이메일과 패스워드로 로그인
     *
     * @param form   : email, password
     * @param errors : form의 validation 체크후 잘못된 형식의 메세지 리턴
     * @return 토큰
     */
    @PostMapping("/signin")
    public ResponseEntity<?> signInMember(@RequestBody @Valid SignIn form, Errors errors) {
        List<ResponseError> responseErrors = validationErrorResponse.checkValidation(errors);
        if (!responseErrors.isEmpty()) {
            return new ResponseEntity<>(responseErrors, HttpStatus.BAD_REQUEST);
        }

        MemberDto memberDto = memberService.signInMember(form);
        log.info("user login -> {}", memberDto.getName());
        String token = tokenProvider.generateToken(memberDto.getId(), memberDto.getEmail(), memberDto.getRoles());

        return ResponseEntity.ok(TokenResponse.from(token));
    }

    /**
     * 사용자 정보 찾기
     *
     * @param token
     * @return 토큰으로 찾은 유저 아이디
     */
    @GetMapping("/id")
    public ResponseEntity<?> getMemberId(@RequestHeader(name = "Authorization") String token) {
        Long id = tokenProvider.getUserIdFromToken(token);
        return ResponseEntity.ok(id);
    }

    @PostMapping("/charge")
    @BalanceLock
    public ResponseEntity<?> chargeBalance(@RequestHeader(name = "Authorization") String token,
                                           @RequestBody @Valid ChargeForm form, Errors errors) {
        List<ResponseError> responseErrors = validationErrorResponse.checkValidation(errors);
        if (!responseErrors.isEmpty()) {
            return new ResponseEntity<>(responseErrors, HttpStatus.BAD_REQUEST);
        }

        return ResponseEntity.ok(memberService.chargeBalance(tokenProvider.getUserIdFromToken(token), form));
    }

    @PostMapping("/follow")
    public ResponseEntity<?> follow(@RequestHeader(name = "Authorization") String token, @RequestBody FollowForm form) {

        return ResponseEntity.ok(memberService.follow(tokenProvider.getUserIdFromToken(token), form.getStoreId()));
    }

    @PostMapping("/unfollow")
    public ResponseEntity<?> unfollow(@RequestHeader(name = "Authorization") String token, @RequestBody FollowForm form) {

        return ResponseEntity.ok(memberService.unfollow(tokenProvider.getUserIdFromToken(token), form.getStoreId()));
    }

    @GetMapping("/stores")
    public ResponseEntity<?> getFollowStores(@RequestHeader(name = "Authorization") String token,
                                             Pageable pageable) {
        return ResponseEntity.ok(memberService.getFollowStores(tokenProvider.getUserIdFromToken(token),pageable));
    }

    @PostMapping("/heart")
    public ResponseEntity<?> heart(@RequestHeader(name = "Authorization") String token, @RequestBody HeartForm form) {

        return ResponseEntity.ok(memberService.heart(tokenProvider.getUserIdFromToken(token), form));
    }

    @PostMapping("/unheart")
    public ResponseEntity<?> unheart(@RequestHeader(name = "Authorization") String token, @RequestBody HeartForm form) {

        return ResponseEntity.ok(memberService.unheart(tokenProvider.getUserIdFromToken(token), form));
    }

    @GetMapping("/items")
    public ResponseEntity<?> getHeartItems(@RequestHeader(name = "Authorization") String token,
                                           Pageable pageable) {
        return ResponseEntity.ok(memberService.getHeartItems(tokenProvider.getUserIdFromToken(token), pageable));
    }

    @PostMapping("/delete/heart")
    public ResponseEntity<?> deleteHeartItem(@RequestParam("itemId") Long id){
        memberService.deleteHeartItem(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/delete/follow")
    public ResponseEntity<?> deleteFollowStore(@RequestParam("storeId") Long id){
        memberService.deleteFollowStore(id);
        return ResponseEntity.ok().build();

    }

    @GetMapping("/balance")
    @BalanceLock
    public ResponseEntity<?> getBalance(@RequestHeader(name = "Authorization") String token){
        return ResponseEntity.ok(memberService.getBalance(tokenProvider.getUserIdFromToken(token)));
    }

    @PostMapping("/order")
    @BalanceLock
    public ResponseEntity<?> decreaseBalance(@RequestHeader(name = "Authorization") String token,
                                           @RequestBody OrderForm form) {
        memberService.decreaseBalance(tokenProvider.getUserIdFromToken(token), form);
        return ResponseEntity.ok().build();
    }

}