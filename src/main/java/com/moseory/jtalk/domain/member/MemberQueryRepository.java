package com.moseory.jtalk.domain.member;

import com.moseory.jtalk.entity.Member;
import com.moseory.jtalk.entity.QMember;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.moseory.jtalk.entity.QFriendRelation.friendRelation;
import static com.moseory.jtalk.entity.QMember.*;
import static com.moseory.jtalk.entity.QMember.member;

@Repository
@RequiredArgsConstructor
public class MemberQueryRepository implements MemberRepositorySupport {

    private final JPAQueryFactory query;

    public List<Member> findAllWithFriendRelation() {
        return query
                .selectFrom(member)
                .leftJoin(member.friendsRelations, friendRelation).fetchJoin()
                .fetch();
    }

    public Optional<Member> findWithFriendRelationById(Long memberId) {
        Member findMember = query
                .selectFrom(member)
                .join(member.friendsRelations, friendRelation).fetchJoin()
                .where(member.id.eq(memberId))
                .fetchOne();

        return Optional.ofNullable(findMember);
    }

    public Optional<Member> findWithFriendRelationByIdAndFriend(Long memberId, Long friendId) {
        Member findFriend = query
                .selectFrom(member)
                .join(member.friendsRelations, friendRelation)
                .where(member.id.eq(memberId).and(friendRelation.friend.id.eq(friendId)))
                .fetchOne();

        return Optional.ofNullable(findFriend);
    }

}
