package com.zerobase.storeapi.repository;

import com.zerobase.storeapi.domain.entity.Store;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StoreRepository extends JpaRepository<Store, Long> {
    boolean existsByName(String name);
    Optional<Store> findByIdAndSellerId(Long id, Long sellerId);

    Page<Store> findByNameContainingIgnoreCaseAndDeleted(String keyword, boolean deleted, Pageable pageable);
    Page<Store> findByNameContainingIgnoreCaseAndDeletedOrderByFollowCountDesc(String keyword, boolean deleted, Pageable pageable);

    Page<Store> findAllByIdInAndDeleted(List<Long> ids, boolean deleted, Pageable pageable);
}
