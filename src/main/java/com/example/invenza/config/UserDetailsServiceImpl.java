package com.example.invenza.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.example.invenza.entity.Member;

public class UserDetailsServiceImpl implements UserDetailsService {
    private final Map<String, Member> memberMap = new HashMap<>();

    public UserDetailsServiceImpl(List<Member> members) {
        members.forEach(m -> memberMap.put(m.getUsername(), m));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var member = memberMap.get(username);
        if (member == null) {
            throw new UsernameNotFoundException("Can't find username: " + username);
        }
        return new MemberUserDetails(member);
    }
}
