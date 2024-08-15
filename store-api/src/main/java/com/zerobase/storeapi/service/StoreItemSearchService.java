package com.zerobase.storeapi.service;

import com.zerobase.storeapi.domain.dto.ItemDto;
import com.zerobase.storeapi.domain.dto.StoreDto;
import com.zerobase.storeapi.domain.entity.Store;
import com.zerobase.storeapi.exception.StoreException;
import com.zerobase.storeapi.repository.StoreItemRepository;
import com.zerobase.storeapi.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import static com.zerobase.storeapi.exception.ErrorCode.NOT_FOUND_STORE;

@Service
@Slf4j
@RequiredArgsConstructor
public class StoreItemSearchService {
    private final StoreItemRepository storeItemRepository;

    // 기본 : 키워드 + 판매순
    public Page<ItemDto> searchItemByKeyword(String keyword, Pageable pageable) {
        return storeItemRepository.findByNameContainingIgnoreCaseOrderByOrderCountDesc(keyword, pageable)
                .map(ItemDto::from);
    }

    // 키워드 + 최신순
    public Page<ItemDto> searchItemByNewest(String keyword, Pageable pageable) {
        return storeItemRepository.findByNameContainingIgnoreCaseOrderByModifiedAtDesc(keyword, pageable)
                .map(ItemDto::from);
    }

    // 키워드 + 별점순
    public Page<ItemDto> searchItemByRating(String keyword, Pageable pageable) {
        return storeItemRepository.findByNameContainingIgnoreCaseOrderByRatingDesc(keyword, pageable)
                .map(ItemDto::from);
    }

    // 키워드 + 후기개수순
    public Page<ItemDto> searchItemByReview(String keyword, Pageable pageable) {
        return storeItemRepository.findByNameContainingIgnoreCaseOrderByRatingCountDesc(keyword, pageable)
                .map(ItemDto::from);
    }

    // 키워드 + 낮은 가격순
    public Page<ItemDto> searchItemByLowerPrice(String keyword, Pageable pageable) {
        return storeItemRepository.findByNameContainingIgnoreCaseOrderByPrice(keyword, pageable)
                .map(ItemDto::from);
    }

    // 키워드 + 높은 가격순
    public Page<ItemDto> searchItemByHighPrice(String keyword, Pageable pageable) {
        return storeItemRepository.findByNameContainingIgnoreCaseOrderByPriceDesc(keyword, pageable)
                .map(ItemDto::from);
    }

    public Page<ItemDto> searchItem(Long storeId, Pageable pageable) {
        return storeItemRepository.findByStoreId(storeId, pageable)
                .map(ItemDto::from);
    }
}
