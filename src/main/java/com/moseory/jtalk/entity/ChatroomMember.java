package com.moseory.jtalk.entity;

import com.moseory.jtalk.entity.base.BaseTimeEntity;
import com.moseory.jtalk.entity.enumeration.ChatroomStatus;
import lombok.Getter;

import javax.persistence.*;

import static javax.persistence.EnumType.*;
import static javax.persistence.FetchType.*;

@Getter
@Entity
public class ChatroomMember extends BaseTimeEntity {

    @Id
    @GeneratedValue
    @Column(name = "chatroom_member_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "chatroom_id", nullable = false)
    private Chatroom chatroom;

    @Column(nullable = false)
    private String chatroomName;

    @Enumerated(STRING)
    @Column(nullable = false)
    private ChatroomStatus status;

    /* 생성 메서드 */

    /* 연관관계 메서드 */

    /* 비지니스 로직 */

    /* 조회 로직 */

}