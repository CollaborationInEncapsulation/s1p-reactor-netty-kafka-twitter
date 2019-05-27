package com.example.demo;

import com.example.demo.mapbox.LocationEnrichService;
import com.example.demo.mapbox.MapboxLocationEnrichService;
import com.example.demo.twitter.Tweet;
import com.example.demo.twitter.Twitter4jStreamService;
import com.example.demo.twitter.TwitterStreamService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.netty.http.server.HttpServer;

public class ReactorNettyApplication {
    private static final Logger LOGGER = LoggerFactory.getLogger(ReactorNettyApplication.class);

    public static void main(String[] args) {
        long startTime = System.nanoTime();
        TwitterStreamService twitterStreamService = new Twitter4jStreamService();
        LocationEnrichService locationEnrichService = new MapboxLocationEnrichService();
        Flux<Tweet> tweetsFlux =
                twitterStreamService.stream()
                                    .transform(locationEnrichService::enrich)
                                    .publish()
                                    .autoConnect(1);


    }
}
