package com.zerobase.memberapi.service;

import com.zerobase.memberapi.domain.dto.MemberDto;
import com.zerobase.memberapi.domain.entity.Member;
import com.zerobase.memberapi.domain.form.SignUp;
import com.zerobase.memberapi.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
@ExtendWith(MockitoExtension.class)
class MemberServiceTest {
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private MemberRepository memberRepository;
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

}