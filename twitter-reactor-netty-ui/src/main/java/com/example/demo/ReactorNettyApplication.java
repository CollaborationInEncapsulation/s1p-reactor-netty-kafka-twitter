package com.example.demo;

import com.example.demo.mapbox.LocationEnrichService;
import com.example.demo.mapbox.MapboxLocationEnrichService;
import com.example.demo.twitter.KafkaTwitterStreamService;
import com.example.demo.twitter.Tweet;
import com.example.demo.twitter.TwitterStreamService;
import com.example.demo.utils.SseHandler;
import com.example.demo.utils.StaticResourceHandler;
import com.example.demo.utils.WebSocketHandler;
import reactor.core.publisher.Flux;
import reactor.netty.DisposableServer;
import reactor.netty.http.server.HttpServer;

public class ReactorNettyApplication {

    public static void main(String[] args) {
        TwitterStreamService twitterStreamService = new KafkaTwitterStreamService();
        LocationEnrichService locationEnrichService = new MapboxLocationEnrichService();
        Flux<Tweet> tweetsFlux =
                twitterStreamService.stream()
                        .transform(locationEnrichService::enrich)
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
