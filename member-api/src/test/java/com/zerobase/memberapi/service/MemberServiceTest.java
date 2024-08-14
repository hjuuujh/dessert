package com.zerobase.memberapi.service;

import com.zerobase.memberapi.client.StoreClient;
import com.zerobase.memberapi.domain.dto.MemberDto;
import com.zerobase.memberapi.domain.entity.Member;
import com.zerobase.memberapi.domain.form.ChargeForm;
import com.zerobase.memberapi.domain.form.SignIn;
import com.zerobase.memberapi.domain.form.SignUp;
import com.zerobase.memberapi.exception.ErrorCode;
import com.zerobase.memberapi.exception.MemberException;
import com.zerobase.memberapi.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private StoreClient storeClient;
    @InjectMocks
    private MemberService memberService;

    @Test
    void successRegisterMember() {
        //given
        SignUp form = SignUp.builder()
                .email("user@gmail.com")
                .password("qwerty")
                .name("user1")
                .phone("0100000000")
                .build();

        given(memberRepository.existsByEmail(anyString()))
                .willReturn(false);

        given(passwordEncoder.encode(anyString()))
                .willReturn("password");

        Member member = Member.builder()
                .email("user@gmail.com")
                .password("password")
                .name("user1")
                .phone("0100000000")
                .roles(Arrays.asList("ROLE_SELLER"))
                .build();
        given(memberRepository.save(any()))
                .willReturn(member);

        ArgumentCaptor<Member> captor = ArgumentCaptor.forClass(Member.class);

        //when
        MemberDto result = memberService.registerMember(form);

        //then
        verify(memberRepository, times(1)).save(captor.capture());
        assertEquals("user@gmail.com", captor.getValue().getEmail());
        assertEquals("user1", captor.getValue().getName());
        assertEquals("0100000000", captor.getValue().getPhone());
    }

    @Test
    void failRegisterMember_ALREADY_REGISTERED_USER() {
        //given
        SignUp form = SignUp.builder()
                .email("user@gmail.com")
                .password("qwerty")
                .name("user1")
                .phone("0100000000")
                .build();

        given(memberRepository.existsByEmail(anyString()))
                .willReturn(true);

        //when
        MemberException exception = assertThrows(MemberException.class, () -> memberService.registerMember(form));

        //then
        assertEquals(ErrorCode.ALREADY_REGISTERED_USER, exception.getErrorCode());
        assertEquals("이미 가입된 이메일입니다.", exception.getErrorMessage());
    }


    @Test
    void successSignInMember() {
        //given
        SignIn form = SignIn.builder()
                .email("user@gmail.com")
                .password("qwerty")
                .build();
        Member member = Member.builder()
                .email("user@gmail.com")
                .password("password")
                .name("user1")
                .phone("0100000000")
                .roles(Arrays.asList("ROLE_CUSTOMER", "ROLE_PARTNER"))
                .build();

        given(memberRepository.findByEmail(anyString()))
                .willReturn(Optional.ofNullable(member));
        given(passwordEncoder.matches(anyString(), anyString()))
                .willReturn(true);

        //when
        MemberDto memberDto = memberService.signInMember(form);

        //then
        assertEquals("user@gmail.com", memberDto.getEmail());
        assertEquals("user1", memberDto.getName());
        assertEquals("0100000000", memberDto.getPhone());
    }

    @Test
    void failSignInMember_NOT_FOUND_USER() {
        //given
        SignIn form = SignIn.builder()
                .email("user@gmail.com")
                .password("qwerty")
                .build();
        given(memberRepository.findByEmail(anyString()))
                .willReturn(Optional.empty());

        //when
        MemberException exception = assertThrows(MemberException.class, () -> memberService.signInMember(form));

        //then
        assertEquals(ErrorCode.NOT_FOUND_USER, exception.getErrorCode());
        assertEquals("일치하는 회원이 없습니다.", exception.getErrorMessage());
    }

    @Test
    void failSignInMember_LOGIN_CHECK_FAIL() {
        //given
        SignIn form = SignIn.builder()
                .email("user@gmail.com")
                .password("qwerty")
                .build();
        Member member = Member.builder()
                .email("user@gmail.com")
                .password("password")
                .name("user1")
                .phone("0100000000")
                .roles(Arrays.asList("ROLE_CUSTOMER", "ROLE_PARTNER"))
                .build();

        given(memberRepository.findByEmail(anyString()))
                .willReturn(Optional.ofNullable(member));
        given(passwordEncoder.matches(anyString(), anyString()))
                .willReturn(false);

        //when
        MemberException exception = assertThrows(MemberException.class, () -> memberService.signInMember(form));

        //then
        assertEquals(ErrorCode.LOGIN_CHECK_FAIL, exception.getErrorCode());
        assertEquals("이메일과 패스워드를 확인해주세요.", exception.getErrorMessage());
    }

    @Test
    void successChargeBalance() {
        //given
        int balance = 0;
        int amount = 10000;
        Member member = Member.builder()
                .id(1L)
                .email("user@gmail.com")
                .password("password")
                .name("user1")
                .phone("0100000000")
                .roles(Arrays.asList("ROLE_CUSTOMER", "ROLE_PARTNER"))
                .balance(balance)
                .build();
        ChargeForm form = ChargeForm.builder()
                .amount(amount)
                .build();
        given(memberRepository.findById(anyLong()))
                .willReturn(Optional.of(member));

        //when
        MemberDto memberDto = memberService.chargeBalance(1L, form);

        //then
        assertEquals(balance + amount, memberDto.getBalance());
    }

    @Test
    void failChargeBalance_CHECK_AMOUNT() {
        //given
        int balance = 0;
        int amount = -10000;
        ChargeForm form = ChargeForm.builder()
                .amount(amount)
                .build();

        //when
        MemberException exception = assertThrows(MemberException.class, () -> memberService.chargeBalance(1L, form));


        //then
        assertEquals(ErrorCode.CHECK_AMOUNT, exception.getErrorCode());
        assertEquals("충전 금액을 확인해주세요.", exception.getErrorMessage());
    }

    @Test
    void successFollow(){
        //given
        given(storeClient.increaseFollow(any()))
                .willReturn(true);

        Member member = Member.builder()
                .id(1L)
                .followList(new HashSet<>())
                .build();

        given(memberRepository.findById(anyLong()))
                .willReturn(Optional.of(member));

        //when
        MemberDto memberDto = memberService.follow(1L, 1L);

        //then
        assertEquals(1, memberDto.getFollowList().size());
    }

    @Test
    void successUnFollow(){
        //given
        given(storeClient.decreaseFollow(any()))
                .willReturn(true);

        Set<Long> followList = new HashSet<>();
        followList.add(1L);
        Member member = Member.builder()
                .id(1L)
                .followList(followList)
                .build();

        given(memberRepository.findById(anyLong()))
                .willReturn(Optional.of(member));

        //when
        MemberDto memberDto = memberService.unfollow(1L, 1L);

        //then
        assertEquals(0, memberDto.getFollowList().size());
    }
}