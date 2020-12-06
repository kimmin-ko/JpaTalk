package com.moseory.jtalk.domain.member;

import com.moseory.jtalk.entity.Member;
import com.moseory.jtalk.global.config.JpaConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(JpaConfig.class)
//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // Use Real Database
class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    TestEntityManager em;

    @Test
    @DisplayName("정상적인 회원 저장 성공")
    void save() {
        // given
        Member normalMember = Member.builder()
                .account("account")
                .name("name")
                .email("email@gmail.com")
                .password("password")
                .phoneNumber("010-3725-9670")
                .birthDate(LocalDate.of(1992, 2, 16))
                .build();

        // when
        memberRepository.save(normalMember);

        em.flush();

        Member findMember = memberRepository.findById(normalMember.getId()).orElseThrow(EntityNotFoundException::new);

        // then
        assertThat(findMember).isEqualTo(normalMember).isSameAs(normalMember);
        assertThat(findMember.getId()).isNotZero();
        assertThat(findMember.getAccount()).isEqualTo(normalMember.getAccount());
        assertThat(findMember.getEmail()).isEqualTo(normalMember.getEmail());
        assertThat(findMember.getPassword()).isEqualTo(normalMember.getPassword());
        assertThat(findMember.getName()).isEqualTo(normalMember.getName());
        assertThat(findMember.getBirthDate()).isEqualTo(normalMember.getBirthDate());
        assertThat(findMember.getCreatedDate()).isNotNull();
        assertThat(findMember.getWithdrawalDate()).isNull();
    }

    @Test
    @DisplayName("회원 Account 존재 여부 확인")
    void existsByAccount() {
        // given
        Member member = Member.builder()
                .account("account")
                .name("name")
                .email("email@gmail.com")
                .password("password")
                .birthDate(LocalDate.of(1992, 2, 16))
                .build();

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
        Member member = Member.builder()
                .account("account")
                .name("name")
                .email("email@gmail.com")
                .password("password")
                .birthDate(LocalDate.of(1992, 2, 16))
                .build();

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









