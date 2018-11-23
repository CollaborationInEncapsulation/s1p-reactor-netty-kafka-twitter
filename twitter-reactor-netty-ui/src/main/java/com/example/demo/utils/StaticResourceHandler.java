package com.example.demo.utils;

import org.reactivestreams.Publisher;
import reactor.netty.http.server.HttpServerRequest;
import reactor.netty.http.server.HttpServerResponse;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.BiFunction;

public final class StaticResourceHandler {

    public static BiFunction<HttpServerRequest, HttpServerResponse, Publisher<Void>> serveResource() {
        return (req, res) -> {
            String uri = req.path();

            Path path = Paths.get(resourceUri).resolve(("".equals(uri) ? "index.html" : uri));

            return res.sendFile(path);
        };
    }




    static final URI resourceUri;
    static {
        URI uri;
        try {
            uri = ClassLoader.getSystemResource("static").toURI();
        } catch (URISyntaxException e) {
            uri = null;
        }
        resourceUri = uri;
    }

}
