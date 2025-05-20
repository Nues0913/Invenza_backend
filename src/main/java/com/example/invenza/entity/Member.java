package com.example.invenza.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Member {
    private String id;
    private String username;
    private String password;
    private String email;
    private String phone;
}