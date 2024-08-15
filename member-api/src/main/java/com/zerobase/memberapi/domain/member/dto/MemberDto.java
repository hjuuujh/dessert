package com.zerobase.memberapi.domain.member.dto;

import com.zerobase.memberapi.domain.member.entity.Member;
import lombok.*;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberDto {
    private Long id;

    private String email;
    private String name;
    private String phone;
    private List<String> roles;
    private Set<Long> followList;
    private Set<Long> heartList;
    private int balance;


    public static MemberDto from(Member member) {
        return MemberDto.builder()
                .id(member.getId())
                .email(member.getEmail())
                .name(member.getName())
                .phone(member.getPhone())
                .roles(member.getRoles())
                .balance(member.getBalance())
                .followList(member.getFollowList())
                .heartList(member.getHeartList())
                .build();
    }

}