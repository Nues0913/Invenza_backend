package com.example.invenza.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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

    @GetMapping("/get-data")
    public ResponseEntity<?> getAllProcurements() {
        try {
            List<ProcurementDto> data = procurementService.getAllProcurements();
            return ResponseEntity.ok(Map.of("data", data));
        } catch (Exception e) {
            log.error("/api/procurement/get-data {}: {}", e.getClass().getName(), e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "error message"));
        }
    }
}

