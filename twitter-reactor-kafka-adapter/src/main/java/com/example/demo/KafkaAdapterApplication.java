package com.example.demo;

import com.example.demo.kafka.KafkaCommons;
import com.example.demo.twitter.RawTweet;
import com.example.demo.twitter.Twitter4jStreamService;
import com.example.demo.twitter.TwitterStreamService;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderRecord;

public class KafkaAdapterApplication {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaAdapterApplication.class);

    public static void main(String[] args) {
        TwitterStreamService twitterStreamService = new Twitter4jStreamService();
        KafkaSender<String, RawTweet> kafkaSender = KafkaSender
            .create(KafkaCommons.resource("/kafka.properties"));

        twitterStreamService.stream()
                            .map(rawTweet -> SenderRecord.create(new ProducerRecord<>("tweets", rawTweet.getId(), rawTweet), rawTweet.getId()))
                            .log()
                            .transform(kafkaSender::send)
                            .blockLast();
    }
}
