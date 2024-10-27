package com.example.ticketing.chat;

import com.example.ticketing.chat.dto.ChatConnectionDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @PostMapping("/connection")
    ResponseEntity<?> getChattingConnection(@RequestBody ChatConnectionDto chatConnectionDto){
        String userUUID = chatService.getChatConnection(chatConnectionDto);
        return ResponseEntity.ok(userUUID);
    }
}
