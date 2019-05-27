package com.example.demo.mapbox;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class MapboxProperties {

    private static final String TOKEN_KEY              = "token";
    private static final String MAPBOX_PROPERTIES_FILE = "/mapbox.properties";

    private final String token;

    public MapboxProperties(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }


    public static MapboxProperties load() {
        return load(MapboxProperties.class.getResourceAsStream(MAPBOX_PROPERTIES_FILE));
    }

    public static MapboxProperties load(InputStream path) {
        Properties properties = new Properties();

        try {
            properties.load(path);
        }
        catch (IOException e) {
            throw  new RuntimeException(e);
        }

        return new MapboxProperties(properties.getProperty(TOKEN_KEY));
    }
}
