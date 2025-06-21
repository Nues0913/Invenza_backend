package com.example.invenza.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.example.invenza.entity.Procurement;

public interface ProcurementRepository extends JpaRepository<Procurement, Long>, JpaSpecificationExecutor<Procurement> {
    Procurement findByCommodityName(String commodityName);
    Procurement findByCommodityType(String commodityType);
    List<Procurement> findByDeadlineDateAfter(LocalDateTime dateTime);

    //後續須實裝的功能
}
