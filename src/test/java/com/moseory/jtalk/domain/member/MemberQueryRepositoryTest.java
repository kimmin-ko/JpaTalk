package com.moseory.jtalk.domain.member;

import com.moseory.jtalk.domain.friendrelation.FriendRelationRepository;
import com.moseory.jtalk.entity.FriendRelation;
import com.moseory.jtalk.entity.Member;
import com.moseory.jtalk.entity.enumeration.FriendRelationStatus;
import io.github.benas.randombeans.EnhancedRandomBuilder;
import io.github.benas.randombeans.api.EnhancedRandom;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Rollback(false)
@Transactional
@SpringBootTest
class MemberQueryRepositoryTest {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    MemberQueryRepository memberQueryRepository;

    @Autowired
    FriendRelationRepository friendRelationRepository;

    @Autowired
    protected EntityManager em;

    static EnhancedRandom memberCreator;

    @BeforeAll
    static void setup() {
        memberCreator = EnhancedRandomBuilder.aNewEnhancedRandomBuilder()
                .stringLengthRange(5, 10)
                .dateRange(LocalDate.of(1920, 1, 1), LocalDate.of(2005, 1, 1))
                .excludeField(f -> f.getName().equals("id"))
                .excludeField(f -> f.getName().equals("friends"))
                .excludeField(f -> f.getName().equals("withdrawalDate"))
                .build();
    }

    @Test
    void findAllWithFriendRelation() {
        // given
        Member member = memberCreator.nextObject(Member.class);
        Member friend1 = memberCreator.nextObject(Member.class);
        Member friend2 = memberCreator.nextObject(Member.class);
        Member friend3 = memberCreator.nextObject(Member.class);
        Member friend4 = memberCreator.nextObject(Member.class);

        memberRepository.save(member);
        memberRepository.save(friend1);
        memberRepository.save(friend2);
        memberRepository.save(friend3);
        memberRepository.save(friend4);

        FriendRelation friendRelation1 = FriendRelation.create(member, friend1);
        FriendRelation friendRelation2 = FriendRelation.create(member, friend2);

        friendRelationRepository.save(friendRelation1);
        friendRelationRepository.save(friendRelation2);

        FriendRelation friendRelation1 = FriendRelation.create(member, friend1);
        FriendRelation friendRelation2 = FriendRelation.create(member, friend2);
        FriendRelation friendRelation3 = FriendRelation.create(member, friend3);
        FriendRelation friendRelation4 = FriendRelation.create(member, friend4);

        friendRelationRepository.save(friendRelation1);
        friendRelationRepository.save(friendRelation2);
        friendRelationRepository.save(friendRelation3);
        friendRelationRepository.save(friendRelation4);

        em.flush();
        em.clear();

        // when
        List<Member> findMembers = memberQueryRepository.findAllWithFriendRelation();

        for (Member findMember : findMembers) {
            System.out.println("findMember = " + findMember);
            System.out.println("findMember = " + findMember.getFriendsRelations().get(0).getFriendName());
            for (FriendRelation friendsRelation : findMember.getFriendsRelations()) {
                System.out.println("friendsRelation = " + friendsRelation.getFriendName());
            }
        }

        // then

    }

    @Test
    @DisplayName("정상적인 회원 등록 후 친구 추가하여 패치 조인하여 조회")
    void findById() {
        // given
        Member member = memberCreator.nextObject(Member.class);
        Member friend1 = memberCreator.nextObject(Member.class);
        Member friend2 = memberCreator.nextObject(Member.class);

        memberRepository.save(member);
        memberRepository.save(friend1);
        memberRepository.save(friend2);

        FriendRelation friendRelation1 = FriendRelation.create(member, friend1);
        FriendRelation friendRelation2 = FriendRelation.create(member, friend2);

        friendRelationRepository.save(friendRelation1);
        friendRelationRepository.save(friendRelation2);

        em.flush();
        em.clear();

        // when
        Member findMember = memberQueryRepository.findWithFriendRelationById(member.getId()).orElseThrow(EntityNotFoundException::new);
        Member findFriend1 = memberRepository.findById(friend1.getId()).orElseThrow(EntityNotFoundException::new);
        Member findFriend2 = memberRepository.findById(friend2.getId()).orElseThrow(EntityNotFoundException::new);

        FriendRelation memberFriend1 = findMember.getFriendsRelations().get(0);
        FriendRelation memberFriend2 = findMember.getFriendsRelations().get(1);

        // then
        assertThat(memberFriend1.getMember()).isEqualTo(findMember);
        assertThat(memberFriend1.getFriend()).isEqualTo(findFriend1);
        assertThat(memberFriend1.getFriendName()).isEqualTo(findFriend1.getName());
        assertThat(memberFriend1.getStatus()).isEqualTo(FriendRelationStatus.NORMAL);
        assertThat(memberFriend1.getCreatedDate()).isNotNull();
        assertThat(memberFriend1.getId()).isNotZero();

        assertThat(memberFriend2.getMember()).isEqualTo(findMember);
        assertThat(memberFriend2.getFriend()).isEqualTo(findFriend2);
        assertThat(memberFriend2.getFriendName()).isEqualTo(findFriend2.getName());
        assertThat(memberFriend2.getStatus()).isEqualTo(FriendRelationStatus.NORMAL);
        assertThat(memberFriend2.getCreatedDate()).isNotNull();
        assertThat(memberFriend2.getId()).isNotZero();
    }
    
}