package com.moseory.jtalk.domain.member;

import com.moseory.jtalk.entity.Member;

import java.util.List;
import java.util.Optional;

public interface MemberRepositoryCustom {

    Optional<Member> findWithFriendRelationById(Long id);

    List<Member> findAllWithFriendRelation();

}
