package com.example.ticketing.chat;

import com.example.ticketing.chat.dto.ChatConnectionDto;
import com.example.ticketing.chat.dto.ChatMessageDto;
import com.example.ticketing.common.UUIDGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Arrays;

@Slf4j
@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final UUIDGenerator uuidGenerator;

    @GetMapping(value = "/setting")
    ResponseEntity<?> chatInitialSetting(HttpServletRequest request, HttpServletResponse response){

        String userUUID = null;
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("userId".equals(cookie.getName())) {
                    userUUID = cookie.getValue();
                    break;
                }
            }
        }

        if(userUUID == null) {
            userUUID = uuidGenerator.makeUUID();
            // 쿠키 생성 및 설정
            ResponseCookie cookie = ResponseCookie.from("userId", userUUID)
                   // .httpOnly(true)
                    .path("/")
                    .maxAge(60 * 60 * 24)
                    .sameSite("Strict")
                    .build();

            // 응답 헤더에 쿠키 추가
            response.addHeader("Set-Cookie", cookie.toString());

        }

        return ResponseEntity.ok(chatService.getAllChatMessages());
    }


    @GetMapping(value = "/connection", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    ResponseEntity<SseEmitter> getChattingConnection(HttpServletRequest request, HttpServletResponse response){

        String userUUID = null;
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("userId".equals(cookie.getName())) {
                    userUUID = cookie.getValue();
                    break;
                }
            }
        }

        SseEmitter emitter = chatService.getChatConnection(userUUID);

        return ResponseEntity.ok(emitter);
    }

    @PostMapping("/sendMessage")
    ResponseEntity<?> sendMessage(@RequestBody ChatMessageDto chatMessageDto, HttpServletRequest request) throws JsonProcessingException {

        String userUUID = Arrays.stream(request.getCookies())
                .filter(cookie -> "userId".equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse("No user ID found in cookie.");
        chatService.sendMessage(userUUID,chatMessageDto);
        return ResponseEntity.ok(true);
    }
}
