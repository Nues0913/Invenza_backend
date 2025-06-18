package com.example.invenza.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.example.invenza.dto.ProcurementDto;
import com.example.invenza.entity.Procurement;
import com.example.invenza.repository.ProcurementRepository;

import jakarta.persistence.criteria.Predicate;

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

    public List<ProcurementDto> getUndueProcurements() {
        LocalDateTime now = LocalDateTime.now();
        List<Procurement> procurements =  procurementRepository.findByDeadlineDateAfter(now);
        return procurements.stream()
        .map(ProcurementDto::of)
        .collect(Collectors.toList());
    }

    public List<ProcurementDto> getUndueProcurements(Map<String, String> allParams) {
        LocalDateTime now = LocalDateTime.now();
        if(allParams == null || allParams.isEmpty()) {
            return getUndueProcurements();
        }

        // businessPartner -> supplierName
        // businessPartnerId -> supplierId
        // responsible -> employeeName
        // responsibleId -> employeeId
        Specification<Procurement> spec = (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (allParams.containsKey("commodityName") && allParams.get("commodityName") != null) {
                predicates.add(builder.equal(root.get("commodityName"), allParams.get("commodityName")));
            }
            if (allParams.containsKey("commodityType") && allParams.get("commodityType") != null) {
                predicates.add(builder.equal(root.get("commodityType"), allParams.get("commodityType")));
            }
            if (allParams.containsKey("businessPartner") && allParams.get("businessPartner") != null) {
                predicates.add(builder.equal(root.get("supplierName"), allParams.get("supplierName")));
            }
            if (allParams.containsKey("businessPartnerId") && allParams.get("businessPartnerId") != null) {
                predicates.add(builder.equal(root.get("supplierId"), allParams.get("supplierId")));
            }
            if (allParams.containsKey("orderTimeStart") && allParams.get("orderTimeStart") != null) {
                predicates.add(builder.greaterThanOrEqualTo(root.get("orderTime"), allParams.get("orderTimeStart")));
            }
            if (allParams.containsKey("orderTimeEnd") && allParams.get("orderTimeEnd") != null) {
                predicates.add(builder.lessThanOrEqualTo(root.get("orderTime"), allParams.get("orderTimeEnd")));
            }
            if (allParams.containsKey("deadlineStart") && allParams.get("deadlineStart") != null) {
                predicates.add(builder.greaterThanOrEqualTo(root.get("deadline"), allParams.get("deadlineStart")));
            }
            if (allParams.containsKey("deadlineEnd") && allParams.get("deadlineEnd") != null) {
                predicates.add(builder.lessThanOrEqualTo(root.get("deadline"), allParams.get("deadlineEnd")));
            }
            if (allParams.containsKey("responsible") && allParams.get("responsible") != null) {
                predicates.add(builder.equal(root.get("employeeName"), allParams.get("employeeName")));
            }
            if (allParams.containsKey("responsibleId") && allParams.get("responsibleId") != null) {
                predicates.add(builder.equal(root.get("employeeId"), allParams.get("employeeId")));
            }
            query.where(builder.and(predicates.toArray(new Predicate[0])));
            return query.getRestriction();
        };
        List<Procurement> procurements = procurementRepository.findAll(spec);
        return procurements.stream()
            .map(ProcurementDto::of)
            .collect(Collectors.toList());
    }
}
