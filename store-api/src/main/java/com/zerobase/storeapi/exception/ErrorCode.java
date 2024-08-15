package com.zerobase.storeapi.exception;


import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error"),

    // 매장 등록
    DUPLICATE_STORE_NAME(HttpStatus.BAD_REQUEST, "매장명은 중복일 수 없습니다."),

    // 매장 수정
    NOT_FOUND_STORE(HttpStatus.BAD_REQUEST, "매장 정보가 존재하지 않습니다."),
    NOT_FOUND_ITEM(HttpStatus.BAD_REQUEST, "아이템정보가 존재하지 않습니다."),

    UNMATCHED_SELLER_STORE(HttpStatus.BAD_REQUEST, "매장 정보와 파트너 정보가 일치하지 않습니다."),
    // 매장 삭제
    // 매장 검색
    // 아이템 등록
    DUPLICATE_ITEM_NAME(HttpStatus.BAD_REQUEST, "아이템명은 중복일 수 없습니다."),
    CHECK_ITEM_PRICE(HttpStatus.BAD_REQUEST, "가격을 확인해주세요."),

    // 옵션 등록
    DUPLICATE_OPTION_NAME(HttpStatus.BAD_REQUEST, "옵션명은 중복일 수 없습니다."),
    CHECK_OPTION_PRICE(HttpStatus.BAD_REQUEST, "가격을 확인해주세요."),
    CHECK_OPTION_QUANTITY(HttpStatus.BAD_REQUEST, "수량을 확인해주세요."),
    CHECK_OPTION_DISCOUNT(HttpStatus.BAD_REQUEST, "할인금액이 가격보다 큽니다.");


    private final HttpStatus httpStatus;
    private final String description;
}