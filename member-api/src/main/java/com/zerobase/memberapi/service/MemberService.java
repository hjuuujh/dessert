package com.zerobase.memberapi.service;


import com.zerobase.memberapi.domain.dto.MemberDto;
import com.zerobase.memberapi.domain.entity.Member;
import com.zerobase.memberapi.domain.form.SignUp;
import com.zerobase.memberapi.exception.ErrorCode;
import com.zerobase.memberapi.exception.MemberException;
import com.zerobase.memberapi.repository.MemberRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@AllArgsConstructor
@Transactional
public class MemberService implements UserDetailsService {
    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;

    /**
     * Spring Security를 이용
     * 유저의 정보를 불러오기 위해서 구현
     *
     * @param email: email을 계정의 고유한 값으로 이용
     * @return 유저 정보
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(email));
    }

    /**
     * form으로 받아온사용자 등록
     *
     * @param form : email, name, password, phone, roles
     * @return 저장된 사용자 정보
     */
    @Transactional
    public MemberDto registerMember(SignUp form) {
        checkAlreadyExists(form);

        Member member = Member.of(form, passwordEncoder.encode(form.getPassword()));

        Member save = memberRepository.save(member);
        return MemberDto.from(save);
    }

    /**
     * 가입하려는 이메일이 이미 존재하는 이메일인지 확인
     * excpetion : ALREADY_REGISTERED_USER "이미 가입된 이메일입니다."
     *
     * @param form
     */
    private void checkAlreadyExists(SignUp form) {
        // 이미 등록된 이메일인 경우 예외 발생 : ALREADY_REGISTERED_USER "이미 가입된 이메일입니다."
        if (memberRepository.existsByEmail(form.getEmail())) {
            throw new MemberException(ErrorCode.ALREADY_REGISTERED_USER);
        }
    }

}