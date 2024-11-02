package com.example.ticketing.chat;

import com.example.ticketing.chat.dto.ChatConnectionDto;
import com.example.ticketing.chat.dto.ChatMessageDto;
import com.example.ticketing.common.UUIDGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.yaml.snakeyaml.emitter.Emitter;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

    private final EmitterRepository emitterRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final String chatKey = "all-chat";
    private static final Long DEFAULT_TIMEOUT = 600L * 1000 * 60;

    public SseEmitter getChatConnection(String userUUID){
        SseEmitter emitter = new SseEmitter(DEFAULT_TIMEOUT);
        emitterRepository.save(userUUID,emitter);

        emitter.onCompletion(() -> emitterRepository.deleteById(userUUID));
        emitter.onTimeout(() -> emitterRepository.deleteById(userUUID));

        log.info(userUUID +" SSE Connection Open");

        return emitter;
    }

    public void sendMessage(String userUUID, ChatMessageDto chatMessageDto) throws JsonProcessingException {

        List<String> users =  emitterRepository.getAll();
        log.info("users size : "+users.size());
        //Emitter에 등록된 유저에게 Message Body 전송
        ObjectMapper objectMapper = new ObjectMapper();
        String data = objectMapper.writeValueAsString(chatMessageDto);
        users.forEach(receiver -> {
            if(receiver.equals(userUUID)){
                return;
            }

            SseEmitter emitter = emitterRepository.get(receiver);
            try{
                emitter.send(SseEmitter.event()
                        .name("chat")
                        .data(data));
            } catch (IOException e){
                emitter.complete();
                emitterRepository.deleteById(receiver);
            }
        });

        log.info(userUUID +" send Message");

        //redis에 대화내용 저장

        // 채팅 메시지를 Redis 리스트에 추가
        redisTemplate.opsForList().rightPush(chatKey, chatMessageDto);

        redisTemplate.expire(chatKey, 7, TimeUnit.DAYS);
    }

    public List<Object> getAllChatMessages(){
        return redisTemplate.opsForList().range(chatKey, 0, -1);
    }
}
