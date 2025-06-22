package com.example.invenza.controller;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.invenza.dto.ProcurementDto;
import com.example.invenza.service.ProcurementService;

import lombok.extern.slf4j.Slf4j;
@Slf4j
@RestController
@RequestMapping("/api/procurement")
public class ProcurementController {

    private final ProcurementService procurementService;

    public ProcurementController(ProcurementService procurementService) {
        this.procurementService = procurementService;
    }
    
    @GetMapping(value = "/get-data", produces = "application/json; charset=utf-8")
    public ResponseEntity<Map<String, Object>> getProcurementData(
        // @RequestParam(required = false) String commodityName,
        // @RequestParam(required = false) String commodityType,
        // @RequestParam(required = false) String businessPartner,
        // @RequestParam(required = false) String businessPartnerId,
        // @RequestParam(required = false) String orderTimeStart,
        // @RequestParam(required = false) String orderTimeEnd,
        // @RequestParam(required = false) String deadlineStart,
        // @RequestParam(required = false) String deadlineEnd,
        // @RequestParam(required = false) String responsible,
        // @RequestParam(required = false) String responsibleId,
        @RequestParam(required = false) Map<String, String> allParams
    ) {
        log.debug("/get-data called with params: {}", allParams);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        List<ProcurementDto> procurements = procurementService.getUndueProcurements(allParams);
        try {
            List<Map<String, Object>> responseList = procurements.stream().map(procurement -> {
                return Map.of(
                    "id", procurement.getId(),
                    "commodity", Map.of(
                        "name", procurement.getCommodityName(),
                        "type", procurement.getCommodityType(),
                        "transactionValue", Map.of(
                            "unitPrice", procurement.getUnitPrice(),
                            "quantity", procurement.getQuantity(),
                            "totalCost", procurement.getUnitPrice().multiply(BigDecimal.valueOf(procurement.getQuantity()))
                        )
                    ),
                    "supplier", Map.of(
                        "name", procurement.getSupplierName(),
                        "id", procurement.getSupplierId(),
                        "association", Map.of(
                            "email", procurement.getSupplierEmail(),
                            "phone", procurement.getSupplierPhone()
                        )
                    ),
                    "orderTimeStamp", procurement.getOrderDate().format(formatter),
                    "deadlineTimeStamp", procurement.getDeadlineDate().format(formatter),
                    "responsible", Map.of(
                        "name", procurement.getEmployeeName(),
                        "id", procurement.getEmployeeId(),
                        "association", Map.of(
                            "email", procurement.getEmployeeEmail(),
                            "phone", procurement.getEmployeePhone()
                        )
                    )
                );
            }).collect(Collectors.toList());
            // log.info("return: {}", responseList);
            return ResponseEntity.ok(Map.of("data", responseList));
        } catch (Exception e) {
            log.error("/get-data {}: {}", e.getClass().getName(), e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
        
    }
    @PutMapping("/update-data")
    public ResponseEntity<?> updateProcurement(@RequestBody Map<String, Object> request) {
        try {
            log.debug("/update-data called with request: {}", request);
            procurementService.updateProcurementFromMap(request);
            return ResponseEntity.ok().build(); // 200 不回傳資料
        } catch (Exception e) {
            log.error("/update-data {}: {}", e.getClass().getName(), e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage())); // 404 錯誤處理
        }
    }
    @DeleteMapping("/delete-data")
    public ResponseEntity<?> deleteProcurement(@RequestParam Long id) {
        try {
            log.debug("/delete-data called with request: {}", id);
            procurementService.deleteProcurementById(id);
            return ResponseEntity.ok().build(); // 200 無內容
        } catch (IllegalArgumentException e) {
            log.error("/delete-data {}: {}", e.getClass().getName(), e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage())); // 400 有錯誤訊息
        }
    }
    @PostMapping("/add-data")
    public ResponseEntity<?> addProcurement(@RequestBody Map<String, Object> request) {
        try {
            log.debug("/add-data called with request: {}", request);
            procurementService.addProcurement(request);
            return ResponseEntity.ok().build(); // 200，無回傳資料
        } catch (Exception e) {
            log.error("/add-data {}: {}", e.getClass().getName(), e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .body(Map.of("error", e.getMessage())); // 404，附錯誤訊息
        }
    }
}

