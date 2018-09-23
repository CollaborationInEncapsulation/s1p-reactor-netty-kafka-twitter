package com.example.demo.retrofit;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import reactor.core.publisher.BaseSubscriber;

class CallbackSubscriber extends BaseSubscriber<Response> {
    final Call originalCall;
    final Callback responseCallback;

    Response response;

    CallbackSubscriber(Call call, Callback callback) {
        originalCall = call;
        responseCallback = callback;
    }

    @Override
    protected void hookOnNext(Response value) {
        response = value;
    }

    @Override
    protected void hookOnComplete() {
        try {
            responseCallback.onResponse(originalCall, response);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void hookOnError(Throwable throwable) {
        responseCallback.onFailure(originalCall, new IOException(throwable));
    }
}
