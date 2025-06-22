package com.example.invenza.controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.invenza.dto.CommodityDto;
import com.example.invenza.service.InventoryService;

import lombok.extern.slf4j.Slf4j;
@Slf4j
@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }
    
    @GetMapping(value = "/get-data", produces = MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8")
    public ResponseEntity<Map<String, Object>> getInventoryData(
        @RequestParam(required = false) Map<String, String> allParams
    ) {
        try {
            List<CommodityDto> commodities = inventoryService.getAllCommoditys(allParams);
            List<Map<String, Object>> responseList = commodities.stream().map(commodity -> {
                return Map.of(
                    "commodity", Map.of(
                        "name", commodity.getName(),
                        "type", commodity.getType()
                    ),
                    "stockQuantity", commodity.getStockQuantity(),
                    "expectedImportQuantity", commodity.getExpectedImportQuantity(),
                    "expectedExportQuantity", commodity.getExpectedExportQuantity(),
                    "futureStockQuantity", commodity.getFutureStockQuantity()
                );
            }).collect(Collectors.toList());
            return ResponseEntity.ok(Map.of("data", responseList));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage())); // 404，附錯誤訊息
        }

    }

}

