package com.example.ticketing.chat;

import com.example.ticketing.chat.dto.ChatConnectionDto;
import com.example.ticketing.chat.dto.ChatMessageDto;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

@Slf4j
@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @PostMapping("/connection")
    ResponseEntity<?> getChattingConnection(@RequestBody ChatConnectionDto chatConnectionDto, HttpServletResponse response){
        String userUUID = chatService.getChatConnection(chatConnectionDto);

        // 쿠키 생성 및 설정
        ResponseCookie cookie = ResponseCookie.from("userId", userUUID)
                .httpOnly(true)                // HttpOnly 설정
                //.secure(true)                  // HTTPS를 사용하는 경우 secure 설정
                .path("/")                     // 쿠키의 유효 경로 설정
                .maxAge(60 * 60 * 24 )    // 1일 동안 유효
                .sameSite("Strict")            // CSRF 방지를 위한 SameSite 설정
                .build();

        // 응답 헤더에 쿠키 추가
        response.addHeader("Set-Cookie", cookie.toString());

        return ResponseEntity.ok(userUUID);
    }

    @PostMapping("/sendMessage")
    ResponseEntity<?> sendMessage(@RequestBody ChatMessageDto chatMessageDto, HttpServletRequest request){

        String userUUID = Arrays.stream(request.getCookies())
                .filter(cookie -> "userId".equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse("No user ID found in cookie.");

        chatService.sendMessage(userUUID,chatMessageDto);
        return ResponseEntity.ok(true);
    }
}
