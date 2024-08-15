package com.zerobase.storeapi.domain.dto;

import com.zerobase.storeapi.domain.entity.Item;
import com.zerobase.storeapi.domain.entity.Option;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.CascadeType;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemDto {
    private Long id;
    private Long storeId;

    private String name;
    private String thumbnailUrl;
    private String description;
    private String descriptionUrl;

    private int price;

    private List<OptionDto> options;

    private float rating;

    private float ratingSum;
    private long ratingCount;

    private long orderCount;
    private long heartCount;

    public static ItemDto from(Item item) {
        List<OptionDto> options = item.getOptions()
                .stream().map(OptionDto::from).collect(Collectors.toList());
        return ItemDto.builder()
                .id(item.getId())
                .storeId(item.getStoreId())
                .name(item.getName())
                .thumbnailUrl(item.getThumbnailUrl())
                .description(item.getDescription())
                .descriptionUrl(item.getDescriptionUrl())
                .options(options)
                .rating(item.getRating())
                .orderCount(item.getOrderCount())
                .heartCount(item.getHeartCount())
                .price(item.getPrice())
                .build();
    }

}
