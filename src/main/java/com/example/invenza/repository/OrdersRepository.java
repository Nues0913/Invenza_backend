package com.example.invenza.repository;

import com.example.invenza.entity.Orders;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrdersRepository extends JpaRepository<Orders, Long> {
    Orders findByCommodityName(String commodityName);
    Orders findByCommodityType(String commodityType);  
}
