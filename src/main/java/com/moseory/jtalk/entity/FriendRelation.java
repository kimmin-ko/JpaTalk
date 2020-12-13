package com.moseory.jtalk.entity;

import com.moseory.jtalk.entity.enumeration.FriendRelationStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.time.LocalDateTime;

import static javax.persistence.EnumType.STRING;
import static javax.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PROTECTED;

@Getter
@Entity
@NoArgsConstructor(access = PROTECTED)
@ToString(of = {"id", "friendName", "status", "createdDate"})
public class FriendRelation {

    @Id
    @GeneratedValue
    @Column(name = "friend_relation_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "friend_id")
    private Member friend;

    private String friendName;

    @Enumerated(STRING)
    private FriendRelationStatus status;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdDate;

    @Builder
    protected FriendRelation(Member member, Member friend, String friendName, FriendRelationStatus status) {
        this.member = member;
        this.friend = friend;
        this.friendName = friendName;
        this.status = status;
    }

    /* 생성 메서드 */
    public static FriendRelation create(Member member, Member friend) {
        FriendRelation friendRelation = new FriendRelation(member, friend, friend.getName(), FriendRelationStatus.NORMAL);

        member.getFriendsRelations().add(friendRelation);

        return friendRelation;
    }

    /* 테스트 메서드 */
    public void setMemberAndFriend(Member member, Member friend) {
        this.member = member;
        this.friend = friend;
        this.friendName = friend.getName();
    }

}