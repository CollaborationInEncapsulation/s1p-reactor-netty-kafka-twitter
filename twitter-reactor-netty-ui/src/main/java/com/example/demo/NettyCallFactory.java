package com.example.demo;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.HttpMethod;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.Okio;
import okio.Sink;
import okio.Timeout;
import reactor.core.Disposable;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SignalType;
import reactor.netty.http.client.HttpClient;

public class NettyCallFactory implements Call.Factory {

    private final HttpClient client;

    public NettyCallFactory() {
        this(HttpClient.create());
    }

    public NettyCallFactory(HttpClient client) {
        this.client = client;
    }

    @Override
    public Call newCall(Request request) {
        Objects.requireNonNull(request);

        class NettyCall implements Call {
            final AtomicReference<Disposable> disposable = new AtomicReference<>();
            final Mono<Response> executable = client
                .baseUrl(request.url().toString())
                .request(HttpMethod.valueOf(request.method().toUpperCase()))
                .send(Flux.create(sink -> {
                    try {
                        if (request.body() != null) {
                            request
                                .body()
                                .writeTo(Okio.buffer(new Sink() {
                                    @Override
                                    public void write(Buffer source, long byteCount) throws IOException {
                                        sink.next(Unpooled.copiedBuffer(source.readByteArray(byteCount)));
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
                        }
                        else {
                            sink.complete();
                        }
                    }
                    catch (IOException e) {
                        sink.error(e);
                    }
                }))
                .responseSingle((response, bodyMono) -> bodyMono
                    .asByteArray()
                    .map(bytes -> {
                        Response.Builder builder = new Response.Builder().request(request);
                        response.responseHeaders()
                                .entries()
                                .forEach(e -> builder.addHeader(e.getKey(), e.getValue()));

                        return builder
                           .body(ResponseBody.create(null, bytes))
                           .code(response.status().code())
                           .protocol(Protocol.HTTP_1_1)
                           .message(response.status().reasonPhrase())
                           .build();
                    })
                );

            @Override
            public Request request() {
                return request;
            }

            @Override
            public Response execute() {
                CountDownLatch latch = new CountDownLatch(1);
                class ResponseSubscriber extends
                                         BaseSubscriber<Response> {
                    Response response;
                    Throwable throwable;
                    @Override
                    protected void hookOnNext(Response value) {
                        response = value;
                    }

                    @Override
                    protected void hookOnError(Throwable e) {
                        throwable = e;
                    }

                    @Override
                    protected void hookFinally(SignalType type) {
                        latch.countDown();
                    }
                }
                ResponseSubscriber subscriber = new ResponseSubscriber();

                if (disposable.compareAndSet(null, subscriber)) {
                    executable.subscribe(subscriber);
                    try {
                        latch.await();
                        if (subscriber.throwable != null) {
                            throw new RuntimeException(subscriber.throwable);
                        }
                        return subscriber.response;
                    }
                    catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }

                throw new IllegalStateException();
            }

            @Override
            public void enqueue(Callback responseCallback) {
                class CallbackSubscriber extends BaseSubscriber<Response> {

                    Response response;

                    @Override
                    protected void hookOnNext(Response value) {
                        response = value;
                    }

                    @Override
                    protected void hookOnComplete() {
                        try {
                            responseCallback.onResponse(NettyCall.this, response);
                        }
                        catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    @Override
                    protected void hookOnError(Throwable throwable) {
                        responseCallback.onFailure(NettyCall.this, new IOException(throwable));
                    }
                }

                CallbackSubscriber subscriber = new CallbackSubscriber();

                if (disposable.compareAndSet(null, subscriber)) {
                    executable.subscribe(subscriber);
                }

                throw new IllegalStateException();
            }

            @Override
            public void cancel() {
                Disposable disposable = this.disposable.get();

                if (disposable != null) {
                    disposable.dispose();
                }
            }

            @Override
            public boolean isExecuted() {
                return disposable.get() != null;
            }

            @Override
            public boolean isCanceled() {
                Disposable disposable = this.disposable.get();

                return disposable != null && disposable.isDisposed();
            }

            @Override
            public Call clone() {
                return new NettyCall();
            }
        }

        return new NettyCall();
    }
}
