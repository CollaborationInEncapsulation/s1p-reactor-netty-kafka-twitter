package com.example.demo;

import com.example.demo.twitter.RawTweet;
import com.example.demo.twitter.Twitter4jStreamService;
import com.example.demo.utils.SseHandler;
import com.example.demo.utils.StaticResourceHandler;
import com.example.demo.utils.WebSocketHandler;
import reactor.core.publisher.Flux;
import reactor.netty.DisposableServer;
import reactor.netty.http.server.HttpServer;

public class ReactorNettyApplication {

    public static void main(String[] args) {
        Flux<RawTweet> tweetsFlux = new Twitter4jStreamService().stream();

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
