package com.example.invenza.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import com.example.invenza.config.MemberUserDetails;

@Getter
@Setter
public class LoginResponse {
    private String jwt;
    private String id;
    private String username;
    private String email;
    private String phone;

    public static LoginResponse of(String jwt, MemberUserDetails user) {
        var res = new LoginResponse();
        res.jwt = jwt;
        res.id = user.getId();
        res.username = user.getUsername();
        res.email = user.getEmail();
        res.phone = user.getPhone();
        return res;
    }
}
