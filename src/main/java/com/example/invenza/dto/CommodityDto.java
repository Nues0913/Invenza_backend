package com.example.invenza.dto;
import com.example.invenza.entity.Commodity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommodityDto {
    private Long id;
    private String name;
    private String type;
    private double stockQuantity;
    private double expectedImportQuantity;
    private double expectedExportQuantity;
    private double futureStockQuantity;

    public static CommodityDto of(Commodity commodity) {
        CommodityDto dto = new CommodityDto();
        dto.setId(commodity.getId());
        dto.setName(commodity.getName());
        dto.setType(commodity.getType());
        dto.setStockQuantity(commodity.getStockQuantity());
        dto.setExpectedImportQuantity(commodity.getExpectedImportQuantity());
        dto.setExpectedExportQuantity(commodity.getExpectedExportQuantity());
        dto.setFutureStockQuantity(commodity.getFutureStockQuantity());
        return dto;
    }
}
