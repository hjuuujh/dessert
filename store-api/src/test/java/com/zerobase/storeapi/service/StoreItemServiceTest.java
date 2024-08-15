package com.zerobase.storeapi.service;

import com.zerobase.storeapi.domain.dto.ItemDto;
import com.zerobase.storeapi.domain.entity.Item;
import com.zerobase.storeapi.domain.entity.Option;
import com.zerobase.storeapi.domain.entity.Store;
import com.zerobase.storeapi.domain.form.item.UpdateItem;
import com.zerobase.storeapi.domain.form.option.CreateOption;
import com.zerobase.storeapi.exception.StoreException;
import com.zerobase.storeapi.repository.StoreItemRepository;
import com.zerobase.storeapi.repository.StoreOptionRepository;
import com.zerobase.storeapi.repository.StoreRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.zerobase.storeapi.exception.ErrorCode.DUPLICATE_ITEM_NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class StoreItemServiceTest {
    @Mock
    private StoreRepository storeRepository;
    @Mock
    private StoreItemRepository storeItemRepository;
    @Mock
    private StoreOptionRepository storeOptionRepository;
    @InjectMocks
    private StoreItemService storeItemService;

//    @Test
//    void successCreateItem(){
//        //given
//        CreateItem form = CreateItem.builder()
//                .storeId(1L)
//                .name("아이템")
//                .thumbnailUrl("thumbnail.jpg")
//                .description("아이템 설명")
//                .descriptionUrl("description.jpg")
//                .options(getOptions())
//                .build();
//
//        Store store = Store.builder()
//                .sellerId(1L)
//                .build();
//        given(storeRepository.findByIdAndSellerId(anyLong(), anyLong()))
//                .willReturn(Optional.ofNullable(store));
//
//        A
//    void successCreateItem(){
//        //given
//        CreateItem form = CreateItem.builder()
//                .storeId(1L)
//                .name("아이템")
//                .thumbnailUrl("thumbnail.jpg")
//                .description("아이템 설명")
//                .descriptionUrl("description.jpg")
//                .options(getOptions())
//                .build();
//
//        Store store = Store.builder()
//                .sellerId(1L)
//                .build();
//        given(storeRepository.findByIdAndSellerId(anyLong(), anyLong()))
//                .willReturn(Optional.ofNullable(store));
//
//        ArgumentCaptor<Item> captor = ArgumentCaptor.forClass(Item.class);
//
//        //when
//        ItemDto item = storeItemService.createItem(1L, form);
//
//        //then
//        verify(storeItemRepository, times(1)).save(captor.capture());
//        assertEquals("아이템", captor.getValue().getName());
//        assertEquals("아이템 설명", captor.getValue().getDescription());
//        assertEquals(getOptions().get(0).getPrice(), captor.getValue().getPrice());
//    }
//
//    @Test
//    void failCreateItem_DUPLICATE_ITEM_NAME(){
//        //given
//        CreateItem form = CreateItem.builder()
//                .storeId(1L)
//                .name("아이템")
//                .thumbnailUrl("thumbnail.jpg")
//                .description("아이템 설명")
//                .descriptionUrl("description.jpg")
//                .options(getOptions())
//                .build();
//
//        Store store = Store.builder()
//                .sellerId(1L)
//                .build();
//        given(storeRepository.findByIdAndSellerId(anyLong(), anyLong()))
//                .willReturn(Optional.ofNullable(store));
//
//        given(storeItemRepository.existsByName(anyString()))
//                .willReturn(true);
//
//        //when
//        StoreException exception = assertThrows(StoreException.class,
//                ()->storeItemService.createItem(1L, form));
//
//        //then
//        assertEquals(DUPLICATE_ITEM_NAME, exception.getErrorCode());
//        assertEquals("아이템명은 중복일 수 없습니다.", exception.getErrorMessage());
//    }
//
//    @Test
//    void failCreateItem_DUPLICATE_OPTION_NAME(){
//        //given
//        CreateItem form = CreateItem.builder()
//                .storeId(1L)
//                .name("아이템")
//                .thumbnailUrl("thumbnail.jpg")
//                .description("아이템 설명")
//                .descriptionUrl("description.jpg")
//                .options(getOptions())
//                .build();
//
//        Store store = Store.builder()
//                .sellerId(1L)
//                .build();
//        given(storeRepository.findByIdAndSellerId(anyLong(), anyLong()))
//                .willReturn(Optional.ofNullable(store));
//
//        given(storeOptionRepository.existsByName(anyString()))
//                .willReturn(true);
//
//        //when
//        StoreException exception = assertThrows(StoreException.class,
//                ()->storeItemService.createItem(1L, form));
//
//        //then
//        assertEquals(DUPLICATE_OPTION_NAME, exception.getErrorCode());
//        assertEquals("옵션명은 중복일 수 없습니다.", exception.getErrorMessage());
//    }
//    @Test
//    void failCreateItem_CHECK_OPTION_PRICE(){
//        //given
//        List<CreateOption> options = getOptions();
//        options.get(0).setPrice(0);
//
//        CreateItem form = CreateItem.builder()
//                .storeId(1L)
//                .name("아이템")
//                .thumbnailUrl("thumbnail.jpg")
//                .description("아이템 설명")
//                .descriptionUrl("description.jpg")
//                .options(options)
//                .build();
//
//        Store store = Store.builder()
//                .sellerId(1L)
//                .build();
//        given(storeRepository.findByIdAndSellerId(anyLong(), anyLong()))
//                .willReturn(Optional.ofNullable(store));
//
//        //when
//        StoreException exception = assertThrows(StoreException.class,
//                ()->storeItemService.createItem(1L, form));
//
//        //then
//        assertEquals(CHECK_OPTION_PRICE, exception.getErrorCode());
//        assertEquals("가격을 확인해주세요.", exception.getErrorMessage());
//    }

    @Test
    void successUpdateOption() {
        //given
        List<CreateOption> createOptions = getCreateOptions();
        createOptions.get(0).setName("옵션 수정");
        createOptions.get(0).setQuantity(500);
        createOptions.get(0).setRegularPrice(9000);
        createOptions.get(0).setDiscount(1000);
        UpdateItem form = UpdateItem.builder()
                .id(1L)
                .storeId(1L)
                .name("아이템 수정")
                .thumbnailUrl("url")
                .descriptionUrl("url")
                .description("설명 수정")
                .options(createOptions)
                .build();

        Store store = Store.builder()
                .id(1L)
                .sellerId(1L)
                .build();

        given(storeRepository.findByIdAndSellerId(anyLong(), anyLong()))
                .willReturn(Optional.of(store));
        given(storeItemRepository.existsByStoreIdAndNameAndIdNot(anyLong(), anyString(), anyLong()))
                .willReturn(false);


        Item item = Item.builder()
                .id(1L)
                .storeId(1L)
                .name("아이템")
                .thumbnailUrl("url")
                .descriptionUrl("url")
                .description("설명")
                .options(getOptions()).build();
        given(storeItemRepository.findById(anyLong()))
                .willReturn(Optional.ofNullable(item));
        ArgumentCaptor<Long> captor = ArgumentCaptor.forClass(Long.class);

        //when
        ItemDto newItem = storeItemService.updateItem(1L, form);

        //then
        verify(storeOptionRepository, times(1)).deleteAllByItemId(captor.capture());
        assertEquals("아이템 수정", newItem.getName());
        assertEquals("설명 수정", newItem.getDescription());
        assertEquals("옵션 수정", newItem.getOptions().get(0).getName());
        assertEquals(500, newItem.getOptions().get(0).getQuantity());
        assertEquals(9000, newItem.getOptions().get(0).getRegularPrice());
        assertEquals(1000, newItem.getOptions().get(0).getDiscount());
    }

    @Test
    void failUpdateOption_DUPLICATE_ITEM_NAME() {
        //given
        List<CreateOption> createOptions = getCreateOptions();
        createOptions.get(0).setName("옵션 수정");
        createOptions.get(0).setQuantity(500);
        createOptions.get(0).setRegularPrice(9000);
        createOptions.get(0).setDiscount(1000);
        UpdateItem form = UpdateItem.builder()
                .id(1L)
                .storeId(1L)
                .name("아이템 수정")
                .thumbnailUrl("url")
                .descriptionUrl("url")
                .description("설명 수정")
                .options(createOptions)
                .build();

        Store store = Store.builder()
                .id(1L)
                .sellerId(1L)
                .build();

        given(storeRepository.findByIdAndSellerId(anyLong(), anyLong()))
                .willReturn(Optional.of(store));
        given(storeItemRepository.existsByStoreIdAndNameAndIdNot(anyLong(), anyString(), anyLong()))
                .willReturn(true);

        //when
        StoreException exception = assertThrows(StoreException.class,
                () -> storeItemService.updateItem(1L, form));

        //then
        assertEquals(DUPLICATE_ITEM_NAME, exception.getErrorCode());
        assertEquals("아이템명은 중복일 수 없습니다.", exception.getErrorMessage());
    }

    @Test
    void successDeleteItem(){
        //given
        Item item = Item.builder()
                .id(1L)
                .storeId(1L)
                .name("아이템")
                .thumbnailUrl("url")
                .descriptionUrl("url")
                .description("설명")
                .options(getOptions()).build();
        given(storeItemRepository.findById(anyLong()))
                .willReturn(Optional.ofNullable(item));

        Store store = Store.builder()
                .id(1L)
                .sellerId(1L)
                .build();

        given(storeRepository.findByIdAndSellerId(anyLong(), anyLong()))
                .willReturn(Optional.of(store));

        //when
        storeItemService.deleteItem(1L, 1L);

        //then
        verify(storeItemRepository, times(1)).delete(any());
    }


    private List<CreateOption> getCreateOptions() {
        List<CreateOption> options = new ArrayList<>();
        options.add(CreateOption.builder()
                .name("옵션1")
                .quantity(10)
                .regularPrice(1000)
                .discount(500)
                .build());
        options.add(CreateOption.builder()
                .name("옵션2")
                .quantity(20)
                .regularPrice(2000)
                .discount(1000)
                .build());
        options.add(CreateOption.builder()
                .name("옵션3")
                .quantity(30)
                .regularPrice(3000)
                .discount(2100)
                .build());
        return options;
    }

    private List<Option> getOptions() {
        List<Option> options = new ArrayList<>();
        options.add(Option.builder()
                .name("옵션1")
                .quantity(10)
                .regularPrice(1000)
                .discount(500)
                .build());
        options.add(Option.builder()
                .name("옵션2")
                .quantity(20)
                .regularPrice(2000)
                .discount(1000)
                .build());
        options.add(Option.builder()
                .name("옵션3")
                .quantity(30)
                .regularPrice(3000)
                .discount(2100)
                .build());
        return options;
    }
}