package com.example.demo;

import java.util.Arrays;
import java.util.Objects;

public class Tweet {

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Tweet tweet = (Tweet) o;

        if (!Objects.equals(content, tweet.content)) {
            return false;
        }
        if (!Arrays.equals(location, tweet.location)) {
            return false;
        }
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        return Arrays.equals(tags, tweet.tags);
    }

    @Override
    public int hashCode() {
        int result = content != null ? content.hashCode() : 0;
        result = 31 * result + Arrays.hashCode(location);
        result = 31 * result + Arrays.hashCode(tags);
        return result;
    }

    public String getContent() {
        return content;
    }

    public double[] getLocation() {
        return location;
    }

    public String[] getTags() {
        return tags;
    }

    @Override
    public String toString() {
        return "Tweet{" + "content='" + content + '\'' + ", location=" + Arrays.toString(
                location) + ", tags=" + Arrays.toString(tags) + '}';
    }

    private final String   content;
    private final double[] location;
    private final String[] tags;

    public Tweet(String content, double[] location, String[] tags) {
        this.content = content;
        this.location = location;
        this.tags = tags;
    }


}
