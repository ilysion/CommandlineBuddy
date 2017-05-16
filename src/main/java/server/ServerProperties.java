package server;

import server.analyzers.CommandAnalyzer;
import server.enums.ResponseType;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Properties;

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
        return Objects.requireNonNull(getInstance().properties.getProperty(type.name()), "No response message found for " + type.name());
    }

    public static <T extends CommandAnalyzer> String getKeyword(Class<T> analyzerClass) {
        return Objects.requireNonNull(getInstance().properties.getProperty(analyzerClass.getSimpleName()), "No keyword found for " + analyzerClass.getSimpleName());
    }
}