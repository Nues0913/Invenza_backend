package com.example.invenza.repository;

import java.util.List;
import com.example.invenza.entity.Procurement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProcurementRepository extends JpaRepository<Procurement, Long> {
    Procurement findByName(String name);
    Procurement findByType(String type);
    
}
