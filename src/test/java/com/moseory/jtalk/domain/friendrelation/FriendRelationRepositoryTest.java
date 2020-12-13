package com.moseory.jtalk.domain.friendrelation;

import com.moseory.jtalk.entity.Member;
import com.moseory.jtalk.entity.FriendRelation;
import com.moseory.jtalk.entity.enumeration.FriendRelationStatus;
import com.moseory.jtalk.domain.member.MemberRepository;
import com.moseory.jtalk.global.config.JpaConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(JpaConfig.class)
class FriendRelationRepositoryTest {

    @Autowired
    FriendRelationRepository friendRelationRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    TestEntityManager em;

    @Test
    @DisplayName("정상적인 친구관계 저장 성공")
    void save_test() {
        // given
        Member member = new Member("memberA", "memberA@email.com", "password", "본인", LocalDate.of(1992, 2, 16));
        Member friendA = new Member("friendA", "friendA@email.com", "password", "친구A", LocalDate.of(1997, 12, 20));
        Member friendB = new Member("friendB", "friendB@email.com", "password", "친구B", LocalDate.of(1997, 12, 20));

        memberRepository.save(member);
        memberRepository.save(friendA);
        memberRepository.save(friendB);

        em.flush(); // DB 저장

        FriendRelation friendRelationA = FriendRelation.create(member, friendA);
        FriendRelation friendRelationB = FriendRelation.create(member, friendB);

        // when
        FriendRelation savedFriendRelation = friendRelationRepository.save(friendRelationA);
        friendRelationRepository.save(friendRelationB);

        // then
        assertThat(member.getFriendsRelations().size()).isEqualTo(2);

        assertThat(savedFriendRelation.getId()).isNotZero();
        assertThat(savedFriendRelation.getMember()).isEqualTo(member);
        assertThat(savedFriendRelation.getFriend()).isEqualTo(friendA);
        assertThat(savedFriendRelation.getFriendName()).isEqualTo(friendA.getName());
        assertThat(savedFriendRelation.getStatus()).isEqualByComparingTo(FriendRelationStatus.NORMAL);
        assertThat(savedFriendRelation.getCreatedDate()).isNotNull();
    }

}