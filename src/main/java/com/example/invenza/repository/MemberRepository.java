package com.example.invenza.repository;

import com.example.invenza.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, String> {
    Member findByName(String name);
    Member findByAccount(String account);
}