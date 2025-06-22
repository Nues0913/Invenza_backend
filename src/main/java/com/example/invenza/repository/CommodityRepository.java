package com.example.invenza.repository;

import java.util.List;
import com.example.invenza.entity.Commodity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CommodityRepository extends JpaRepository<Commodity, Long>, JpaSpecificationExecutor<Commodity> {
    Commodity findByName(String name);
    Commodity findByType(String type);
    List<Commodity> findByStockQuantityLessThan(double quantity);
}
