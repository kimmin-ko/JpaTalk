package com.moseory.jtalk.domain.member;

import com.moseory.jtalk.domain.abstact.AbstractRepositoryTest;
import com.moseory.jtalk.entity.Member;
import io.github.benas.randombeans.EnhancedRandomBuilder;
import io.github.benas.randombeans.api.EnhancedRandom;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class MemberRepositoryTest extends AbstractRepositoryTest {

    @Autowired
    MemberRepository memberRepository;

    static EnhancedRandom memberCreator;

    @BeforeAll
    static void setup() {
        memberCreator = EnhancedRandomBuilder.aNewEnhancedRandomBuilder()
                .stringLengthRange(3, 5)
                .dateRange(LocalDate.of(1920, 1, 1), LocalDate.of(2005, 1, 1))
                .excludeField(f -> f.getName().equals("id"))
                .excludeField(f -> f.getName().equals("friends"))
                .excludeField(f -> f.getName().equals("withdrawalDate"))
                .randomize(f -> f.getName().equals("email"), () -> "test@gmail.com")
                .build();
    }

    @Test
    @DisplayName("정상적인 회원 저장 성공")
    void save() {
        // given
        Member member = memberCreator.nextObject(Member.class);

        // when
        memberRepository.save(member);

        em.flush();

        Member findMember = memberRepository.findById(member.getId()).orElseThrow(EntityNotFoundException::new);

        // then
        assertThat(findMember).isEqualTo(member).isSameAs(member);

        assertThat(findMember.getId()).isNotZero();
        assertThat(findMember.getAccount()).isEqualTo(member.getAccount());
        assertThat(findMember.getEmail()).isEqualTo(member.getEmail());
        assertThat(findMember.getPassword()).isEqualTo(member.getPassword());
        assertThat(findMember.getName()).isEqualTo(member.getName());
        assertThat(findMember.getBirthDate()).isEqualTo(member.getBirthDate());
        assertThat(findMember.getCreatedDate()).isNotNull();
        assertThat(findMember.getWithdrawalDate()).isNull();
    }

    @Test
    @DisplayName("회원 Account 존재 여부 확인")
    void existsByAccount() {
        // given
        Member member = memberCreator.nextObject(Member.class);

        String invalidAccount = "Invalid Account";

        memberRepository.save(member);

        // when
        boolean existsAccount = memberRepository.existsByAccount(member.getAccount());
        boolean nonExistsAccount = memberRepository.existsByAccount(invalidAccount);

        // then
        assertThat(existsAccount).isTrue();
        assertThat(nonExistsAccount).isFalse();
    }

    @Test
    @DisplayName("회원 Email 존재 여부 확인")
    void existsByEmail() {
        // given
        Member member = memberCreator.nextObject(Member.class);

        String invalidEmail = "Invalid email";

        memberRepository.save(member);

        // when
        boolean existsEmail = memberRepository.existsByEmail(member.getEmail());
        boolean nonExistsEmail = memberRepository.existsByEmail(invalidEmail);

        // then
        assertThat(existsEmail).isTrue();
        assertThat(nonExistsEmail).isFalse();
    }

}









