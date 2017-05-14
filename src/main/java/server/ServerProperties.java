package server;

import org.springframework.util.Assert;
import server.analyzers.CommandAnalyzer;
import server.enums.ResponseType;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

public class ServerProperties {
    //TODO: In terms of performance, all the information in the properties file should only be read once during server startup.

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

    static int getPort() {
        return Integer.parseInt(getInstance().properties.getProperty("port"));
    }

    static String getName() {
        return getInstance().properties.getProperty("name");
    }

    static int getMaxLoginAttempts() {
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
        NOT_ALLOWED("You are not allowed to do that."),
        DISCONNECTED("You have been disconnected from the server."),
        USER_DOES_NOT_EXIST("This user does not exist."),
        INCOMING_ACTIVE_USERS("Active users: "),
        DISCONNECT_REQUEST("Disconnecting user..");
        */
        return null;
    }

    public static String getMessage(ResponseType type) {
        Map<ResponseType, String> responseMessages = getResponseMessages();
        Assert.notNull(responseMessages, "getResponseMessages() should not return null.");
        String message = responseMessages.get(type);
        Assert.notNull(message, "Map returned by getResponseMessages() did not contain the requested ResponseType.");
        return message;
    }

    private static <T extends CommandAnalyzer> Map<Class<T>, String> getAnalyzerKeywords() {
        //TODO: Must return the keyword of all the CommandAnalyzers. The keyword is always inputString.split[0].
        // The current keywords are as follows. They should probably be changed:
        /*
        DisconnectAnalyzer: "disconnect"
        HelpRequestAnalyzer: "help"
        MakeModAnalyzer: "makemod"
        PermaBanAnalyzer: "permaban"
        RestoreUserStanding: "restore"
        SilenceAnalyzer: "silence"
        ViewConnectedUsersAnalyzer: "view"
        */
        return null;
    }

    public static <T extends CommandAnalyzer> String getKeyword(Class<T> analyzerClass) {
        Map<Class<T>, String> keywords = getAnalyzerKeywords();
        Assert.notNull(keywords, "getAnalyzerKeywords() should not return null.");
        String keyword = keywords.get(analyzerClass);
        Assert.notNull(keyword, "Map returned by getResponseMessages() did not contain the requested Analyzer.");
        return keyword;
    }

}
