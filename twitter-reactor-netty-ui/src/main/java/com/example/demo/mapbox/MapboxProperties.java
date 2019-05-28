package com.example.demo.mapbox;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Properties;

public class MapboxProperties {

    private final String token;

    public MapboxProperties(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }



    static final String TOKEN_KEY = "token";
    static final String MAPBOX_PROPERTIES_FILE = "mapbox.properties";

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
