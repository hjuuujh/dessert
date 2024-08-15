package com.zerobase.storeapi.service;

import com.zerobase.storeapi.domain.dto.ItemDto;
import com.zerobase.storeapi.domain.entity.Item;
import com.zerobase.storeapi.domain.entity.Option;
import com.zerobase.storeapi.domain.entity.Store;
import com.zerobase.storeapi.domain.form.item.CreateItem;
import com.zerobase.storeapi.domain.form.item.UpdateItem;
import com.zerobase.storeapi.domain.form.option.CreateOption;
import com.zerobase.storeapi.exception.StoreException;
import com.zerobase.storeapi.repository.StoreItemRepository;
import com.zerobase.storeapi.repository.StoreOptionRepository;
import com.zerobase.storeapi.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.zerobase.storeapi.exception.ErrorCode.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class StoreItemService {
    private final StoreRepository storeRepository;
    private final StoreItemRepository storeItemRepository;
    private final StoreOptionRepository storeOptionRepository;

    public ItemDto createItem(Long sellerId, CreateItem form) {
        Store store = checkMatchStoreAndSeller(form.getStoreId(), sellerId);

        checkItemName(store.getId(), form.getName());

        checkOptionName(form.getOptions());

        checkOptionPrice(form.getOptions());

        Item item = Item.of(sellerId, form);
        storeItemRepository.save(item);
        return ItemDto.from(item);
    }

    private Store checkMatchStoreAndSeller(Long storeId, Long sellerId) {
        return storeRepository.findByIdAndSellerId(storeId, sellerId)
                .orElseThrow(() -> new StoreException(UNMATCHED_SELLER_STORE));
    }

    private void checkOptionPrice(List<CreateOption> options) {
        List<Integer> optionPrices = options.stream().map(CreateOption::getRegularPrice).collect(Collectors.toList());
        List<Integer> optionDiscount = options.stream().map(CreateOption::getDiscount).collect(Collectors.toList());

        for (int i = 0; i < optionPrices.size(); i++) {
            if (optionPrices.get(i) <= 0) {
                String errorDescription = String.format("옵션 가격: %s", optionPrices.get(i));
                throw new StoreException(CHECK_OPTION_PRICE, errorDescription);
            }

            if (optionDiscount.get(i) > optionPrices.get(i)) {
                String errorDescription = String.format("옵션 가격: %s, 할인 가격: %s", optionPrices.get(i), optionDiscount.get(i));
                throw new StoreException(CHECK_OPTION_DISCOUNT, errorDescription);
            }
        }
    }

    private void checkOptionName(List<CreateOption> options) {
        List<String> optionNames = options.stream().map(CreateOption::getName).collect(Collectors.toList());
        if (optionNames.size() != Set.copyOf(optionNames).size()) {
            throw new StoreException(DUPLICATE_OPTION_NAME);
        }
    }

    private void checkItemName(Long storeId, String itemName) {
        if (storeItemRepository.existsByStoreIdAndName(storeId, itemName)) {
            throw new StoreException(DUPLICATE_ITEM_NAME);
        }
    }

    @Transactional
    public ItemDto updateItem(Long sellerId, UpdateItem form) {
        Store store = checkMatchStoreAndSeller(form.getStoreId(), sellerId);

        if (storeItemRepository.existsByStoreIdAndNameAndIdNot(store.getId(), form.getName(), form.getId())) {
            throw new StoreException(DUPLICATE_ITEM_NAME);
        }

        // 아이템 내용 업데이트
        Item item = storeItemRepository.findById(form.getId())
                .orElseThrow(() -> new StoreException(NOT_FOUND_ITEM));
        item.updateItem(form);

        // 옵션 업데이트 따로 안만들고 같이 진행
        // 옵션 이름 중복확인
        checkOptionName(form.getOptions());

        // 옵션 가격>0 확인
        checkOptionPrice(form.getOptions());

        // 기존 아이템의 옵션들 삭제
        storeOptionRepository.deleteAllByItemId(form.getId());

        // 새로운 옵션 만들어 아이템에 추가
        List<Option> newOptions = form.getOptions().stream()
                .map(Option::of)
                .collect(Collectors.toList());
        item.updateOptions(newOptions);

        // 옵션 테이블에 추가
        storeOptionRepository.saveAll(newOptions);

        return ItemDto.from(item);
    }

    public void deleteItem(Long sellerId, Long id) {
        Item item = storeItemRepository.findById(id)
                .orElseThrow(() -> new StoreException(NOT_FOUND_ITEM));

        checkMatchStoreAndSeller(item.getStoreId(), sellerId);

        storeItemRepository.delete(item);
    }
}
