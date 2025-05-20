package com.example.invenza.config;

import com.example.invenza.entity.Member;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.Collections;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberUserDetails implements UserDetails {
    private String id;
    private String username;
    private String password;
    private String email;
    private String phone;

    public MemberUserDetails() {}

    public MemberUserDetails(Member member) {
        this.id = member.getId();
        this.username = member.getUsername();
        this.password = member.getPassword();
        this.email = member.getEmail();
        this.phone = member.getPhone();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 如有權限可在此實作，暫時回傳空集合
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
