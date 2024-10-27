package com.example.ticketing.chat;

import com.example.ticketing.chat.dto.ChatConnectionDto;
import com.example.ticketing.common.UUIDGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.yaml.snakeyaml.emitter.Emitter;

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
}
