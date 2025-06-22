package com.example.invenza.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.Optional;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.example.invenza.dto.OrdersDto;
import com.example.invenza.dto.OrdersDto;
import com.example.invenza.entity.Orders;
import com.example.invenza.entity.Orders;
import com.example.invenza.repository.OrdersRepository;

import jakarta.persistence.criteria.Predicate;
import lombok.val;

@Service
public class OrdersService {
    private final OrdersRepository ordersRepository;

    public OrdersService(OrdersRepository ordersRepository) {
        this.ordersRepository = ordersRepository;
    }

    public List<OrdersDto> getUndueOrders() {
        LocalDateTime now = LocalDateTime.now();
        List<Orders> orders =  ordersRepository.findByDeadlineDateAfter(now);
        return orders.stream()
        .map(OrdersDto::of)
        .collect(Collectors.toList());
    }

    public List<OrdersDto> getUndueOrders(Map<String, String> allParams) {
        if(allParams == null || allParams.isEmpty()) {
            return getUndueOrders();
        }
        Specification<Orders> spec = (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

            // base predicates
            if (allParams.containsKey("commodityName") && allParams.get("commodityName") != null) {
                predicates.add(builder.equal(root.get("commodityName"), allParams.get("commodityName")));
            }
            if (allParams.containsKey("commodityType") && allParams.get("commodityType") != null) {
                predicates.add(builder.equal(root.get("commodityType"), allParams.get("commodityType")));
            }

            // businessPartner predicates
            if (allParams.containsKey("businessPartner") && allParams.get("businessPartner") != null) {
                predicates.add(builder.equal(root.get("dealerName"), allParams.get("businessPartner")));
            }
            if (allParams.containsKey("businessPartnerId") && allParams.get("businessPartnerId") != null) {
                predicates.add(builder.equal(root.get("dealerId"), allParams.get("businessPartnerId")));
            }

            // responsible predicates
            if (allParams.containsKey("responsible") && allParams.get("responsible") != null) {
                predicates.add(builder.equal(root.get("employeeName"), allParams.get("responsible")));
            }
            if (allParams.containsKey("responsibleId") && allParams.get("responsibleId") != null) {
                predicates.add(builder.equal(root.get("employeeId"), allParams.get("responsibleId")));
            }

            // LocalDateTime predicates
            if (allParams.containsKey("orderTimeStart") && allParams.get("orderTimeStart") != null) {
                predicates.add(builder.greaterThanOrEqualTo(root.get("orderDate"), LocalDateTime.parse(allParams.get("orderTimeStart"), dateTimeFormatter)));
            }
            if (allParams.containsKey("orderTimeEnd") && allParams.get("orderTimeEnd") != null) {
                predicates.add(builder.lessThanOrEqualTo(root.get("orderDate"), LocalDateTime.parse(allParams.get("orderTimeEnd"), dateTimeFormatter)));
            }
            if (allParams.containsKey("deadlineStart") && allParams.get("deadlineStart") != null) {
                predicates.add(builder.greaterThanOrEqualTo(root.get("deadlineDate"), LocalDateTime.parse(allParams.get("deadlineStart"), dateTimeFormatter)));
            }
            if (allParams.containsKey("deadlineEnd") && allParams.get("deadlineEnd") != null) {
                predicates.add(builder.lessThanOrEqualTo(root.get("deadlineDate"), LocalDateTime.parse(allParams.get("deadlineEnd"), dateTimeFormatter)));
            }
            query.where(builder.and(predicates.toArray(Predicate[]::new)));
            return query.getRestriction();
        };
        List<Orders> orderss = ordersRepository.findAll(spec);
        return orderss.stream()
            .map(OrdersDto::of)
            .collect(Collectors.toList());
    }
    public void updateOrdersFromMap(Map<String, Object> request) {
        OrdersDto dto = flattenRequestToDto(request);

        Optional<Orders> optional = ordersRepository.findById(dto.getId());
        if (optional.isEmpty()) {
            throw new RuntimeException("orders not found with ID: " + dto.getId());
        }

        Orders orders = optional.get();
        // 資料寫入 entity
        orders.setCommodityName(dto.getCommodityName());
        orders.setCommodityType(dto.getCommodityType());
        orders.setUnitPrice(dto.getUnitPrice());
        orders.setQuantity(dto.getQuantity());

        orders.setDealerName(dto.getDealerName());
        orders.setDealerId(dto.getDealerId());
        orders.setDealerEmail(dto.getDealerEmail());
        orders.setDealerPhone(dto.getDealerPhone());

        orders.setOrderDate(dto.getOrderDate());
        orders.setDeadlineDate(dto.getDeadlineDate());

        orders.setEmployeeName(dto.getEmployeeName());
        orders.setEmployeeId(dto.getEmployeeId());
        orders.setEmployeeEmail(dto.getEmployeeEmail());
        orders.setEmployeePhone(dto.getEmployeePhone());

        ordersRepository.save(orders); // 寫入資料庫
    }

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public OrdersDto flattenRequestToDto(Map<String, Object> request) {
        OrdersDto dto = new OrdersDto();

        dto.setId(Long.valueOf(request.get("id").toString()));

        Map<String, Object> commodity = (Map<String, Object>) request.get("commodity");
        dto.setCommodityName(commodity.get("name").toString());
        dto.setCommodityType(commodity.get("type").toString());
        Map<String, Object> transactionValue = (Map<String, Object>) commodity.get("transactionValue");
        dto.setUnitPrice(new BigDecimal(transactionValue.get("unitPrice").toString()));
        dto.setQuantity(Double.parseDouble(transactionValue.get("quantity").toString()));
        dto.setTotalCost(new BigDecimal(transactionValue.get("totalCost").toString()));

        Map<String, Object> Dealer = (Map<String, Object>) request.get("Dealer");
        dto.setDealerName(Dealer.get("name").toString());
        dto.setDealerId(Dealer.get("id").toString());
        Map<String, Object> DealerAssoc = (Map<String, Object>) Dealer.get("association");
        dto.setDealerEmail(DealerAssoc.get("email").toString());
        dto.setDealerPhone(DealerAssoc.get("phone").toString());

        Map<String, Object> responsible = (Map<String, Object>) request.get("responsible");
        dto.setEmployeeName(responsible.get("name").toString());
        dto.setEmployeeId(responsible.get("id").toString());
        Map<String, Object> employeeAssoc = (Map<String, Object>) responsible.get("association");
        dto.setEmployeeEmail(employeeAssoc.get("email").toString());
        dto.setEmployeePhone(
            employeeAssoc.get("phone") != null ? employeeAssoc.get("phone").toString() : null
        );

        dto.setOrderDate(LocalDateTime.parse(request.get("orderTimeStamp").toString(), formatter));
        dto.setDeadlineDate(LocalDateTime.parse(request.get("deadlineTimeStamp").toString(), formatter));

        return dto;
    }
    public void deleteOrdersById(Long id) {
        Orders orders = ordersRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("找不到對應 ID 的採購資料"));

        ordersRepository.delete(orders);
    }
    public void addOrders(Map<String, Object> request) {
        OrdersDto dto = flattenRequestToDto(request);
        Orders orders = new Orders();

        // 基本欄位
        orders.setCommodityName(dto.getCommodityName());
        orders.setCommodityType(dto.getCommodityType());
        orders.setUnitPrice(dto.getUnitPrice());
        orders.setQuantity(dto.getQuantity());
        orders.setOrderDate(dto.getOrderDate());
        orders.setDeadlineDate(dto.getDeadlineDate());

        // 設定供應商欄位（直接儲存進 Orders，無需資料庫查詢）
        orders.setDealerName(dto.getDealerName());
        orders.setDealerId(dto.getDealerId());
        orders.setDealerEmail(dto.getDealerEmail());
        orders.setDealerPhone(dto.getDealerPhone());

        // 查詢並綁定負責人（員工）資訊（這邊仍要驗證存在）
        orders.setEmployeeName(dto.getEmployeeName());
        orders.setEmployeeId(dto.getEmployeeId());
        orders.setEmployeeEmail(dto.getEmployeeEmail());
        orders.setEmployeePhone(dto.getEmployeePhone());
        // 儲存
        ordersRepository.save(orders);
    }
}
