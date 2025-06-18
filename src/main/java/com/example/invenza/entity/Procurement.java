package com.example.invenza.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.Setter;

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

    private LocalDateTime orderDate;
    private LocalDateTime deadlineDate;

    private String employeeName;
    private String employeeId;
    private String employeeEmail;
    private String employeePhone;
}
