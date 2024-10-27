package com.example.ticketing.common;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UUIDGenerator {

    public String makeUUID(){
        return UUID.randomUUID().toString();
    }
}
