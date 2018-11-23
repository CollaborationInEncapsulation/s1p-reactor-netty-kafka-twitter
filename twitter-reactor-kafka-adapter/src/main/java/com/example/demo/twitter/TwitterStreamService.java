package com.example.demo.twitter;

import reactor.core.publisher.Flux;

public interface TwitterStreamService {

    Flux<RawTweet> stream();
}
