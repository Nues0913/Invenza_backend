package com.example.invenza.service;

import org.springframework.stereotype.Service;

import com.example.invenza.dto.*;
import com.example.invenza.entity.*;
import com.example.invenza.repository.*;

import java.util.List;
import java.time.LocalDate;

@Service
public class ProcurementService {

    private ProcurementRepository procurementRepository;

    public ProcurementService(ProcurementRepository procurementRepository) {
        this.procurementRepository = procurementRepository;
    }

    public List<ProcurementDto> getAllProcurements() {
        List<Procurement> procurements = procurementRepository.findAll();

        return procurements.stream().map(p -> {
            ProcurementDto dto = new ProcurementDto();
            dto.setId(p.getId());
            dto.setCommodityName(p.getCommodityName());
            dto.setCommodityType(p.getCommodityType());
            dto.setUnitPrice(p.getUnitPrice());
            dto.setQuantity(p.getQuantity());
            dto.setTotalCost(p.getTotalCost());

            dto.setSupplierName(p.getSupplierName());
            dto.setSupplierId(p.getSupplierId());
            dto.setSupplierEmail(p.getSupplierEmail());
            dto.setSupplierPhone(p.getSupplierPhone());

            dto.setOrderDate(p.getOrderDate());
            dto.setDeadlineDate(p.getDeadlineDate());

            // TODO: 若未來有負責人欄位再加上
            dto.setEmployeeName("Employee name");
            dto.setEmployeeId("Employee id");
            dto.setEmployeeEmail("Employee email");
            dto.setEmployeePhone("Employee phone");

            return dto;
        }).toList();
    }

    public List<Procurement> getUndueProcurements() {
        LocalDate today = LocalDate.now();
        return procurementRepository.findByDeadlineDateAfter(today);
    }
}
