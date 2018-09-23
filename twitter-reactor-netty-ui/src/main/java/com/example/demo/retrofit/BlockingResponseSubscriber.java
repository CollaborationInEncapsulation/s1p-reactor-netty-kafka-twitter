package com.example.demo.retrofit;

import java.util.concurrent.CountDownLatch;

import okhttp3.Response;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.SignalType;

public class BlockingResponseSubscriber extends BaseSubscriber<Response> {
    final CountDownLatch countDownLatch = new CountDownLatch(1);

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
        countDownLatch.countDown();
    }

    public Response block() {
        try {
            countDownLatch.await();
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }

        if (throwable != null) {
            throw new RuntimeException(throwable);
        }

        return response;
    }
}
