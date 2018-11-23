package com.example.demo.twitter;

import java.util.Collections;

import com.example.demo.kafka.KafkaCommons;
import reactor.core.publisher.Flux;
import reactor.kafka.receiver.KafkaReceiver;

public class KafkaTwitterStreamService implements TwitterStreamService {

    final Flux<RawTweet> tweetsFlux;

    public KafkaTwitterStreamService() {
        this.tweetsFlux = KafkaReceiver
            .create(KafkaCommons.<String, RawTweet>resource("kafka.properties")
                                .subscription(Collections.singleton("tweets")))
            .receive()
            .concatMap(record -> record.receiverOffset()
                                       .commit()
                                       .thenReturn(record.value()));

    }

    @Override
    public Flux<RawTweet> stream() {
        return tweetsFlux;
    }
}
