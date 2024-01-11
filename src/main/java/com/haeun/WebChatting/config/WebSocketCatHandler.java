package com.haeun.WebChatting.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.haeun.WebChatting.DTO.ChatDTO;
import com.haeun.WebChatting.DTO.ChatRoom;
import com.haeun.WebChatting.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
@Slf4j
@RequiredArgsConstructor
public class WebSocketCatHandler extends TextWebSocketHandler {
    private final ObjectMapper mapper;

    private final ChatService service;

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        log.info("payload {}", payload);

//        TextMessage textMessage = new TextMessage("Welcome Chatting Server");
//        session.sendMessage(textMessage);

        //채팅 메시지를 전달받아 채팅 메시지 객체로 변환
        ChatDTO chatMessage = mapper.readValue(payload, ChatDTO.class);
        log.info("session {}", chatMessage.toString());

        //전달 받은 메시지에 담긴 채팅방 id로 대상 채팅방 정보 조회
        ChatRoom room = service.findRoomById(chatMessage.getRoomId());
        log.info("room {}", room.toString());

        //메시지 타입에 따른 메시지 발송
        room.handleAction(session, chatMessage, service);
    }
}
