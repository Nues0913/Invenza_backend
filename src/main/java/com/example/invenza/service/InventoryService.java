package com.example.invenza.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.invenza.dto.CommodityDto;
import com.example.invenza.entity.Commodity;
import com.example.invenza.repository.CommodityRepository;

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
}
