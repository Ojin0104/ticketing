package com.example.ticketing.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;
@Slf4j
@Component
public class UUIDGenerator {

    public String makeUUID(){
        String uuid = UUID.randomUUID().toString();
        log.info("makeUUID : "+uuid);
        return uuid;
    }
}
