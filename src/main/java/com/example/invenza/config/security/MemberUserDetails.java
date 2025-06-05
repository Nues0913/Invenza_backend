package com.example.invenza.config.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
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
    private String role;

    public MemberUserDetails() {}

    public MemberUserDetails(Member member) {
        this.id = member.getId();
        this.name = member.getName();
        this.password = member.getPassword();
        this.email = member.getEmail();
        this.phone = member.getPhone();
        this.role = member.getRole();
    }

    @Override
    public String getUsername() {
        return this.name;
    }

    public String getRole() {
        return this.role;
    }
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // TODO: 實作權限功能(admin, employee)
        List<GrantedAuthority> authorities = new ArrayList<>();
        
        if (role == null || role.trim().isEmpty()) {
            return authorities;
        }
        
        int r = Integer.parseInt(String.valueOf(role), 16);

        if ((r & 1) != 0) {
            authorities.add(new SimpleGrantedAuthority("ROLE_SALER"));
        }
        if ((r & 2) != 0) {
            authorities.add(new SimpleGrantedAuthority("ROLE_INVENTORY"));
        }
        if ((r & 4) != 0) {
            authorities.add(new SimpleGrantedAuthority("ROLE_PROCUREMENT"));
        }
        if ((r & 8) != 0) {
            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        }

        return authorities;
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
