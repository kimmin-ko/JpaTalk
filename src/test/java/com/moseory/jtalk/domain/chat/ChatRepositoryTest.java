package com.moseory.jtalk.domain.chat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

@DataJpaTest
class ChatRepositoryTest {

    @Autowired
    ChatRepository chatRepository;

    @Autowired
    TestEntityManager em;

    @Test
    @DisplayName("chat 저장 테스트")
    void save() {
        // given

        // when

        // then

    }

}