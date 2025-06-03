package com.example.invenza.repository;

import com.example.invenza.entity.Procurement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProcurementRepository extends JpaRepository<Procurement, Long> {
    Procurement findByCommodityName(String commodityName);
    Procurement findByCommodityType(String commodityType);
    //後續須實裝的功能
}
