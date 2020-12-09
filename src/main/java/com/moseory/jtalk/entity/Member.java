package com.moseory.jtalk.entity;

import com.moseory.jtalk.entity.base.BaseTimeEntity;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.moseory.jtalk.domain.member.MemberApiController.MemberJoinRequest;
import static lombok.AccessLevel.PROTECTED;

@Getter
@Entity
//@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor(access = PROTECTED)
@ToString(of = {"id", "account", "email", "password", "name", "phoneNumber", "stateMessage", "profileUrl", "birthDate", "withdrawalDate"})
public class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    @OneToMany(mappedBy = "member")
    private List<FriendRelation> friends = new ArrayList<>();

    @Column(nullable = false, unique = true)
    private String account;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    private String phoneNumber;

    private String stateMessage;

    private String profileUrl;

    @Column(nullable = false)
    private LocalDate birthDate;

    private LocalDateTime withdrawalDate;

    public Member(String account, String email, String password, String name, LocalDate birthDate) {
        this.account = account;
        this.email = email;
        this.password = password;
        this.name = name;
        this.birthDate = birthDate;
    }

    @Builder
    private Member(String account, String email, String password, String name, String phoneNumber, LocalDate birthDate) {
        this.account = account;
        this.email = email;
        this.password = password;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.birthDate = birthDate;
    }

    /* 생성 메서드 */
    public static Member from(MemberJoinRequest memberJoinRequest) {
        return Member.builder()
                .account(memberJoinRequest.getAccount())
                .email(memberJoinRequest.getEmail())
                .password(memberJoinRequest.getPassword())
                .name(memberJoinRequest.getName())
                .phoneNumber(memberJoinRequest.getPhoneNumber())
                .birthDate(memberJoinRequest.getBirthDate())
                .build();
    }

    /* 연관관계 메서드 */

    /* 비지니스 로직 */

    /* 조회 로직 */

}