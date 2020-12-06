package com.moseory.jtalk.entity;

import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.CascadeType.ALL;

@Getter
@Entity
public class Chatroom {

    @Id
    @GeneratedValue
    @Column(name = "chatroom_id")
    private Long id;

    @OneToMany(mappedBy = "chatroom", cascade = ALL)
    private List<ChatroomMember> chatroomMembers = new ArrayList<>();

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdDate;

    private LocalDateTime deletedDate;

    /* 생성 메서드 */

    /* 연관관계 메서드 */

    /* 비지니스 로직 */

    /* 조회 로직 */

}
