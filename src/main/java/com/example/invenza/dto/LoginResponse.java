package com.example.invenza.dto;

import com.example.invenza.config.security.MemberUserDetails;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginResponse {
    private String jwt;
    private String name;
    private String id;
    private String email;
    private String phone;

    public static LoginResponse of(String jwt, MemberUserDetails user) {
        var res = new LoginResponse();
        res.setJwt(jwt);
        res.setName(user.getUsername());
        res.setId(user.getId());
        res.setEmail(user.getEmail());
        res.setPhone(user.getPhone());
        return res;
    }
}
