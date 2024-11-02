package com.example.ticketing.chat.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


@Getter
@NoArgsConstructor
public class ChatMessageDto {
    private String name;
    private String userUUID;
    private String message;
    private String timestamp;

    public ChatMessageDto(String name, String userUUID, String message) {
        this.name = name;
        this.userUUID = userUUID;
        this.message = message;
        this.timestamp = DateTimeFormatter.ISO_INSTANT.format(Instant.now()); // 현재 시간 ISO 형식 문자열;
    }
}
