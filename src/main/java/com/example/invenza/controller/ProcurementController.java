package com.example.invenza.controller;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        log.info("getProcurementData params: {}", allParams);
        List<ProcurementDto> procurements = procurementService.getUndueProcurements(allParams);
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
    }

}

