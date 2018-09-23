package com.example.demo.twitter;

import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;

@FunctionalInterface
public interface SimpleStatusListener extends StatusListener {

    @Override
    default void onException(Exception ex) {

    }

    void onStatus(Status status);

    @Override
    default void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {

    }

    @Override
    default void onTrackLimitationNotice(int numberOfLimitedStatuses) {

    }

    @Override
    default void onScrubGeo(long userId, long upToStatusId) {

    }

    @Override
    default void onStallWarning(StallWarning warning) {

    }
}
