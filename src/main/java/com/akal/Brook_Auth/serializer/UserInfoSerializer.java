package com.akal.Brook_Auth.serializer;

import com.akal.Brook_Auth.eventProducer.UserInfoEvent;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.serialization.Serializer;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;

public class UserInfoSerializer implements Serializer<UserInfoEvent> {
    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        Serializer.super.configure(configs, isKey);
    }

    @Override
    public byte[] serialize(String s, UserInfoEvent userInfoEvent) {
        ObjectMapper objectMapper = new ObjectMapper();
        byte[] retVal = null;
        try {
            retVal = objectMapper.writeValueAsString(userInfoEvent).getBytes();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return new byte[0];
    }

    @Override
    public void close() {
        Serializer.super.close();
    }
}
