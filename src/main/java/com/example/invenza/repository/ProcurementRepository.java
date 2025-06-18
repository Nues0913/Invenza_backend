package com.example.invenza.repository;

import com.example.invenza.entity.Procurement;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProcurementRepository extends JpaRepository<Procurement, Long> {
    Procurement findByCommodityName(String commodityName);
    Procurement findByCommodityType(String commodityType);
    List<Procurement> findByDeadlineDateAfter(LocalDateTime dateTime);

    //後續須實裝的功能
}
