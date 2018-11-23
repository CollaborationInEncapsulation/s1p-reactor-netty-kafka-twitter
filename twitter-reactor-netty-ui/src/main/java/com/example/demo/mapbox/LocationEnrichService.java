package com.example.demo.mapbox;

import com.example.demo.twitter.RawTweet;
import com.example.demo.twitter.Tweet;
import reactor.core.publisher.Flux;

public interface LocationEnrichService {

    Flux<Tweet> enrich(Flux<RawTweet> rawTweetFlux);
}
