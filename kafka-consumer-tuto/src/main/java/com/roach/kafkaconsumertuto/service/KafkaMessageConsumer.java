package com.roach.kafkaconsumertuto.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class KafkaMessageConsumer {

    private static final String KAFKA_MESSAGE_KEY = "MESSAGE";
    @KafkaListener(topics = KAFKA_MESSAGE_KEY, groupId = "message_group")
    public String consumeMessage(String message) throws IOException {
        System.out.println("Message : " + message);
        return message;
    }

}
