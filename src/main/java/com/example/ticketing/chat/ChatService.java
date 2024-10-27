package com.example.ticketing.chat;

import com.example.ticketing.chat.dto.ChatConnectionDto;
import com.example.ticketing.chat.dto.ChatMessageDto;
import com.example.ticketing.common.UUIDGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

    private final EmitterRepository emitterRepository;
    private final UUIDGenerator uuidGenerator;

    private static final Long DEFAULT_TIMEOUT = 600L * 1000 * 60;

    public String getChatConnection(ChatConnectionDto chatConnectionDto){
        SseEmitter emitter = new SseEmitter(DEFAULT_TIMEOUT);
        String userUUID = uuidGenerator.makeUUID();
        emitterRepository.save(userUUID,emitter);
        return userUUID;
    }

    public void sendMessage(ChatMessageDto chatMessageDto) {

        //Emitter에 등록된 유저에게 Message Body 전송

        //redis에 대화내용 저장
    }
}
