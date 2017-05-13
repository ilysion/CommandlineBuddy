package server;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created on 2017-05-10.
 */

public class ServerProperties {
    private final static String FILENAME = "config.properties";
    private final static ServerProperties INSTANCE = new ServerProperties();

    private final Properties properties = new Properties();

    private ServerProperties() {
        try (InputStream is = new FileInputStream(FILENAME)) {
            properties.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static ServerProperties getInstance() {
        return INSTANCE;
    }

    public static int getPort() {
        return Integer.parseInt(getInstance().properties.getProperty("port"));
    }

    public static String getName() {
        return getInstance().properties.getProperty("name");
    }
}
