package com.example.invenza.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.example.invenza.entity.Orders;
import com.example.invenza.entity.Procurement;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrdersDto {
    private Long id;
    private String commodityName;
    private String commodityType;
    private BigDecimal unitPrice;
    private double quantity;
    private BigDecimal totalCost;

    private String dealerName;
    private String dealerId;
    private String dealerEmail;
    private String dealerPhone;

    private LocalDateTime orderDate;
    private LocalDateTime deadlineDate;

    private String employeeName;
    private String employeeId;
    private String employeeEmail;
    private String employeePhone;
    public static OrdersDto of(Orders entity) {
        OrdersDto dto = new OrdersDto();
        dto.setId(entity.getId());
        dto.setCommodityName(entity.getCommodityName());
        dto.setCommodityType(entity.getCommodityType());
        dto.setUnitPrice(entity.getUnitPrice());
        dto.setQuantity(entity.getQuantity());
        dto.setTotalCost(entity.getTotalPrice());

        dto.setDealerName(entity.getDealerName());
        dto.setDealerId(entity.getDealerId());
        dto.setDealerEmail(entity.getDealerEmail());
        dto.setDealerPhone(entity.getDealerPhone());

        dto.setOrderDate(entity.getOrderDate());
        dto.setDeadlineDate(entity.getDeadlineDate());

        dto.setEmployeeName(entity.getEmployeeName());
        dto.setEmployeeId(entity.getEmployeeId());
        dto.setEmployeeEmail(entity.getEmployeeEmail());
        dto.setEmployeePhone(entity.getEmployeePhone());

        return dto;
    }
}
