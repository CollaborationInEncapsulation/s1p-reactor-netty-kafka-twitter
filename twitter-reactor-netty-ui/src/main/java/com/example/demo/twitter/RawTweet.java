package com.example.demo.twitter;

import java.util.Arrays;

public class RawTweet {

    private String   id;
    private String   user;
    private String   content;
    private String[] tags;
    private double[] location;
    private String   userLocation;

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

    public void setId(String id) {
        this.id = id;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setTags(String[] tags) {
        this.tags = tags;
    }

    public void setLocation(double[] location) {
        this.location = location;
    }

    public void setUserLocation(String userLocation) {
        this.userLocation = userLocation;
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
