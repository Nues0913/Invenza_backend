package com.example.invenza.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import static java.util.Comparator.comparingDouble;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.example.invenza.dto.CommodityDto;
import com.example.invenza.entity.Commodity;
import com.example.invenza.entity.Procurement;
import com.example.invenza.repository.CommodityRepository;

import jakarta.persistence.criteria.Predicate;

@Service
public class InventoryService {

    private final CommodityRepository commodityRepository;

    public InventoryService(CommodityRepository commodityRepository) {
        this.commodityRepository = commodityRepository;
    }

    public List<CommodityDto> getAllCommoditys() {
        List<Commodity> commoditys = commodityRepository.findAll();

        return commoditys.stream().map(p -> {
            CommodityDto dto = new CommodityDto();
            dto.setId(p.getId());
            dto.setName(p.getName());
            dto.setType(p.getType());
            dto.setStockQuantity(p.getStockQuantity());
            dto.setExpectedImportQuantity(p.getExpectedImportQuantity());
            dto.setExpectedExportQuantity(p.getExpectedExportQuantity());
            dto.setFutureStockQuantity(p.getFutureStockQuantity());

            return dto;
        }).toList();
    }

    public List<CommodityDto> getAllCommoditys(Map<String, String> allParams) {
        if (allParams == null || allParams.isEmpty()) {
            return getAllCommoditys();
        }
        Specification<Commodity> spec = (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (allParams.containsKey("commodityName") && allParams.get("commodityName") != null) {
                predicates.add(builder.equal(root.get("name"), allParams.get("commodityName")));
            }
            if (allParams.containsKey("commodityType") && allParams.get("commodityType") != null) {
                predicates.add(builder.equal(root.get("type"), allParams.get("commodityType")));
            }
            if (allParams.containsKey("minAmount") && allParams.get("minAmount") != null) {
                if(allParams.containsKey("inventoryFilterType") && allParams.get("inventoryFilterType").equals("inventoryFuture")){
                    // transient 屬性無法直接查詢
                    predicates.add(
                        builder.greaterThanOrEqualTo(
                            builder.diff(
                                builder.sum(root.get("stockQuantity"), root.get("expectedImportQuantity")),
                                root.get("expectedExportQuantity")
                            ),
                            Double.parseDouble(allParams.get("minAmount"))
                        )
                    );
                } else {
                    predicates.add(builder.greaterThanOrEqualTo(root.get("stockQuantity"), Double.parseDouble(allParams.get("minAmount"))));
                }
            }
            if (allParams.containsKey("maxAmount") && allParams.get("maxAmount") != null) {
                if(allParams.containsKey("inventoryFilterType") && allParams.get("inventoryFilterType").equals("inventoryFuture")){
                    // transient 屬性無法直接查詢
                    predicates.add(
                        builder.lessThanOrEqualTo(
                            builder.diff(
                                builder.sum(root.get("stockQuantity"), root.get("expectedImportQuantity")),
                                root.get("expectedExportQuantity")
                            ),
                            Double.parseDouble(allParams.get("maxAmount"))
                        )
                    );
                } else {
                    predicates.add(builder.lessThanOrEqualTo(root.get("stockQuantity"), Double.parseDouble(allParams.get("maxAmount"))));
                }
            }
            query.where(builder.and(predicates.toArray(Predicate[]::new)));
            return query.getRestriction();
        };
        List<Commodity> commoditys = commodityRepository.findAll(spec);
        if(allParams.containsKey("inventoryFilterType") && allParams.get("inventoryFilterType").equals("inventoryFuture")) {
            commoditys.sort(comparingDouble(Commodity::getFutureStockQuantity));
        } else if (allParams.containsKey("inventoryFilterType") && allParams.get("inventoryFilterType").equals("inventoryNow")) {
            commoditys.sort(comparingDouble(Commodity::getStockQuantity));
        }
        return commoditys.stream()
            .map(CommodityDto::of)
            .collect(Collectors.toList());
    }
}
