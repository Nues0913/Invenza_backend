package com.example.invenza.config.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.invenza.repository.MemberRepository;

@Service
public class MemberUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    public MemberUserDetailsService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String account) throws UsernameNotFoundException {
        var member = memberRepository.findByAccount(account);
        if (member == null) {
            throw new UsernameNotFoundException("Can't find user: " + account);
        }
        return new MemberUserDetails(member);
    }
}
