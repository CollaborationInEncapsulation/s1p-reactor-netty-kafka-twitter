package com.example.demo;

import java.util.Arrays;

import com.mapbox.api.geocoding.v5.GeocodingCriteria;
import com.mapbox.api.geocoding.v5.MapboxGeocoding;
import com.mapbox.api.geocoding.v5.models.GeocodingResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.ReplayProcessor;
import reactor.core.scheduler.Schedulers;
import reactor.kafka.sender.KafkaSender;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import twitter4j.HashtagEntity;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.TwitterStreamFactory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class DemoApplication {

    private static KafkaSender<Long, Tweet> sender;



    public static void main(String[] args) {
//        Map<String, Object> props = new HashMap<>();
//        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.31.109:9092");
//        props.put(ProducerConfig.CLIENT_ID_CONFIG, "sample-producer");
//        props.put(ProducerConfig.ACKS_CONFIG, "all");
//        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class);
//        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
//        sender = KafkaSender.create(SenderOptions.create(props));

        SpringApplication.run(DemoApplication.class, args);
    }



    @Controller
    public static class IndexController {
        @GetMapping("/")
        public String index() {
            return "index";
        }
    }

    @RestController
    public static class SSEController {
        private final Flux<Tweet> tweetsFlux =  Flux.<Tweet>create(sink -> {
            new TwitterStreamFactory().getInstance().addListener(new StatusListener() {

                private final NettyCallFactory factory = new NettyCallFactory();

                @Override
                public void onStatus(Status status) {
                    if (status.getGeoLocation() != null) {
                        sink.next(new Tweet(
                                status.getText(),
                                new double[] {
                                    status.getGeoLocation().getLongitude(),
                                    status.getGeoLocation().getLatitude()
                                },
                                Arrays.stream(status.getHashtagEntities())
                                      .map(HashtagEntity::getText)
                                      .toArray(String[]::new)
                        ));
                    }

                    if (status.getPlace() != null) {
                        sink.next(new Tweet(
                                status.getText(),
                                new double[] {
                                    status.getPlace().getGeometryCoordinates()[0][0].getLongitude(),
                                    status.getPlace().getGeometryCoordinates()[0][0].getLatitude()
                                },
                                Arrays.stream(status.getHashtagEntities())
                                      .map(HashtagEntity::getText)
                                      .toArray(String[]::new)
                        ));
                    }

                    if (status.getUser().getLocation() != null) {

                        MapboxGeocoding client = MapboxGeocoding.builder()
                                                                .accessToken("")
                                                                .query(status.getUser().getLocation())
                                                                .autocomplete(true)
                                                                .geocodingTypes(
                                                                        GeocodingCriteria.TYPE_POI)
                                                                .mode(GeocodingCriteria.MODE_PLACES)
                                                                .build();
                        client.setCallFactory(factory);

                        client.enqueueCall(new Callback<GeocodingResponse>() {
                            @Override
                            public void onResponse(Call<GeocodingResponse> call, Response<GeocodingResponse> response) {
                                GeocodingResponse body = response.body();
                                if (body == null || body.features().isEmpty()) {
                                    return;
                                }
                                sink.next(new Tweet(
                                        status.getText(),
                                        new double[] {
                                            body.features().get(0).center().longitude(),
                                            body.features().get(0).center().latitude()
                                        },
                                        Arrays.stream(status.getHashtagEntities())
                                              .map(HashtagEntity::getText)
                                              .toArray(String[]::new)
                                ));
                            }

                            @Override
                            public void onFailure(Call<GeocodingResponse> call, Throwable t) {

                            }
                        });
                    }
                }

                @Override
                public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {

                }

                @Override
                public void onTrackLimitationNotice(int numberOfLimitedStatuses) {

                }

                @Override
                public void onScrubGeo(long userId, long upToStatusId) {

                }

                @Override
                public void onStallWarning(StallWarning warning) {

                }

                @Override
                public void onException(Exception ex) {

                }
            }).filter(
                "S1P",
                "SpringOnePlatform",
                "SpringFramework",
                "Java",
                "ReactiveJava",
                "RxJava",
                "Kotlin",
                "RxKotlin",
                "ProjectReactor",
                "ReactiveProgramming",
                "ReactiveSystem",
                "ApacheKafka",
                "Kafka",
                "Akka",
                "AkkaStreams",
                "Alpakka",
                "ReactiveKafka",
                "Scala",
                "RxScala",
                "ReactorScala",
                "ReactiveStreams",
                "Netty",
                "ReactorNetty",
                "RxNetty",
                "WebFlux",
                "Programming",
                "Coding",
                "JavaScript",
                "JS",
                "Computer",
                "Science",
                "Software",
                "RxJs",
                "JDBC",
                "R2DBC"
            );
        })
                .subscribeOn(Schedulers.elastic())
                .log()
//            .filter(s -> s.getPlace() != null || s.getGeoLocation() != null || s.getUser().getLocation() != null)
                .subscribeWith(ReplayProcessor.create(30));

        @GetMapping(value = "/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
        public Flux<ServerSentEvent<Tweet>> commandLineRunner() {
            return tweetsFlux.map(t ->
                ServerSentEvent.builder(t)
                               .build()
            );

//                    .log()
//                    .map(o -> SenderRecord.create(new ProducerRecord<>("tweets", o.getId(), o), o.getId()))
//                    .transform(sender::send)
//                    .blockLast();
//        };
        }
    }


}
