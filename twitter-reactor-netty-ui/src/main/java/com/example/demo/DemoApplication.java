package com.example.demo;

import reactor.kafka.sender.KafkaSender;
import reactor.netty.DisposableServer;
import reactor.netty.http.server.HttpServer;

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

        DisposableServer server =
                HttpServer.create()
                          .port(9000)
                          .route(r -> r.get("/sse", SseHandler.serveSse())
                                       .ws("/ws", WebSocketHandler.serveWebsocket())
                                       .get("/{fileName}", StaticResourceHandler.serveResource())
                                       .get("/data/{fileName}", StaticResourceHandler.serveResource()))
                          .wiretap()
                          .bindNow();

        server.onDispose()
              .block();
    }
}
