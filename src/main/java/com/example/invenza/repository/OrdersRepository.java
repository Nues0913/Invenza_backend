package com.example.invenza.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.example.invenza.entity.Orders;


public interface OrdersRepository extends JpaRepository<Orders, Long>, JpaSpecificationExecutor<Orders>  {
    Orders findByCommodityName(String commodityName);
    Orders findByCommodityType(String commodityType);  
    List<Orders> findByDeadlineDateAfter(LocalDateTime dateTime);
}
