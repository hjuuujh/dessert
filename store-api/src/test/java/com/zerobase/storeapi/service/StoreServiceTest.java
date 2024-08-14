package com.zerobase.storeapi.service;

import com.zerobase.storeapi.domain.dto.StoreDto;
import com.zerobase.storeapi.domain.entity.Store;
import com.zerobase.storeapi.domain.form.store.RegisterStore;
import com.zerobase.storeapi.domain.form.store.UpdateStore;
import com.zerobase.storeapi.exception.StoreException;
import com.zerobase.storeapi.repository.StoreRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.zerobase.storeapi.exception.ErrorCode.DUPLICATE_STORE_NAME;
import static com.zerobase.storeapi.exception.ErrorCode.UNMATCHED_SELLER_STORE;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class StoreServiceTest {

    @Mock
    private StoreRepository storeRepository;
    @InjectMocks
    private StoreService storeService;

    @Test
    void successRegisterStore() {
        //given
        RegisterStore form = RegisterStore.builder()
                .name("매장")
                .description("매장 설명")
                .thumbnailUrl("https://.jpg")
                .build();

        given(storeRepository.existsByName(anyString()))
                .willReturn(false);

        ArgumentCaptor<Store> captor = ArgumentCaptor.forClass(Store.class);

        //when
        StoreDto storeDto = storeService.registerStore(1L, form);

        //then
        verify(storeRepository, times(1)).save(captor.capture());
        assertEquals(form.getName(), captor.getValue().getName());
        assertEquals(form.getDescription(), captor.getValue().getDescription());
        assertEquals(form.getThumbnailUrl(), captor.getValue().getThumbnailUrl());
    }

    @Test
    void failRegisterStore_DUPLICATE_STORE_NAME() {
        //given
        RegisterStore form = RegisterStore.builder()
                .name("매장")
                .description("매장 설명")
                .thumbnailUrl("https://.jpg")
                .build();

        given(storeRepository.existsByName(anyString()))
                .willReturn(true);

        ArgumentCaptor<Store> captor = ArgumentCaptor.forClass(Store.class);

        //when
        StoreException exception = assertThrows(StoreException.class,
                () -> storeService.registerStore(1L, form));

        //then
        assertEquals(DUPLICATE_STORE_NAME, exception.getErrorCode());
        assertEquals("매장명은 중복일 수 없습니다.", exception.getErrorMessage());
    }

    @Test
    void successUpdateStore() {
        //given
        Store store = Store.builder()
                .id(1L)
                .name("매장")
                .description("매장 설명")
                .thumbnailUrl("https://.jpg")
                .build();

        UpdateStore form = UpdateStore.builder()
                .id(1L)
                .name("매장 이름 수정")
                .description("매장 정보 수정")
                .thumbnailUrl("매장 섬네일 수정")
                .build();

        given(storeRepository.existsByName(anyString()))
                .willReturn(false);

        given(storeRepository.findByIdAndSellerId(anyLong(), anyLong()))
                .willReturn(Optional.of(store));

        //when
        StoreDto storeDto = storeService.updateStore(1L, form);

        //then
        assertEquals(form.getId(), storeDto.getId());
        assertEquals(form.getName(), storeDto.getName());
        assertEquals(form.getDescription(), storeDto.getDescription());
        assertEquals(form.getThumbnailUrl(), storeDto.getThumbnailUrl());
    }

    @Test
    void failUpdateStore_UNMATCHED_SELLER_STORE() {
        //given
        UpdateStore form = UpdateStore.builder()
                .id(1L)
                .name("매장 이름 수정")
                .description("매장 정보 수정")
                .thumbnailUrl("매장 섬네일 수정")
                .build();

        given(storeRepository.existsByName(anyString()))
                .willReturn(false);

        given(storeRepository.findByIdAndSellerId(anyLong(), anyLong()))
                .willReturn(Optional.empty());

        //when
        StoreException exception = assertThrows(StoreException.class,
                () -> storeService.updateStore(1L, form));

        //then
        assertEquals(UNMATCHED_SELLER_STORE, exception.getErrorCode());
        assertEquals("매장 정보와 파트너 정보가 일치하지 않습니다.", exception.getErrorMessage());   //then
    }


    @Test
    void failUpdateStore_DUPLICATE_STORE_NAME() {
        //given
        UpdateStore form = UpdateStore.builder()
                .id(1L)
                .name("매장 이름 수정")
                .description("매장 정보 수정")
                .thumbnailUrl("매장 섬네일 수정")
                .build();

        given(storeRepository.existsByName(anyString()))
                .willReturn(true);

        //when
        StoreException exception = assertThrows(StoreException.class,
                () -> storeService.updateStore(1L, form));

        //then
        assertEquals(DUPLICATE_STORE_NAME, exception.getErrorCode());
        assertEquals("매장명은 중복일 수 없습니다.", exception.getErrorMessage());
    }

    @Test
    void successDeleteStore(){
        //given
        Store store = Store.builder()
                .id(1L)
                .sellerId(1L)
                .name("매장")
                .description("매장 설명")
                .thumbnailUrl("https://.jpg")
                .build();

        given(storeRepository.findByIdAndSellerId(anyLong(), anyLong()))
                .willReturn(Optional.of(store));
        //when
        boolean result = storeService.deleteStore(1L, 1L);

        //then
        assertTrue(result);
    }

    @Test
    void failDeleteStore_UNMATCHED_SELLER_STORE(){
        //given

        given(storeRepository.findByIdAndSellerId(anyLong(), anyLong()))
                .willReturn(Optional.empty());
        //when
        StoreException exception = assertThrows(StoreException.class,
                () -> storeService.deleteStore(1L, 1L));

        //then
        assertEquals(UNMATCHED_SELLER_STORE, exception.getErrorCode());
        assertEquals("매장 정보와 파트너 정보가 일치하지 않습니다.", exception.getErrorMessage());   //then
    }
}