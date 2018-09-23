package com.example.demo.retrofit;

import okhttp3.Call;
import okhttp3.Request;
import reactor.netty.http.client.HttpClient;

public class ReactorNettyCallFactory implements Call.Factory {

    final HttpClient client;

    public ReactorNettyCallFactory() {
        this(HttpClient.create());
    }

    public ReactorNettyCallFactory(HttpClient client) {
        this.client = client;
    }

    @Override
    public Call newCall(Request request) {

        return new ReactorNettyCall(
            request,
            client
                .headers(RetrofitAdapterUtils.adaptHeaders(request))
                .wiretap()
                .request(RetrofitAdapterUtils.adaptMethod(request))
                .uri(RetrofitAdapterUtils.adaptURI(request))
                .send(RetrofitAdapterUtils.adaptBody(request))
                .responseSingle(RetrofitAdapterUtils.adaptResponse(request))
        );
    }
}
