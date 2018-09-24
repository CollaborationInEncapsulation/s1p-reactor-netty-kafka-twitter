package com.example.demo.retrofit;

import java.io.IOException;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.Okio;
import okio.Sink;
import okio.Timeout;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.ByteBufMono;
import reactor.netty.http.client.HttpClientResponse;

final class RetrofitAdapterUtils {

    static Consumer<? super HttpHeaders> adaptHeaders(Request request) {
        return h -> request.headers()
                           .names()
                           .forEach(n -> h.add(n, request.header(n)));
    }

    static HttpMethod adaptMethod(Request request) {
        return HttpMethod.valueOf(request.method()
                                         .toUpperCase());
    }

    static String adaptURI(Request request) {
        return request.url().toString();
    }

    static Publisher<ByteBuf> adaptBody(Request request) {
        return Flux.create(sink -> {
            try {
                if (request.body() != null) {
                    request.body()
                           .writeTo(Okio.buffer(new Sink() {
                               @Override
                               public void write(Buffer source, long byteCount) throws IOException {
                                   sink.next(ByteBufAllocator.DEFAULT
                                                             .buffer()
                                                             .writeBytes(source.readByteArray(byteCount)));
                               }

                               @Override
                               public void flush() {

                               }

                               @Override
                               public Timeout timeout() {
                                   return Timeout.NONE;
                               }

                               @Override
                               public void close() {
                                   sink.complete();
                               }
                           }));
                } else {
                    sink.complete();
                }
            } catch (IOException e) {
                sink.error(e);
            }
        });
    }

    static BiFunction<HttpClientResponse, ByteBufMono, Mono<Response>> adaptResponse(Request request) {
        return (response, bodyMono) ->
            bodyMono
                .asByteArray()
                .map(bytes -> {
                    Response.Builder builder = new Response.Builder();

                    response.responseHeaders()
                            .entries()
                            .forEach(e -> builder.addHeader(e.getKey(), e.getValue()));

                    return builder.request(request)
                                  .body(ResponseBody.create(null, bytes))
                                  .code(response.status().code())
                                  .protocol(Protocol.HTTP_1_1)
                                  .message(response.status().reasonPhrase())
                                  .build();
                });
    }
}
