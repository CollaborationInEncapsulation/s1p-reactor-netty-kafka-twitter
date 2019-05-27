package com.example.demo.kafka;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Properties;

import reactor.kafka.sender.SenderOptions;

public class KafkaCommons {

    public static <K, V> SenderOptions<K, V> resource(String name) {
        return load(KafkaCommons.class.getResourceAsStream(name));
    }

    public static <K, V> SenderOptions<K, V> load(InputStream path) {
        Properties properties = new Properties();

        try {
            properties.load(path);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }

        return SenderOptions.create(properties);
    }
}
