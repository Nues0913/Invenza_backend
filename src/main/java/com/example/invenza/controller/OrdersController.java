package com.example.invenza.controller;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.invenza.dto.OrdersDto;
import com.example.invenza.service.OrdersService;

import lombok.extern.slf4j.Slf4j;
@Slf4j
@RestController
@RequestMapping("/api/sales")

public class OrdersController {
    private final OrdersService ordersService;

    public OrdersController(OrdersService ordersService) {
        this.ordersService = ordersService;
    }
    @GetMapping(value = "/get-data", produces = "application/json; charset=utf-8")
    public ResponseEntity<Map<String, Object>> getOrdersData(@RequestParam(required = false) Map<String, String> allParams) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        log.info("getOrdersData params: {}", allParams);
        List<OrdersDto> orderss = ordersService.getUndueOrders(allParams);
        try {
            List<Map<String, Object>> responseList = orderss.stream().map(orders -> {
                return Map.of(
                    "commodity", Map.of(
                        "name", orders.getCommodityName(),
                        "transactionValue", Map.of(
                            "unitPrice", orders.getUnitPrice(),
                            "quantity", orders.getQuantity(),
                            "totalCost", orders.getUnitPrice().multiply(BigDecimal.valueOf(orders.getQuantity()))
                        ),
                        "type", orders.getCommodityType()
                    ),
                    "deadlineTimeStamp", orders.getDeadlineDate().format(formatter),
                    "distributor", Map.of(
                        "association", Map.of(
                            "email", orders.getDealerEmail(),
                            "phone", orders.getDealerPhone()
                        ),
                        "id", orders.getDealerId(),
                        "name", orders.getDealerName()
                    ),
                    "id", orders.getId(),
                    "orderTimeStamp", orders.getOrderDate().format(formatter),
                    "responsible", Map.of(
                        "association", Map.of(
                            "email", orders.getEmployeeEmail(),
                            "phone", orders.getEmployeePhone()
                        ),
                        "id", orders.getEmployeeId(),
                        "name", orders.getEmployeeName()
                    )
                );
            }).collect(Collectors.toList());
            // log.info("return: {}", responseList);
            return ResponseEntity.ok(Map.of("data", responseList));
        } catch (Exception e) {
            log.error("Error occurred while fetching orders data", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }        
    }
    @PutMapping("/update-data")
    public ResponseEntity<?> updateOrders(@RequestBody Map<String, Object> request) {
        try {
            ordersService.updateOrdersFromMap(request);
            return ResponseEntity.ok().build(); // 200 不回傳資料
        } catch (Exception e) {
            log.error("Error occurred while updating Orders data", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage())); // 404 錯誤處理
        }
    }
    @DeleteMapping("/delete-data")
    public ResponseEntity<?> deleteOrders(@RequestParam Long id) {
        try {
            ordersService.deleteOrdersById(id);
            return ResponseEntity.ok().build(); // 200 無內容
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage())); // 400 有錯誤訊息
        }
    }
    @PostMapping("/add-data")
    public ResponseEntity<?> addOrders(@RequestBody Map<String, Object> request) {
        try {
            ordersService.addOrders(request);
            return ResponseEntity.ok().build(); // 200，無回傳資料
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .body(Map.of("error", e.getMessage())); // 404，附錯誤訊息
        }
    }
}
