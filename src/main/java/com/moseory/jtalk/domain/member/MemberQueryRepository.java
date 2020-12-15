package com.moseory.jtalk.domain.member;

import com.moseory.jtalk.entity.Member;
import com.moseory.jtalk.entity.QMember;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.moseory.jtalk.entity.QFriendRelation.friendRelation;
import static com.moseory.jtalk.entity.QMember.member;

@Repository
@RequiredArgsConstructor
public class MemberQueryRepository {

    private final JPAQueryFactory query;

    public List<Member> findAllWithFriendRelation() {
        return query
                .selectFrom(member)
                .join(member.friendsRelations)
                .fetch();
    }

    public Optional<Member> findWithFriendRelationById(Long memberId) {
        Member findMember = query
                .selectFrom(member)
                .join(member.friendsRelations).fetchJoin()
                .where(member.id.eq(memberId))
                .fetchOne();

        return Optional.ofNullable(findMember);
    }


    /**
     * 변경
     */
    public List<Member> findFriendsById(Long memberId) {
        QMember friend = new QMember("friend");

        return query
                .select(friend)
                .from(friendRelation).fetchJoin()
                .where(friendRelation.member.id.eq(memberId))
                .fetch();
    }

}
