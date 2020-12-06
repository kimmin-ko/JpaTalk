package com.moseory.jtalk.entity;

import com.moseory.jtalk.entity.enumeration.ChatStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;

import java.time.LocalDateTime;

import static javax.persistence.EnumType.*;
import static javax.persistence.FetchType.*;
import static lombok.AccessLevel.*;

@Getter
@Entity
@NoArgsConstructor(access = PROTECTED)
public class Chat {

    @Id
    @GeneratedValue
    @Column(name = "chat_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "chatroom_id", nullable = false)
    private Chatroom chatroom;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(nullable = false)
    private String message;

    @Enumerated(STRING)
    @Column(nullable = false)
    private ChatStatus status;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdDate;

    private LocalDateTime deletedDate;

    private Chat(Chatroom chatroom, Member member, String message, ChatStatus status) {
        this.chatroom = chatroom;
        this.member = member;
        this.message = message;
        this.status = status;
    }

    /* 생성 메서드 */
    public static Chat create(Chatroom chatroom, Member member, String message) {
        return new Chat(chatroom, member, message, ChatStatus.NORMAL);
    }

}
