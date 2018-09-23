package com.example.demo.kafka;

import java.io.IOException;
import java.util.Map;

import com.example.demo.twitter.RawTweet;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Deserializer;

public class RawTweetDeserializer implements Deserializer<RawTweet> {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) { }

    @Override
    public RawTweet deserialize(String topic,  byte[] data) {
        try {
            return objectMapper.readValue(data, RawTweet.class);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() { }
}
