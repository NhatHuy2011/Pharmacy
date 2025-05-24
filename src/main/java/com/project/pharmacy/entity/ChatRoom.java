package com.project.pharmacy.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class ChatRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @Column
    String senderId;

    @Column
    Boolean senderStatus;

    @Column
    String receiverId;

    @Column
    Boolean receiverStatus;

    @Column
    Boolean roomStatus;

    @OneToMany(mappedBy = "chatRoom")
    List<ChatMessage> messages;
}
