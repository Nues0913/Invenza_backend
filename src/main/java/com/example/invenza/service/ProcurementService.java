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

import com.example.invenza.dto.ProcurementDto;
import com.example.invenza.entity.Procurement;
import com.example.invenza.repository.ProcurementRepository;

import jakarta.persistence.criteria.Predicate;

@Service
public class ProcurementService {

    private final ProcurementRepository procurementRepository;

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
        if(allParams == null || allParams.isEmpty()) {
            return getUndueProcurements();
        }
        Specification<Procurement> spec = (root, query, builder) -> {
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
                predicates.add(builder.equal(root.get("supplierName"), allParams.get("businessPartner")));
            }
            if (allParams.containsKey("businessPartnerId") && allParams.get("businessPartnerId") != null) {
                predicates.add(builder.equal(root.get("supplierId"), allParams.get("businessPartnerId")));
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
        List<Procurement> procurements = procurementRepository.findAll(spec);
        return procurements.stream()
            .map(ProcurementDto::of)
            .collect(Collectors.toList());
    }
    public void updateProcurementFromMap(Map<String, Object> request) {
        ProcurementDto dto = flattenRequestToDto(request);

        Optional<Procurement> optional = procurementRepository.findById(dto.getId());
        if (optional.isEmpty()) {
            throw new RuntimeException("Procurement not found with ID: " + dto.getId());
        }

        Procurement procurement = optional.get();
        // 資料寫入 entity
        procurement.setCommodityName(dto.getCommodityName());
        procurement.setCommodityType(dto.getCommodityType());
        procurement.setUnitPrice(dto.getUnitPrice());
        procurement.setQuantity(dto.getQuantity());

        procurement.setSupplierName(dto.getSupplierName());
        procurement.setSupplierId(dto.getSupplierId());
        procurement.setSupplierEmail(dto.getSupplierEmail());
        procurement.setSupplierPhone(dto.getSupplierPhone());

        procurement.setOrderDate(dto.getOrderDate());
        procurement.setDeadlineDate(dto.getDeadlineDate());

        procurement.setEmployeeName(dto.getEmployeeName());
        procurement.setEmployeeId(dto.getEmployeeId());
        procurement.setEmployeeEmail(dto.getEmployeeEmail());
        procurement.setEmployeePhone(dto.getEmployeePhone());

        procurementRepository.save(procurement); // 寫入資料庫
    }

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public ProcurementDto flattenRequestToDto(Map<String, Object> request) {
        ProcurementDto dto = new ProcurementDto();

        dto.setId(Long.valueOf(request.get("id").toString()));

        Map<String, Object> commodity = (Map<String, Object>) request.get("commodity");
        dto.setCommodityName(commodity.get("name").toString());
        dto.setCommodityType(commodity.get("type").toString());
        Map<String, Object> transactionValue = (Map<String, Object>) commodity.get("transactionValue");
        dto.setUnitPrice(new BigDecimal(transactionValue.get("unitPrice").toString()));
        dto.setQuantity(Integer.parseInt(transactionValue.get("quantity").toString()));
        dto.setTotalCost(new BigDecimal(transactionValue.get("totalCost").toString()));

        Map<String, Object> supplier = (Map<String, Object>) request.get("supplier");
        dto.setSupplierName(supplier.get("name").toString());
        dto.setSupplierId(supplier.get("id").toString());
        Map<String, Object> supplierAssoc = (Map<String, Object>) supplier.get("association");
        dto.setSupplierEmail(supplierAssoc.get("email").toString());
        dto.setSupplierPhone(supplierAssoc.get("phone").toString());

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

    public void deleteProcurementById(Long id) {
        Procurement procurement = procurementRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("找不到對應 ID 的採購資料"));

        procurementRepository.delete(procurement);
    }
    public void addProcurement(Map<String, Object> request) {
        ProcurementDto dto = flattenRequestToDto(request);
        Procurement procurement = new Procurement();

        // 基本欄位
        procurement.setCommodityName(dto.getCommodityName());
        procurement.setCommodityType(dto.getCommodityType());
        procurement.setUnitPrice(dto.getUnitPrice());
        procurement.setQuantity(dto.getQuantity());
        procurement.setOrderDate(dto.getOrderDate());
        procurement.setDeadlineDate(dto.getDeadlineDate());

        // 設定供應商欄位（直接儲存進 procurement，無需資料庫查詢）
        procurement.setSupplierName(dto.getSupplierName());
        procurement.setSupplierId(dto.getSupplierId());
        procurement.setSupplierEmail(dto.getSupplierEmail());
        procurement.setSupplierPhone(dto.getSupplierPhone());

        // 查詢並綁定負責人（員工）資訊（這邊仍要驗證存在）
        procurement.setEmployeeName(dto.getEmployeeName());
        procurement.setEmployeeId(dto.getEmployeeId());
        procurement.setEmployeeEmail(dto.getEmployeeEmail());
        procurement.setEmployeePhone(dto.getEmployeePhone());
        // 儲存
        procurementRepository.save(procurement);
    }


}
