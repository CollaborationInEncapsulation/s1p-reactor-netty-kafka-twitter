package com.example.demo.retrofit;

import java.util.concurrent.atomic.AtomicReference;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;
import reactor.core.Disposable;
import reactor.core.publisher.Mono;

public class ReactorNettyCall implements Call {
    final AtomicReference<Disposable> disposable = new AtomicReference<>();

    final Request request;
    final Mono<Response> executable;

    public ReactorNettyCall(Request request, Mono<Response> executable) {
        this.request = request;
        this.executable = executable;
    }

    @Override
    public Request request() {
        return request;
    }

    @Override
    public Response execute() {
        BlockingResponseSubscriber subscriber = new BlockingResponseSubscriber();

        if (disposable.compareAndSet(null, subscriber)) {
            executable.subscribe(subscriber);

            return subscriber.block();
        }

        throw new IllegalStateException();
    }

    @Override
    public void enqueue(Callback responseCallback) {
        CallbackSubscriber subscriber = new CallbackSubscriber(this, responseCallback);

        if (disposable.compareAndSet(null, subscriber)) {
            executable.subscribe(subscriber);
            return;
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
        return new ReactorNettyCall(request, executable);
    }
}
