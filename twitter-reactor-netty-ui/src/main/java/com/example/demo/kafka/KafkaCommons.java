package com.example.demo.kafka;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Properties;

import reactor.kafka.receiver.ReceiverOptions;
import reactor.kafka.sender.SenderOptions;

public class KafkaCommons {

    public static <K, V> ReceiverOptions<K, V> resource(String name) {
        return load(Paths.get(ClassLoader.getSystemResource(name).getFile()));
    }

    public static <K, V> ReceiverOptions<K, V> load(Path path) {
        Properties properties = new Properties();

        try {
            properties.load(Files.newInputStream(path, StandardOpenOption.READ));
        }
        catch (IOException e) {
            throw  new RuntimeException(e);
        }

        return ReceiverOptions.create(properties);
    }
}
