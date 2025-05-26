package com.project.pharmacy.repository;

import com.project.pharmacy.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, String> {
    boolean existsBySenderId(String senderId);

    boolean existsByReceiverId(String receiverId);

    boolean existsBySenderIdAndReceiverId(String senderId, String receiverId);

    ChatRoom findBySenderIdAndReceiverId(String senderId, String receiverId);

    List<ChatRoom> findAllBySenderId(String senderId);

    List<ChatRoom> findAllByReceiverId(String senderId);

    List<ChatRoom> findByRoomStatus(Boolean status);
}
