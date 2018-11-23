package com.example.demo.twitter;

import java.util.Arrays;

public class RawTweet {

    private final String   id;
    private final String   user;
    private final String   content;
    private final String[] tags;
    private final double[] location;
    private final String   userLocation;

    public RawTweet(String id, String user, String content, String[] tags, double[] location, String userLocation) {
        this.id = id;
        this.user = user;
        this.content = content;
        this.location = location;
        this.tags = tags;
        this.userLocation = userLocation;
    }

    public String getId() {
        return id;
    }

    public String getUser() {
        return user;
    }

    public String getContent() {
        return content;
    }

    public String[] getTags() {
        return tags;
    }

    public double[] getLocation() {
        return location;
    }

    public String getUserLocation() {
        return userLocation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        RawTweet tweet = (RawTweet) o;

        if (!id.equals(tweet.id)) {
            return false;
        }
        return user.equals(tweet.user);
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + user.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "RawTweet{" + "id='" + id + '\'' + ", user='" + user + '\'' + ", content='" + content + '\'' + ", tags=" + Arrays.toString(
                tags) + ", location=" + Arrays.toString(location) + ", userLocation='" + userLocation + '\'' + '}';
    }
}
