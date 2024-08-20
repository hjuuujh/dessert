package com.zerobase.storeapi.service;

import com.zerobase.storeapi.client.MemberClient;
import com.zerobase.storeapi.client.RedisClient;
import com.zerobase.storeapi.controller.CartController;
import com.zerobase.storeapi.domain.entity.Option;
import com.zerobase.storeapi.domain.redis.Cart;
import com.zerobase.storeapi.exception.StoreException;
import com.zerobase.storeapi.repository.StoreItemOptionRepository;
import com.zerobase.storeapi.repository.StoreItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.zerobase.storeapi.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class CartOrderService {
    private final StoreItemRepository storeItemRepository;
    private final RedisClient redisClient;
    private final CartService cartService;
    private final MemberClient memberClient;
    private final StoreItemOptionRepository storeItemOptionRepository;

    public Cart orderCart(Long customerId, Cart cart) {
        Cart orderCart = cartService.refreshCart(cart);
        if (!orderCart.getMessages().isEmpty()) {
            System.out.println(orderCart.getMessages());
            // 문제가 있음
            throw new StoreException(ORDER_FAIL_CHECK_CART);
        }

        // 고객의 포인트 충분한지 확인
        checkEnoughBalance(customerId, cart);

        // 수량 충분한지 확인
        checkEnoughQuantity(cart);

        // 선택주문 : 주문하지 않은 아이템 장바구니에 남아야함

        // item id 별 -> option id별 정렬해서
        // 겹치는 경우 아이템 일부 주문시 남은 옵션 새 카트에 추가
        // 안겹치는 경우 == 주문안함 -> 새카트에 추가

        // item id로 정렬
        Cart oldCart = cartService.getCart(customerId);
        Collections.sort(oldCart.getItems());
        // option id로 정렬
        for (Cart.Item item : oldCart.getItems()) {
            Collections.sort(item.getOptions());
        }

        Collections.sort(orderCart.getItems());
        // option id로 정렬
        for (Cart.Item item : orderCart.getItems()) {
            Collections.sort(item.getOptions());
        }

        // 기존 장바구니에서 구매하지 않은 제품들 남겨두기 위해 새 카트 생성
        Cart newCart = new Cart(customerId, new ArrayList<>(), new ArrayList<>(), 0);

        // 기존 장바구니에서 선택 주문하기 때문에 orderCart의 size가 oldCart의 size보다 작음
        // > orderCart에 없는게 oldCart에는 있을 수 있지만 반대는 없음
        // -> orderCart size 기준으로 순회
        int i = 0;
        int j = 0;
        List<Cart.Item> newItems = new ArrayList<>();
        while (i < orderCart.getItems().size()) {
            // 하나씩 가져옴
            Cart.Item orderItem = orderCart.getItems().get(i);
            Cart.Item oldItem = oldCart.getItems().get(j);

            // 아이디 비교해서 주문안한 경우
            if (orderItem.getId() > oldItem.getId()) {
                // 새 카트에 추가
                newItems.add(oldItem);
                // 다음 item으로 비교
                j++;
                continue;
            }

            // 기존 장바구니에서 선택해서 주문 -> orderCart의 option size가 oldCart의 옵션 사이즈보다 작음
            // -> orderCart option 사이즈로 순회
            int k = 0;
            int l = 0;
            List<Cart.Option> newOptions = new ArrayList<>();
            while (k < orderItem.getOptions().size()) {
                // 하나씩 가져옴
                Cart.Option orderOption = orderItem.getOptions().get(k);
                Cart.Option oldOption = oldItem.getOptions().get(l);

                // 아이디 비교해서 주문안한 옵션 있으면
                // 새카트에 추가하고 기존 아이템에서 다음 옵션 가져옴
                if (orderOption.getId() < oldOption.getId()) {
                    newOptions.add(oldOption);
                    k++;
                    continue;
                }

                // db의 잔여 수량 수정
                // orderapi 개발하면서 @Transactional 필요
                // 결제실패시 되돌리는 코드 필요
                Option option = storeItemOptionRepository.findById(orderOption.getId())
                        .orElseThrow(() -> new StoreException(NOT_FOUND_ITEM));

                option.decreaseQuantity(orderOption.getQuantity());


                try {
                    // 남은 옵션이 있는 경우
                    oldItem.getOptions().get(l + 1);
                    l++;
                } catch (Exception e) {
                    // 남은 옵션이 없는 경우
                    k++;
                }

            }

            Cart.Item newItem = Cart.Item.builder()
                    .id(orderItem.getId())
                    .storeId(orderItem.getStoreId())
                    .storeName(orderItem.getStoreName())
                    .name(orderItem.getName())
                    .options(newOptions)
                    .build();

            newItems.add(newItem);

            i++;
            j++;
        }

        int len = newItems.size();
        List<Cart.Item> finalCartProducts = new ArrayList<>();
        for (int m = 0; m < len; m++) {
            if (!newItems.get(m).getOptions().isEmpty()) {
                finalCartProducts.add(newItems.get(m));
            }
        }

        newCart.setItems(finalCartProducts);
        newCart.setTotalPrice(cartService.getTotalPrice(newCart));

        redisClient.put(customerId, newCart);
        return newCart;
    }

    private void checkEnoughQuantity(Cart cart) {
        cart.getItems().forEach(item -> {
            item.getOptions().forEach(option -> {
                Option remainQuantity = storeItemOptionRepository.findById(option.getId())
                        .orElseThrow(() -> new StoreException(NOT_FOUND_ITEM));
                if (option.getQuantity() > remainQuantity.getQuantity()) {
                    throw new StoreException(ITEM_QUANTITY_NOT_ENOUGH);
                }
            });
        });
    }

    private void checkEnoughBalance(Long customerId, Cart cart) {
        int totalPrice = cart.getTotalPrice();
//        Long balance = memberClient.getBalance(customerId);
        long balance = 10000000L;
        if (balance < totalPrice) {
            throw new StoreException(ORDER_FAIL_NO_MONEY);
        }
    }


}

