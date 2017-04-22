package server.singletons;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/*This is a singleton.*/
public class ServerProperties {
    /*not sure if this is the best way to create universal access to properties info, but it seems to work*/
    //TODO: THESE ARE EXAMPLES OF PROPERTY FIELDS THAT THE SERVER CLASS MIGHT NEED. IF THERE ARE OTHER ONES AS WELL, THEY CAN BE ADDED TO THIS CLASS. -danb
    private final int portValue;
    private final int maxConnectionsValue;

    private static ServerProperties SERVER_PROPERTIES_INSTANCE = null;

    private static ServerProperties getServerProperties() throws IOException{
        if (SERVER_PROPERTIES_INSTANCE == null) {
            SERVER_PROPERTIES_INSTANCE = new ServerProperties();
        }
        return SERVER_PROPERTIES_INSTANCE;
    }


    private ServerProperties() throws IOException {
        Path propertiesFilePath = Paths.get("PLACEHOLDER_PROPERTIES_FILEPATH");
        try (InputStream inputStream = Files.newInputStream(propertiesFilePath)) {
            //TODO: READ PROPERTIES INFO FROM PROPERTIES FILE (I DON'T KNOW THE FORMAT OF THIS FILE) -danb
                /*THE FOLLOWING VALUES ARE PLACEHOLDERS -danb*/
            this.portValue = 0;
            this.maxConnectionsValue = 0;
        }

    }

    public static int getPortValue() throws IOException {
        return getServerProperties().portValue;
    }

    static int getMaxConnectionsValue() throws IOException {
        return getServerProperties().maxConnectionsValue;
    }

}
