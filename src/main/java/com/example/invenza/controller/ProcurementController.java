package com.example.invenza.controller;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.invenza.entity.Procurement;
import com.example.invenza.service.ProcurementService;
import com.example.invenza.dto.ProcurementDto;

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
    public ResponseEntity<Map<String, Object>> getProcurementData() {
        List<Procurement> procurements = procurementService.getUndueProcurements();
        List<ProcurementDto> responseList = procurements.stream()
        .map(ProcurementDto::of)
        .collect(Collectors.toList());
        log.info("return: {}", responseList);
        return ResponseEntity.ok(Map.of("data", responseList));
    }

}

