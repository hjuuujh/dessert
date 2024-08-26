package com.zerobase.memberapi.repository;

import com.zerobase.memberapi.domain.member.entity.Seller;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SellerRepository extends JpaRepository<Seller, Long> {

    boolean existsByEmail(String email);

    Optional<Seller> findByEmail(String email);

}