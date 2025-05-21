package com.example.invenza.config.security;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.invenza.entity.Member;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberUserDetails implements UserDetails {
    private String jwt;
    private String id;
    private String name;
    private String password;
    private String email;
    private String phone;

    public MemberUserDetails() {}

    public MemberUserDetails(Member member) {
        this.id = member.getId();
        this.name = member.getName();
        this.password = member.getPassword();
        this.email = member.getEmail();
        this.phone = member.getPhone();
    }

    @Override
    public String getUsername() {
        return this.name;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // TODO: 實作權限功能(admin, employee)
        return Collections.emptyList();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
