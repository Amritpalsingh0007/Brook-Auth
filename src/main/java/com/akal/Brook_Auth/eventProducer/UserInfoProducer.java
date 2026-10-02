package com.akal.Brook_Auth.eventProducer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

public class UserInfoProducer {
    private final KafkaTemplate<String, UserInfoEvent> kafkaTemplate;

    @Value("${spring.kafka.topic.name}")
    private String TOPIC_NAME;

    @Autowired
    public UserInfoProducer(KafkaTemplate<String, UserInfoEvent> kafkaTemplate){
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendEventToKafka(UserInfoEvent userInfoEvent){
        Message<UserInfoEvent> message = MessageBuilder.withPayload(userInfoEvent).setHeader(KafkaHeaders.TOPIC, TOPIC_NAME).build();
        kafkaTemplate.send(message);
    }
}
