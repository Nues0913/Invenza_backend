package com.example.invenza.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "procurement")
public class Procurement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String commodityName;
    private String commodityType;

    private BigDecimal unitPrice;
    private int quantity;

    // 不存入資料庫，動態計算
    @Transient
    public BigDecimal getTotalCost() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }

    private String supplierName;
    private String supplierId;
    private String supplierEmail;
    private String supplierPhone;

    private LocalDate orderDate;
    private LocalDate deadlineDate;
}
