package com.example.demo.twitter;

import java.util.Collections;

import com.example.demo.kafka.KafkaCommons;
import reactor.core.publisher.Flux;
import reactor.kafka.receiver.KafkaReceiver;

public class KafkaTwitterStreamService implements TwitterStreamService {

    final Flux<RawTweet> tweetsFlux;

    public KafkaTwitterStreamService() {
        // Integration with Reactor Kafka
        // this.tweetsFlux = ...

    }

    @Override
    public Flux<RawTweet> stream() {
        return tweetsFlux;
    }
}
