package com.example.demo.twitter;

import java.util.Arrays;

import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.scheduler.Schedulers;
import twitter4j.HashtagEntity;
import twitter4j.TwitterStreamFactory;

public class Twitter4jStreamService implements TwitterStreamService {

    private static final String[] FILTER_KEY_WORDS = {
        "JPrime", "@jPrimeConf", "jPrimeConf", "JPrime2019", "#jprime", "#jprime2019",
        "SpringFramework", "WebFlux", "ProjectReactor", "ReactorNetty", "ReactorCore",
        "ReactorKafka", "ReactiveStreams", "BlockHound", "ReactorTools"
    };


    private final Flux<RawTweet> tweetsFlux;

    public Twitter4jStreamService() {
        this.tweetsFlux = Flux
            .<RawTweet>create(sink -> new TwitterStreamFactory()
                .getInstance()
                .addListener((SimpleStatusListener)(status) -> {
                    if (status.getGeoLocation() != null || status.getPlace() != null || status.getUser().getLocation() != null) {
                        RawTweet.Builder rawTweetBuilder = RawTweet
                            .builder()
                            .withId(String.valueOf(status.getId()))
                            .withUser(status.getUser().getName())
                            .withContent(status.getText())
                            .withTags(
                                Arrays.stream(status.getHashtagEntities())
                                      .map(HashtagEntity::getText)
                                      .toArray(String[]::new)
                            )
                            .withUserLocation(status.getUser().getLocation());

                        if (status.getGeoLocation() != null) {
                            rawTweetBuilder.withLocation(
                                new double[] {
                                    status.getGeoLocation().getLongitude(),
                                    status.getGeoLocation().getLatitude()
                                }
                            );
                        }

                        if (status.getPlace() != null) {
                            rawTweetBuilder.withLocation(
                                new double[] {
                                    status.getPlace()
                                          .getGeometryCoordinates()[0][0].getLongitude(),
                                    status.getPlace()
                                          .getGeometryCoordinates()[0][0].getLatitude()
                                }
                            );
                        }

                        sink.next(rawTweetBuilder.build());
                    }
                })
                .filter(FILTER_KEY_WORDS)
            , FluxSink.OverflowStrategy.ERROR)
			.subscribeOn(Schedulers.newSingle("Tweets-Processor"));
    }

    @Override
    public Flux<RawTweet> stream() {
        return tweetsFlux;
    }
}
