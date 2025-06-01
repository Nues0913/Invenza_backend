package com.example.invenza.repository;

import java.util.List;
import com.example.invenza.entity.Commodity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommodityRepository extends JpaRepository<Commodity, Long> {
    Commodity findByName(String name);
    Commodity findByType(String type);
    List<Commodity> findByStockQuantityLessThan(double quantity);
}
