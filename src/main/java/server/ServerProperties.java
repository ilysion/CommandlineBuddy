package server;

import org.springframework.util.Assert;
import server.enums.ResponseType;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

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

    public static int getMaxLoginAttempts() {
        //TODO: To be implemented.
        return 0;
    }

    private static Map<ResponseType, String> getResponseMessages() {
        //TODO: Must return the default messages of all the ResponseTypes.
        // The current default messages are as follows:
        /*
        DISCONNECTED_FAIL_ATTEMPTS("Disconnected from server due to too many unsuccessful connectUser attempts."),
        REQUEST_LOGIN_INPUT("Connected to server.\nIf you have previously created an account, please enter your username and password in following format: '<username> <password>'.\nIf you want to create a new account, please enter your desired username and password in the following format: 'register <username> <password>'."),
        INVALID_LOGIN_INPUT("Invalid login. Try again."),
        ACCOUNT_CREATED("UserDataForInMemory successfully created. You can now log in."),
        USERNAME_TAKEN("Username already taken. Try again."),
        INVALID_LOGIN_FORMAT("Invalid response format. Try again."),
        INVALID_COMMAND("Invalid command. Try again."),
        HELP_NORMAL("The following commands are available to you:\nhelp\nsend <message>\nleave'\nview"),
        HELP_MOD("The following commands are available to you:\nhelp\nsend <message>\nleave\nview\npermaban <username>\nremoveban <username>"),
        HELP_ADMIN("The following commands are available to you:\nhelp\nsend <message>\nleave\nview\npermaban <username>\nremoveban <username>\nmakemod <username>\nremovemod <username>"),
        GENERAL_SUCCESS("Command executed successfully."),
        NOT_ALLOWED("You are not allowed to do that.");
        */
        return null;
    }

    public static String getMessageOfResponse(ResponseType type) {
        Map<ResponseType, String> responseMessages = getResponseMessages();
        Assert.notNull(responseMessages, "getResponseMessages() should not return null.");
        String message = getResponseMessages().get(type);
        Assert.notNull(message, "Map returned by getResponseMessages() did not the requested ResponseType.");
        return message;
    }
}
