package com.example.invenza.dto;

import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class ProcurementDto {
    private Long id;
    private String commodityName;
    private String commodityType;
    private BigDecimal unitPrice;
    private int quantity;
    private BigDecimal totalCost;

    private String supplierName;
    private String supplierId;
    private String supplierEmail;
    private String supplierPhone;

    private LocalDate orderDate;
    private LocalDate deadlineDate;

    private String employeeName;
    private String employeeId;
    private String employeeEmail;
    private String employeePhone;
}

