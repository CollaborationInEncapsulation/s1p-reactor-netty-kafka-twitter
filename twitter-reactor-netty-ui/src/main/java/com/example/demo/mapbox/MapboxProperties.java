package com.example.demo.mapbox;

import java.io.IOException;
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
        return load(Paths.get(ClassLoader.getSystemResource(MAPBOX_PROPERTIES_FILE).getFile()));
    }

    public static MapboxProperties load(Path path) {
        Properties properties = new Properties();

        try {
            properties.load(Files.newInputStream(path, StandardOpenOption.READ));
        }
        catch (IOException e) {
            throw  new RuntimeException(e);
        }

        return new MapboxProperties(properties.getProperty(TOKEN_KEY));
    }
}
