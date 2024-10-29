package com.example.ticketing.chat;

import com.example.ticketing.chat.dto.ChatConnectionDto;
import com.example.ticketing.chat.dto.ChatMessageDto;
import com.example.ticketing.common.UUIDGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.yaml.snakeyaml.emitter.Emitter;

import java.io.IOException;
import java.util.List;

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

        emitter.onCompletion(() -> emitterRepository.deleteById(userUUID));
        emitter.onTimeout(() -> emitterRepository.deleteById(userUUID));

        return userUUID;
    }

    public void sendMessage(String userUUID, ChatMessageDto chatMessageDto) {

        List<String> users =  emitterRepository.getAll();

        //Emitter에 등록된 유저에게 Message Body 전송
        users.forEach(receiver -> {
            if(receiver.equals(userUUID)){
                return;
            }

            SseEmitter emitter = emitterRepository.get(receiver);
            try{
                emitter.send(SseEmitter.event()
                        .name("chat")
                        .data(chatMessageDto.getName()+": "+chatMessageDto.getMessage()));
            } catch (IOException e){
                emitter.complete();
                emitterRepository.deleteById(receiver);
            }
        });


        //redis에 대화내용 저장
    }
}
