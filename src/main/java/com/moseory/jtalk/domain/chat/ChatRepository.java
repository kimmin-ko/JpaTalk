package com.moseory.jtalk.domain.chat;

import com.moseory.jtalk.entity.Chat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRepository extends JpaRepository<Chat, Long> {



}
