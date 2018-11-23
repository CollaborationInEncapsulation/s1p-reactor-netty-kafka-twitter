package com.example.demo;

import com.example.demo.mapbox.LocationEnrichService;
import com.example.demo.mapbox.MapboxLocationEnrichService;
import com.example.demo.twitter.KafkaTwitterStreamService;
import com.example.demo.twitter.Tweet;
import com.example.demo.twitter.TwitterStreamService;
import com.example.demo.utils.SseHandler;
import com.example.demo.utils.StaticResourceHandler;
import com.example.demo.utils.WebSocketHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;
import reactor.function.TupleUtils;
import reactor.netty.DisposableServer;
import reactor.netty.http.server.HttpServer;
import reactor.util.function.Tuples;

import java.util.concurrent.TimeUnit;

public class ReactorNettyApplication {
    public static final Logger LOG = LoggerFactory.getLogger(ReactorNettyApplication.class);

    public static void main(String[] args) {
        TwitterStreamService twitterStreamService = new KafkaTwitterStreamService();
        LocationEnrichService locationEnrichService = new MapboxLocationEnrichService();
        Flux<Tweet> tweetsFlux =
                twitterStreamService.stream()
                        .transform(locationEnrichService::enrich)
                        .map(d -> Tuples.of(System.nanoTime(), d))
                        .log()
                        .log()
                        .log()
                        .log()
                        .log()
                        .log()
                        .log()
                        .log()
                        .log()
                        .log()
                        .log()
                        .log()
                        .log()
                        .log()
                        .log()
                        .log()
                        .log()
                        .log()
                        .log()
                        .log()
                        .log()
                        .log()
                        .log()
                        .doOnNext(TupleUtils.consumer((timestamp, tweet) -> {
                            LOG.error("Execution Time {}", System.nanoTime() - timestamp);
                            tweet.toString();
                        }))
                        .map(TupleUtils.function((t, tweet) -> tweet))
                        .publish()
                        .autoConnect(1);

        HttpServer.create()
                .port(8080)
                .route(r -> r.get("/sse", SseHandler.serveSse(tweetsFlux))
                        .ws("/ws", WebSocketHandler.serveWebsocket(tweetsFlux))
                        .get("/{fileName}", StaticResourceHandler.serveResource())
                        .get("/data/{fileName}", StaticResourceHandler.serveResource()))
                .wiretap(true)
                .bind()
                .flatMap(DisposableServer::onDispose)
                .block();
    }
}
