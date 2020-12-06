package com.moseory.jtalk.domain.member;

import com.moseory.jtalk.entity.Member;
import com.moseory.jtalk.entity.QMember;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.Optional;

import static com.moseory.jtalk.entity.QMember.*;

@Repository
@RequiredArgsConstructor
public class MemberQueryRepository {

    private final EntityManager em;

    public Optional<Member> findById(Long id) {
        JPAQueryFactory query = new JPAQueryFactory(em);

        Member member = query
                .selectFrom(QMember.member)
                .join(QMember.member.friends).fetchJoin()
                .where(QMember.member.id.eq(id))
                .fetchOne();

        return Optional.ofNullable(member);
    }

}
