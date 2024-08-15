package com.zerobase.memberapi.repository;

import com.zerobase.memberapi.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    boolean existsByEmail(String email);

    Optional<Member> findByEmail(String email);

    @Query(value = "select l.follow_list from member_follow_list l where l.member_id = (:memberId)", nativeQuery = true)
    List<Long> findFollowList(@Param("memberId") Long memberId);
}