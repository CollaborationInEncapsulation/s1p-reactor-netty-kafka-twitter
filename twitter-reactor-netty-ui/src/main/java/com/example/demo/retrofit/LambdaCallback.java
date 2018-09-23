package com.example.demo.retrofit;

import java.util.function.BiConsumer;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LambdaCallback<T> implements Callback<T> {

    final BiConsumer<? super Response<T>,? super Throwable> biConsumer;

    LambdaCallback(BiConsumer<? super Response<T>, ? super Throwable> consumer) {
        biConsumer = consumer;
    }

    @Override
    public void onResponse(Call<T> call, Response<T> response) {
        biConsumer.accept(response, null);
    }

    @Override
    public void onFailure(Call<T> call, Throwable t) {
        biConsumer.accept(null, t);
    }

    public static <T> LambdaCallback<T> create(BiConsumer<Response<T>, Throwable> biConsumer) {
        return new LambdaCallback<>(biConsumer);
    }
}
