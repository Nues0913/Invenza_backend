package com.example.invenza.util;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 測試工具類 - 提供測試數據建立的便利方法
 */
public class TestDataBuilder {

    public static Procurement.Builder procurementBuilder() {
        return new Procurement.Builder();
    }

    public static ProcurementDto.Builder procurementDtoBuilder() {
        return new ProcurementDto.Builder();
    }

    /**
     * Procurement 建構者
     */
    public static class Procurement {
        public static class Builder {
            private final com.example.invenza.entity.Procurement procurement = new com.example.invenza.entity.Procurement();

            public Builder id(Long id) {
                procurement.setId(id);
                return this;
            }

            public Builder commodityName(String name) {
                procurement.setCommodityName(name);
                return this;
            }

            public Builder commodityType(String type) {
                procurement.setCommodityType(type);
                return this;
            }

            public Builder unitPrice(BigDecimal price) {
                procurement.setUnitPrice(price);
                return this;
            }

            public Builder quantity(Integer quantity) {
                procurement.setQuantity(quantity);
                return this;
            }

            public Builder supplierName(String name) {
                procurement.setSupplierName(name);
                return this;
            }

            public Builder supplierId(String id) {
                procurement.setSupplierId(id);
                return this;
            }

            public Builder supplierEmail(String email) {
                procurement.setSupplierEmail(email);
                return this;
            }

            public Builder supplierPhone(String phone) {
                procurement.setSupplierPhone(phone);
                return this;
            }

            public Builder orderDate(LocalDateTime date) {
                procurement.setOrderDate(date);
                return this;
            }

            public Builder deadlineDate(LocalDateTime date) {
                procurement.setDeadlineDate(date);
                return this;
            }

            public Builder withDefaults() {
                return this
                    .id(1L)
                    .commodityName("測試商品")
                    .commodityType("測試類型")
                    .unitPrice(new BigDecimal("1000"))
                    .quantity(10)
                    .supplierName("測試供應商")
                    .supplierId("SUP001")
                    .supplierEmail("test@supplier.com")
                    .supplierPhone("02-1234-5678")
                    .orderDate(LocalDateTime.now().minusDays(1))
                    .deadlineDate(LocalDateTime.now().plusDays(30));
            }

            public com.example.invenza.entity.Procurement build() {
                return procurement;
            }
        }
    }

    /**
     * ProcurementDto 建構者
     */
    public static class ProcurementDto {
        public static class Builder {
            private final com.example.invenza.dto.ProcurementDto dto = new com.example.invenza.dto.ProcurementDto();

            public Builder id(Long id) {
                dto.setId(id);
                return this;
            }

            public Builder commodityName(String name) {
                dto.setCommodityName(name);
                return this;
            }

            public Builder commodityType(String type) {
                dto.setCommodityType(type);
                return this;
            }

            public Builder unitPrice(BigDecimal price) {
                dto.setUnitPrice(price);
                return this;
            }

            public Builder quantity(Integer quantity) {
                dto.setQuantity(quantity);
                return this;
            }

            public Builder supplierName(String name) {
                dto.setSupplierName(name);
                return this;
            }

            public Builder supplierId(String id) {
                dto.setSupplierId(id);
                return this;
            }

            public Builder employeeName(String name) {
                dto.setEmployeeName(name);
                return this;
            }

            public Builder employeeId(String id) {
                dto.setEmployeeId(id);
                return this;
            }

            public Builder withDefaults() {
                return this
                    .id(1L)
                    .commodityName("測試商品")
                    .commodityType("測試類型")
                    .unitPrice(new BigDecimal("1000"))
                    .quantity(10)
                    .supplierName("測試供應商")
                    .supplierId("SUP001")
                    .employeeName("測試員工")
                    .employeeId("EMP001");
            }

            public com.example.invenza.dto.ProcurementDto build() {
                return dto;
            }
        }
    }
}
