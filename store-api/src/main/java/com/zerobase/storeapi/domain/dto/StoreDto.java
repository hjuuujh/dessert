package com.zerobase.storeapi.domain.dto;

import com.zerobase.storeapi.domain.entity.Store;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoreDto {
    private Long id;
    private Long sellerId;
    private String name;
    private String description;
    private String thumbnailUrl;

    private int followCount;

    private boolean deleted;

    public static StoreDto from(Store store) {
        return StoreDto.builder()
                .id(store.getId())
                .sellerId(store.getSellerId())
                .name(store.getName())
                .description(store.getDescription())
                .thumbnailUrl(store.getThumbnailUrl())
                .followCount(store.getFollowCount())
                .deleted(store.isDeleted())
                .build();
    }

}
