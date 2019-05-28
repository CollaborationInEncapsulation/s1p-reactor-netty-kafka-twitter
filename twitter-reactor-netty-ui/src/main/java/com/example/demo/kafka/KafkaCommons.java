package com.example.demo.kafka;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import reactor.kafka.receiver.ReceiverOptions;

public class KafkaCommons {

    public static <K, V> ReceiverOptions<K, V> resource(String name) {
        return load(KafkaCommons.class.getResourceAsStream(name));
    }

    public static <K, V> ReceiverOptions<K, V> load(InputStream path) {
        Properties properties = new Properties();

        try {
            properties.load(path);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }

        return ReceiverOptions.create(properties);
    }
}
