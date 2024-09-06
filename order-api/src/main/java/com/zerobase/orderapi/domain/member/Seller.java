package com.zerobase.orderapi.domain.member;

import com.zerobase.orderapi.domain.BaseEntity;
import com.zerobase.orderapi.domain.order.Orders;
import lombok.*;
import org.hibernate.envers.AuditOverride;
import org.springframework.batch.core.configuration.annotation.JobScope;

import javax.persistence.*;
import javax.persistence.criteria.Order;
import javax.ws.rs.DefaultValue;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity(name = "seller")
@AuditOverride(forClass = BaseEntity.class)
public class Seller extends BaseEntity {
    // 파트너 entity
    // Spring Security 를 이용 : UserDetail 를 구현

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;
    private String password;
    private String name;
    private String phone;
    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> roles;

//    @OneToMany(fetch = FetchType.EAGER, mappedBy = "seller")
//    private Set<Orders> orders;

    @DefaultValue("0")
    private int income;

    public void updateIncome(int totalPrice) {
        this.income += totalPrice;
    }

    public void decreaseIncome(Integer totalPrice) {
        this.income -= totalPrice;
    }
}