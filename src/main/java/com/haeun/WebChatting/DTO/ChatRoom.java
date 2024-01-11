package com.haeun.WebChatting.DTO;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.haeun.WebChatting.service.ChatService;
import lombok.Builder;
import lombok.Data;
import org.springframework.web.socket.WebSocketSession;

import java.util.HashSet;
import java.util.Set;

@Data
//@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class ChatRoom {
    private String roomId; // 채팅방 아이디
    private String name; // 채팅방 이름
    private Set<WebSocketSession> sessions = new HashSet<>();

    @Builder
    public ChatRoom(String roomId, String name){
        this.roomId = roomId;
        this.name = name;
    }

    //메세지 타입에 따라 session(클라이언트)에게 메시지를 전달히기 위한 메서드
    public void handleAction(WebSocketSession session, ChatDTO message, ChatService service) {
        // message 에 담긴 타입을 확인한다.
        // 이때 message 에서 getType 으로 가져온 내용이
        // ChatDTO 의 열거형인 MessageType 안에 있는 ENTER 과 동일한 값이라면
        if (message.getType().equals(ChatDTO.MessageType.ENTER)) {
            // sessions 에 넘어온 session 을 담고,
            sessions.add(session);

            // message 에는 입장하였다는 메시지를 띄운다
            message.setMessage(message.getSender() + " 님이 입장하셨습니다");
            sendMessage(message, service);
        } else if (message.getType().equals(ChatDTO.MessageType.TALK)) {
            message.setMessage(message.getMessage());
            sendMessage(message, service);
        }
    }

    //sessions에 담긴 모든 세션에 handleAction으로 부터 넘어온 메시지를 전달할 수 있도록 하는 메서드
    public <T> void sendMessage(T message, ChatService service) {
        sessions.parallelStream().forEach(session -> service.sendMessage(session, message));
    }
}
