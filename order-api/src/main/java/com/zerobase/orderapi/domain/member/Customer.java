package com.zerobase.orderapi.domain.member;

import com.zerobase.orderapi.domain.BaseEntity;
import lombok.*;
import org.hibernate.envers.AuditOverride;

import javax.persistence.*;
import javax.ws.rs.DefaultValue;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity(name = "customer")
@AuditOverride(forClass = BaseEntity.class)
public class Customer extends BaseEntity{
    // 고객 entity
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

    @DefaultValue("0")
    private int balance;

    @ElementCollection(fetch = FetchType.EAGER)
    private Set<Long> followList;
    @ElementCollection(fetch = FetchType.EAGER)
    private Set<Long> heartList;

    public void decreaseBalance(int totalPrice) {
        this.balance -= totalPrice;
    }

    public void increaseBalance(Integer totalPrice) {
        this.balance += totalPrice;
    }
}