package com.zerobase.orderapi.domain;

import com.zerobase.orderapi.domain.type.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.envers.AuditOverride;

import javax.persistence.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@AuditOverride(forClass = BaseEntity.class)
public class Orders extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long customerId;
    private Long sellerId;
    private Long storeId;

    private Long itemId;
    private Long optionId;
    private Integer price;
    private Integer quantity;

    @Enumerated(EnumType.STRING)
    private Status status;

    public void updateStatus(Status status) {
        this.status = status;
    }
}


