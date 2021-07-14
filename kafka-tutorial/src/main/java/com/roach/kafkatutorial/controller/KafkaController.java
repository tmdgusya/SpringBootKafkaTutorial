package com.roach.kafkatutorial.controller;

import com.roach.kafkatutorial.service.KafkaMessageProducer;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/kafka")
public class KafkaController {

    private KafkaMessageProducer kafkaMessageProducer;

    public KafkaController(KafkaMessageProducer kafkaMessageProducer) {
        this.kafkaMessageProducer = kafkaMessageProducer;
    }

    @PostMapping
    public String sendMessage(@RequestParam("message") String message) {
        kafkaMessageProducer.sendMessage(message);
        return "success";
    }

}
