package server;

import org.springframework.util.Assert;
import server.analyzers.CommandAnalyzer;
import server.enums.ResponseType;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class ServerProperties {
    private final static String FILENAME = "config.properties";
    private final static ServerProperties INSTANCE = new ServerProperties();

    private final Properties properties = new Properties();

    private ServerProperties() {
        try (InputStream is = new FileInputStream(FILENAME)) {
            properties.load(is);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static ServerProperties getInstance() {
        return INSTANCE;
    }

    static int getPort() {
        return Integer.parseInt(getInstance().properties.getProperty("port"));
    }

    static String getName() {
        return getInstance().properties.getProperty("name");
    }

    static int getMaxLoginAttempts() {
        return Integer.parseInt(getInstance().properties.getProperty("max-attempts"));
    }

    public static String getMessage(ResponseType type) {
        String message = getInstance().properties.getProperty(type.name());
        Assert.notNull(message, type.name() + " doesn't have a matching response message!");
        return message;
    }

    public static <T extends CommandAnalyzer> String getKeyword(Class<T> analyzerClass) {
        String keyword = getInstance().properties.getProperty(analyzerClass.getSimpleName());
        Assert.notNull(keyword, analyzerClass.getName() + " doesn't have a matching keyword!");
        return keyword;
    }
}