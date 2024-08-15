package com.zerobase.storeapi.service;

import com.zerobase.storeapi.client.from.FollowForm;
import com.zerobase.storeapi.client.from.StoresForm;
import com.zerobase.storeapi.domain.dto.StoreDto;
import com.zerobase.storeapi.domain.entity.Store;
import com.zerobase.storeapi.domain.form.store.RegisterStore;
import com.zerobase.storeapi.domain.form.store.UpdateStore;
import com.zerobase.storeapi.exception.StoreException;
import com.zerobase.storeapi.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.zerobase.storeapi.exception.ErrorCode.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class StoreService {
    private final StoreRepository storeRepository;


    public StoreDto registerStore(Long sellerId, RegisterStore form) {
        checkDuplicateStoreName(form.getName());

        Store store = Store.of(sellerId, form);
        storeRepository.save(store);
        return StoreDto.from(store);
    }

    /**
     * 매장 이름 중복 확인
     * exception : DUPLICATE_STORE_NAME "매장명은 중복일 수 없습니다."
     *
     * @param name
     */
    private void checkDuplicateStoreName(String name) {
        boolean exists = storeRepository.existsByName(name);

        if (exists) {
            throw new StoreException(DUPLICATE_STORE_NAME);
        }
    }

    @Transactional
    public StoreDto updateStore(Long sellerId, UpdateStore form) {
        checkDuplicateStoreName(form.getName());
        Store store = checkMatchSellerAndStore(sellerId, form.getId());
        store.update(form);
        return StoreDto.from(store);
    }

    private Store checkMatchSellerAndStore(Long sellerId, Long id) {
        return storeRepository.findByIdAndSellerId(id, sellerId)
                .orElseThrow(() -> new StoreException(UNMATCHED_SELLER_STORE));
    }

    @Transactional
    public boolean deleteStore(Long sellerId, Long id) {
        Store store = checkMatchSellerAndStore(sellerId, id);
        store.delete();

        // 아이템 삭제 코드 추가 필요

        return store.isDeleted();
    }

    @Transactional
    public boolean followIncrease(FollowForm form) {
        try {
            Store store = storeRepository.findById(form.getStoreId())
                    .orElseThrow(() -> new StoreException(NOT_FOUND_STORE));
            store.increaseFollow();
            return true;
        }catch (StoreException e) {
            return false;
        }

    }

    @Transactional
    public boolean followDecrease(FollowForm form) {
        try {
            Store store = storeRepository.findById(form.getStoreId())
                    .orElseThrow(() -> new StoreException(NOT_FOUND_STORE));
            store.decreaseFollow();
            return true;
        }catch (StoreException e) {
            return false;
        }
    }

    public List<StoreDto> getStores(StoresForm form) {
        List<Store> stores = storeRepository.findAllByIdIn(form.getFollowList());
        return stores.stream().map(StoreDto::from).collect(Collectors.toList());
    }
}
