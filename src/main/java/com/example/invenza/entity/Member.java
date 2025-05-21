package com.example.invenza.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "member")
public class Member {
    @Id
    private String id;
    private String account;
    private String name;
    private String password;
    private String email;
    private String phone;
}