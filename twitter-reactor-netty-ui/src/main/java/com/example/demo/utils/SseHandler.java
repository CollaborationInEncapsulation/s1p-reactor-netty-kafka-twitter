package com.example.demo.utils;

import com.example.demo.twitter.RawTweet;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.netty.NettyPipeline;
import reactor.netty.http.server.HttpServerRequest;
import reactor.netty.http.server.HttpServerResponse;

import java.util.function.BiFunction;

public final class SseHandler {

    public static BiFunction<HttpServerRequest, HttpServerResponse, Publisher<Void>> serveSse(Flux<RawTweet> tweetsFlux) {
        // Integration with Reactor Netty
    }

}