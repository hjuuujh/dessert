package com.zerobase.storeapi.service;

import com.zerobase.storeapi.domain.dto.StoreDto;
import com.zerobase.storeapi.domain.entity.Store;
import com.zerobase.storeapi.repository.StoreRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(MockitoExtension.class)
class StoreSearchServiceTest {
    @Mock
    private StoreRepository storeRepository;
    @InjectMocks
    private StoreSearchService storeSearchService;

    @Test
    void searchStoreByKeyword(){
        //given
        List<Store> stores = getStores();
        List<Store> searchedStores = stores.stream().filter(store -> !store.isDeleted() && store.getName().contains("1"))
                .collect(Collectors.toList());
        PageRequest pageable = PageRequest.of(0, 10);
        Page<Store> page = new PageImpl<>(searchedStores);
        given(storeRepository.findByNameContainingIgnoreCaseAndDeleted(anyString(), anyBoolean(), any()))
                .willReturn(page);
        //when
        Page<StoreDto> storeDtos = storeSearchService.searchStoreByKeyword("1", pageable);

        //then
        assertEquals(2, storeDtos.getTotalElements());
    }

    @Test
    void searchStoreByFollowOrder(){
        //given
        List<Store> stores = getStores();
        List<Store> searchedStores = stores.stream().filter(store -> !store.isDeleted() && store.getName().contains("1"))
                .sorted(Comparator.comparing(Store::getFollowCount).reversed())
                .collect(Collectors.toList());

        PageRequest pageable = PageRequest.of(0, 10);
        Page<Store> page = new PageImpl<>(searchedStores);
        given(storeRepository.findByNameContainingIgnoreCaseAndDeleted(anyString(), anyBoolean(), any()))
                .willReturn(page);
        //when
        Page<StoreDto> storeDtos = storeSearchService.searchStoreByKeyword("1", pageable);

        //then
        assertEquals(2, storeDtos.getTotalElements());
        assertEquals("매장11", storeDtos.getContent().get(0).getName());
        assertEquals("매장1", storeDtos.getContent().get(1).getName());
    }

    @Test
    void searchStore(){
        //given
        List<Store> stores = getStores();
        Store store = stores.get(0);
        given(storeRepository.findById(anyLong()))
                .willReturn(Optional.ofNullable(store));
        //when
        StoreDto storeDto = storeSearchService.searchStore(1L);

        //then
        assertEquals("매장1", storeDto.getName());
        assertEquals("매장1 설명", storeDto.getDescription());
    }

    private List<Store> getStores(){
        List<Store> stores = new ArrayList<>();
        stores.add(Store.builder()
                .id(1L)
                .name("매장1")
                .description("매장1 설명")
                .followCount(20)
                .deleted(false)
                .build());
        stores.add(Store.builder()
                .id(1L)
                .name("매장2")
                .description("매장1 설명")
                .followCount(10)
                .deleted(true)
                .build());
        stores.add(Store.builder()
                .id(1L)
                .name("매장3")
                .description("매장1 설명")
                .followCount(20)
                .deleted(false)
                .build());
        stores.add(Store.builder()
                .id(1L)
                .name("매장11")
                .description("매장1 설명")
                .followCount(100)
                .deleted(false)
                .build());

        return stores;
    }
}