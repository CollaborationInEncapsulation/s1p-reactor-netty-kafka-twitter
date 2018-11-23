package com.example.demo.mapbox;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.example.demo.retrofit.LambdaCallback;
import com.example.demo.twitter.RawTweet;
import com.example.demo.twitter.Tweet;
import com.mapbox.api.geocoding.v5.GeocodingCriteria;
import com.mapbox.api.geocoding.v5.MapboxGeocoding;
import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import reactor.cache.CacheMono;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Signal;
import reactor.okhttp.netty.ReactorNettyCallFactory;

public class MapboxLocationEnrichService implements LocationEnrichService {

    final ReactorNettyCallFactory nettyCallFactory = new ReactorNettyCallFactory();
    final MapboxProperties mapboxProperties = MapboxProperties.load();
    final Map<String, Signal<? extends double[]>> placeCoordinatesMap = new ConcurrentHashMap<>();

    @Override
    public Flux<Tweet> enrich(Flux<RawTweet> rawTweetFlux) {
        return rawTweetFlux
                .flatMap(rawTweet -> {
                    if (rawTweet.getLocation().length == 0) {

                        // Introduce Cache

                        return Mono.create(sink -> {
                                    MapboxGeocoding client = MapboxGeocoding
                                            .builder()
                                            .accessToken(mapboxProperties.getToken())
                                            .query(rawTweet.getUserLocation())
                                            .autocomplete(true)
                                            .geocodingTypes(GeocodingCriteria.TYPE_POI)
                                            .mode(GeocodingCriteria.MODE_PLACES)
                                            .build();

                                    client.setCallFactory(nettyCallFactory);

                                    client.enqueueCall(LambdaCallback.create((response, e) -> {
                                        if(response == null || response.body() == null || response.body().features().isEmpty()) {
                                            sink.success();
                                            return;
                                        }

                                        List<CarmenFeature> features = response.body()
                                                .features();

                                        sink.success(
                                                new double[]{
                                                        features.get(0).center().longitude(),
                                                        features.get(0).center().latitude()
                                                }
                                        );
                                    }));
                                }))
                                .map(coordinates -> new Tweet(
                                        rawTweet.getId(),
                                        rawTweet.getUser(),
                                        rawTweet.getContent(),
                                        rawTweet.getTags(),
                                        coordinates
                                ));
                    }

                    return Mono.just(new Tweet(
                            rawTweet.getId(),
                            rawTweet.getUser(),
                            rawTweet.getContent(),
                            rawTweet.getTags(),
                            rawTweet.getLocation()
                    ));
                });
    }
}