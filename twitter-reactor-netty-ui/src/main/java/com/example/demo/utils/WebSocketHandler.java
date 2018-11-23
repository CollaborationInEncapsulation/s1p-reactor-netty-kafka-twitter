package com.example.demo.utils;

import java.util.function.BiFunction;

import com.example.demo.twitter.RawTweet;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.netty.NettyPipeline;
import reactor.netty.http.websocket.WebsocketInbound;
import reactor.netty.http.websocket.WebsocketOutbound;

public final class WebSocketHandler {

    public static BiFunction<WebsocketInbound, WebsocketOutbound, Publisher<Void>> serveWebsocket(Flux<RawTweet> tweetsFlux) {
        return (in, out) ->
                tweetsFlux.map(SerializingUtils::toByteBuffer)
                          .transform(flux -> out.options(NettyPipeline.SendOptions::flushOnEach)
                                                .sendObject(flux));
    }

}