package com.moseory.jtalk.domain.member;

import com.moseory.jtalk.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom {

    boolean existsByEmail(String email);

    boolean existsByAccount(String account);

}