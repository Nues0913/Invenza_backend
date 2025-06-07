package com.example.invenza.controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

//import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.invenza.dto.ProcurementDto;
import com.example.invenza.entity.Procurement;
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
    public ResponseEntity<Map<String, Object>> getProcurementData() {
        List<Procurement> procurements = procurementService.getUndueProcurements();

        List<ProcurementDto> result = procurements.stream()
            .map(procurement -> ProcurementDto.of(procurement))
            .collect(Collectors.toList());

        return ResponseEntity.ok(Map.of("data", result));
    }
}

