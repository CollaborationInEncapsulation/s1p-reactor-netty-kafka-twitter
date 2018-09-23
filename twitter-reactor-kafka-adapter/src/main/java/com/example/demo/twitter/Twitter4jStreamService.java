package com.example.demo.twitter;

import java.util.Arrays;

import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;
import twitter4j.HashtagEntity;
import twitter4j.TwitterStreamFactory;

public class Twitter4jStreamService implements TwitterStreamService {

    private static final String[] FILTER_KEY_WORDS = {
        "S1P", "SpringOnePlatform", "SpringFramework", "WebFlux", "R2DBC",
        "Java", "Kotlin", "Scala", "JavaScript", "JS",
        "ReactiveJava", "RxJava", "RxNetty", "RxKotlin", "RxScala", "RxJs",
        "ProjectReactor", "ReactorNetty", "Reactor-Netty", "ReactorCore", "Reactor-Core", "ReactorKafka", "Reactor-Kafka", "ReactorScala", "Reactor-Scala", "ReactorRabbitMQ", "Reactor-RabbitMQ",
        "Programming", "ReactiveProgramming", "ReactiveSystem", "ReactiveStreams", "Reactive-Streams", "Coding",
        "ApacheKafka", "Kafka", "ReactiveKafka",
        "Akka", "AkkaStreams", "Alpakka",
        "Netty", "JDBC",
        "Computer", "Science", "Software",
    };


    Flux<RawTweet> tweetsFlux;

    public Twitter4jStreamService() {
        this.tweetsFlux = Flux
            .<RawTweet>create(sink -> new TwitterStreamFactory()
                .getInstance()
                .addListener((SimpleStatusListener)(status) -> {
                    if (status.getGeoLocation() != null) {
                        sink.next(new RawTweet(
                            String.valueOf(status.getId()),
                            status.getUser().getName(),
                            status.getText(),
                            Arrays.stream(status.getHashtagEntities())
                                  .map(HashtagEntity::getText)
                                  .toArray(String[]::new),
                            new double[]{
                                status.getGeoLocation().getLongitude(),
                                status.getGeoLocation().getLatitude()
                            },
                            status.getUser().getLocation()
                        ));
                    }

                    if (status.getPlace() != null) {
                        sink.next(new RawTweet(
                            String.valueOf(status.getId()),
                            status.getUser().getName(),
                            status.getText(),
                            Arrays.stream(status.getHashtagEntities())
                                  .map(HashtagEntity::getText)
                                  .toArray(String[]::new),
                            new double[]{
                                status.getPlace().getGeometryCoordinates()[0][0].getLongitude(),
                                status.getPlace().getGeometryCoordinates()[0][0].getLatitude()
                            },
                            status.getUser().getLocation()
                        ));
                    }

                    if(status.getUser().getLocation() != null) {
                        sink.next(new RawTweet(
                            String.valueOf(status.getId()),
                            status.getUser().getName(),
                            status.getText(),
                            Arrays.stream(status.getHashtagEntities())
                                  .map(HashtagEntity::getText)
                                  .toArray(String[]::new),
                            new double[0],
                            status.getUser().getLocation()
                        ));
                    }
                })
                .filter(FILTER_KEY_WORDS)
            )
			.subscribeOn(Schedulers.elastic());
    }

    @Override
    public Flux<RawTweet> stream() {
        return tweetsFlux;
    }
}
