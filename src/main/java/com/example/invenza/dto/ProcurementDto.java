package com.example.invenza.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.example.invenza.entity.Procurement;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProcurementDto {
    private Long id;
    private String commodityName;
    private String commodityType;
    private BigDecimal unitPrice;
    private double quantity;
    private BigDecimal totalCost;

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
    
    public static ProcurementDto of(Procurement entity) {
        ProcurementDto dto = new ProcurementDto();
        dto.setId(entity.getId());
        dto.setCommodityName(entity.getCommodityName());
        dto.setCommodityType(entity.getCommodityType());
        dto.setUnitPrice(entity.getUnitPrice());
        dto.setQuantity(entity.getQuantity());
        dto.setTotalCost(entity.getTotalCost());

        dto.setSupplierName(entity.getSupplierName());
        dto.setSupplierId(entity.getSupplierId());
        dto.setSupplierEmail(entity.getSupplierEmail());
        dto.setSupplierPhone(entity.getSupplierPhone());

        dto.setOrderDate(entity.getOrderDate());
        dto.setDeadlineDate(entity.getDeadlineDate());

        dto.setEmployeeName(entity.getEmployeeName());
        dto.setEmployeeId(entity.getEmployeeId());
        dto.setEmployeeEmail(entity.getEmployeeEmail());
        dto.setEmployeePhone(entity.getEmployeePhone());

        return dto;
    }
}




