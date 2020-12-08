package com.moseory.jtalk.domain.member;

import com.moseory.jtalk.domain.abstact.AbstractServiceTest;
import com.moseory.jtalk.entity.Member;
import com.moseory.jtalk.entity.enumeration.FriendRelationStatus;
import com.moseory.jtalk.global.exception.business.DuplicateAccountException;
import com.moseory.jtalk.global.exception.business.DuplicateEmailException;
import io.github.benas.randombeans.EnhancedRandomBuilder;
import io.github.benas.randombeans.api.EnhancedRandom;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

class MemberServiceTest extends AbstractServiceTest {

    @InjectMocks
    MemberService memberService;

    @Mock
    MemberRepository memberRepository;

    static EnhancedRandom memberCreator;
    static EnhancedRandom friendRelationCreator;

    @BeforeAll
    static void setup() {
        memberCreator = EnhancedRandomBuilder.aNewEnhancedRandomBuilder()
                .stringLengthRange(3, 5)
                .dateRange(LocalDate.of(1920, 1, 1), LocalDate.of(2005, 1, 1))
                .excludeField(f -> f.getName().equals("id"))
                .excludeField(f -> f.getName().equals("friends"))
                .excludeField(f -> f.getName().equals("createdDate"))
                .excludeField(f -> f.getName().equals("modifiedDate"))
                .excludeField(f -> f.getName().equals("withdrawalDate"))
                .randomize(f -> f.getName().equals("email"), () -> "test@gmail.com")
                .build();

        friendRelationCreator = EnhancedRandomBuilder.aNewEnhancedRandomBuilder()
                .stringLengthRange(3, 5)
                .excludeField(f -> f.getName().equals("id"))
                .excludeField(f -> f.getName().equals("member"))
                .excludeField(f -> f.getName().equals("friend"))
                .excludeField(f -> f.getName().equals("createdDate"))
                .randomize(f -> f.getName().equals("status"), () -> FriendRelationStatus.NORMAL)
                .build();
    }

    @Test
    @DisplayName("정상적인 회원 가입")
    void join() {
        // given
        Member member = memberCreator.nextObject(Member.class);

        given(memberRepository.existsByAccount(member.getAccount())).willReturn(false);
        given(memberRepository.existsByEmail(member.getEmail())).willReturn(false);

        // when
        memberService.join(member);

        // then
        then(memberRepository).should(times(1)).save(member);
        then(memberRepository).shouldHaveNoMoreInteractions();
    }

    @Test
    @DisplayName("회원과 친구가 정상적으로 존재할 때 추가")
    void addFriend() {
        // given
        Member member = memberCreator.nextObject(Member.class);
        Member friend = memberCreator.nextObject(Member.class);

        given(memberRepository.findById(1L)).willReturn(Optional.of(member));
        given(memberRepository.findById(2L)).willReturn(Optional.of(friend));

        // when

        // then

    }

    @Test
    @DisplayName("회원가입 Email 중복")
    void join_duplicate_email() {
        // given
        Member member = memberCreator.nextObject(Member.class);

        given(memberRepository.existsByEmail(member.getEmail())).willReturn(true);

        // then
        assertThatThrownBy(() -> memberService.join(member))
                .isInstanceOf(DuplicateEmailException.class);
    }

    @Test
    @DisplayName("회원가입 Account 중복")
    void join_duplicate_account() {
        // given
        Member member = memberCreator.nextObject(Member.class);

        given(memberRepository.existsByAccount(member.getAccount())).willReturn(true);

        // then
        assertThatThrownBy(() -> memberService.join(member))
                .isInstanceOf(DuplicateAccountException.class);
    }

}