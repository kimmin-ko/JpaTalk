package com.moseory.jtalk.domain.member;

import com.moseory.jtalk.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositorySupport {

    boolean existsByEmail(String email);

    boolean existsByAccount(String account);

}