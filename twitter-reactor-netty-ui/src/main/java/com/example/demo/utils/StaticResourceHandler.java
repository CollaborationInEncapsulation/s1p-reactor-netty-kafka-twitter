package com.example.demo.utils;

import org.apache.commons.io.IOUtils;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;
import reactor.netty.http.server.HttpServerRequest;
import reactor.netty.http.server.HttpServerResponse;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.BiFunction;

public final class StaticResourceHandler {

    public static BiFunction<HttpServerRequest, HttpServerResponse, Publisher<Void>> serveResource() {
        return (req, res) -> {
            String uri = req.path();

            try {
                byte[] byteArray  = IOUtils.resourceToByteArray("/static/" + ("".equals(uri)  ? "index.html" : uri));
                return res.sendByteArray(Mono.just(byteArray));
            }
            catch (IOException e) {
                return Mono.error(e);
            }
        };
    }




    static final URI resourceUri;
    static {
        URI uri;
        try {
            uri = StaticResourceHandler.class.getResource("/static").toURI();
        } catch (URISyntaxException e) {
            uri = null;
        }
        resourceUri = uri;
    }

}
