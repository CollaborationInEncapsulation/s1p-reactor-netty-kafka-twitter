package com.example.demo.twitter;

import java.util.Arrays;

import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;
import twitter4j.HashtagEntity;
import twitter4j.TwitterStreamFactory;

public class Twitter4jStreamService implements TwitterStreamService {

    private static final String[] FILTER_KEY_WORDS = {
        "JPrime", "@jPrimeConf", "jPrimeConf", "JPrime2019", "#jprime", "#jprime2019",
        "SpringFramework", "WebFlux", "ProjectReactor", "ReactorNetty", "ReactorCore",
        "ReactorKafka", "ReactiveStreams", "BlockHound", "ReactorTools"
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
