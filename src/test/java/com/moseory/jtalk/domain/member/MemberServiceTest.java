package com.moseory.jtalk.domain.member;

import com.moseory.jtalk.entity.Member;
import com.moseory.jtalk.global.exception.business.DuplicateAccountException;
import com.moseory.jtalk.global.exception.business.DuplicateEmailException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @InjectMocks
    MemberService memberService;

    @Mock
    MemberRepository memberRepository;

    Member member;

    @BeforeEach
    void beforeEach() {
        member = new Member(
                "account",
                "memberA@naver.com",
                "test1234",
                "memberA",
                LocalDate.of(1992, 2, 16));
    }

    @Test
    @DisplayName("회원가입 테스트")
    void join() {
        // given
        given(memberRepository.existsByAccount(member.getAccount())).willReturn(false);
        given(memberRepository.existsByEmail(member.getEmail())).willReturn(false);

        // when
        memberService.join(member);

        // then
        then(memberRepository).should(times(1)).save(member);
        then(memberRepository).shouldHaveNoMoreInteractions();
    }

    @Test
    @DisplayName("회원가입 Email 중복 테스트")
    void join_duplicate_email() {
        // given
        given(memberRepository.existsByEmail(member.getEmail())).willReturn(true);

        // when

        // then
        assertThatThrownBy(() -> memberService.join(member))
                .isInstanceOf(DuplicateEmailException.class);
    }

    @Test
    @DisplayName("회원가입 Account 중복 테스트")
    void join_duplicate_account() {
        // given
        given(memberRepository.existsByAccount(member.getAccount())).willReturn(true);

        // when

        // then
        assertThatThrownBy(() -> memberService.join(member))
                .isInstanceOf(DuplicateAccountException.class);
    }

}