package com.project.pharmacy.repository;

import com.project.pharmacy.entity.ChatMessage;
import com.project.pharmacy.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, String> {
    List<ChatMessage> findAllByChatRoomOrderByTimeAsc(ChatRoom chatRoom);
}
