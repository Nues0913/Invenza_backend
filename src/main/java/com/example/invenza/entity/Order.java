package com.example.invenza.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "order")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String commodityName;
    private String commodityType;


    private String dealerName;
    private String dealerId;
    private String dealerEmail;
    private String dealerPhone;

    private BigDecimal unitPrice;
    private int quantity;

    // 不存入資料庫，動態計算
    @Transient
    public BigDecimal getTotalPrice() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }

    private LocalDate orderDate;
    private LocalDate deadlineDate;
}
