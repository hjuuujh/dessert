package com.zerobase.storeapi.domain.entity;

import com.zerobase.storeapi.domain.BaseEntity;
import com.zerobase.storeapi.domain.form.item.CreateItem;
import com.zerobase.storeapi.domain.form.item.UpdateItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.envers.AuditOverride;

import javax.persistence.*;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@AuditOverride(forClass = BaseEntity.class)
public class Item extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long sellerId;
    private Long storeId;

    private String name;
    private String thumbnailUrl;
    private String description;
    private String descriptionUrl;

    private int price;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "item_id")
    private List<Option> options;

    @ColumnDefault("0")
    private float rating; // 매장 별점

    // 별점 업데이트 할때마다 리뷰테이블 전체읽어서 정보 얻어오는 것보다
    // 리뷰 총 개수와 별점 총합 저장해두는게 효율적이라 생각함
    @ColumnDefault("0")
    private float ratingSum; // 아이템에 등록된 후기들의 별점 총합
    @ColumnDefault("0")
    private long ratingCount; // 아이템에 등록된 후기들의 개수 총합

    @ColumnDefault("0")
    private long orderCount;
    @ColumnDefault("0")
    private long heartCount;

    public static Item of(Long sellerId, CreateItem form){
        return Item.builder()
                .sellerId(sellerId)
                .storeId(form.getStoreId())
                .name(form.getName())
                .thumbnailUrl(form.getThumbnailUrl())
                .description(form.getDescription())
                .descriptionUrl(form.getDescriptionUrl())
                .price(form.getOptions().get(0).getRegularPrice()-form.getOptions().get(0).getDiscount()) // 첫번째 옵션의 가격을 아이템의 가격으로 이용
                .options(form.getOptions().stream()
                        .map(Option::of)
                        .collect(Collectors.toList()))
                .build();
    }

    public void updateItem(UpdateItem form) {
        this.name = form.getName();
        this.thumbnailUrl = form.getThumbnailUrl();
        this.description = form.getDescription();
        this.descriptionUrl = form.getDescriptionUrl();
    }

    public void updateOptions(List<Option> newOptions) {
        this.options = newOptions;
    }

    public void increaseHeart() {
        heartCount++;
    }

    public void decreaseHeart() {
        this.heartCount = Math.max(heartCount - 1, 0);
    }
}
