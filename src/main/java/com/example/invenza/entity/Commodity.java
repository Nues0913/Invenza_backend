package com.example.invenza.entity;

import jakarta.persistence.*;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "commodity")
public class Commodity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String type;
    private double stockQuantity;
    private double expectedImportQuantity;
    private double expectedExportQuantity;

    @Transient
    public double getFutureStockQuantity() {
        return stockQuantity - expectedExportQuantity + expectedImportQuantity;
    }
}