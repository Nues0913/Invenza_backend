package com.example.invenza.repository;

import com.example.invenza.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Order findByName(String name);
    Order findByType(String type);  
    //後續須實裝的功能
}
