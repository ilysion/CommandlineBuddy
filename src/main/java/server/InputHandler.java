package server;

import server.analyzers.*;
import server.enums.LoginInputType;
import server.enums.ResponseType;
import server.enums.UserStanding;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

class InputHandler {

    private final Client client;
    private final String input;
    private String username;
    private String password;
    private final List<CommandAnalyzer> availableAnalyzers = Arrays.asList(new HelpRequestAnalyzer(), new ViewConnectedUsersAnalyzer(), new PermaBanAnalyzer(), new SilenceAnalyzer(), new RestoreUserStanding(), new MakeModAnalyzer(), new DisconnectAnalyzer());
    private CommandAnalyzer selectedAnalyzer;


    InputHandler(Client client, String input) {
        this.client = client;
        this.input = input;
    }

    boolean isCommand() {
        for (CommandAnalyzer analyzer : availableAnalyzers) {
            if (analyzer.isValidCommand(input.split(" "))) {
                selectedAnalyzer = analyzer;
                return true;
            }
        }
        return false;
    }

    ResponseType handleLoginAndGenerateResponse() {
        LoginInputType type = digestInput();
        if (type.equals(LoginInputType.INVALID_FORMAT)) {
            return ResponseType.INVALID_LOGIN_FORMAT;
        }
        else if (type.equals(LoginInputType.LOGIN_ATTEMPT)) {
            Objects.requireNonNull(username, "Username not initialized before LOGIN_ATTEMPT.");
            Objects.requireNonNull(password, "Password not initialized before LOGIN_ATTEMPT.");
            boolean isCorrect = Database.checkLogin(username, password);
            if (isCorrect) {
                if (UserStanding.BANNED.equals(Database.getUserStanding(username))) {
                    return ResponseType.NOT_ALLOWED;
                }
                else {
                    client.setStageToLoggedIn();
                    client.setUsername(username);
                    client.setUserStanding(Database.getUserStanding(client.getUsername()));
                    return ResponseType.GENERAL_SUCCESS;
                }
            }
            else {
                client.incrementAttempts();
                if (client.getAttempts() > ServerProperties.getMaxLoginAttempts()) {
                    return ResponseType.DISCONNECTED_FAIL_ATTEMPTS;
                }
                else {
                    return ResponseType.INVALID_LOGIN_INPUT;
                }
            }
        }
        else if (type.equals(LoginInputType.REGISTRATION_ATTEMPT)){
            Objects.requireNonNull(username, "Username not initialized before REGISTRATION_ATTEMPT.");
            Objects.requireNonNull(password, "Password not initialized before REGISTRATION_ATTEMPT.");
            boolean userExists = Database.doesUserExist(username);
            if (!userExists) {
                Database.createNewAccount(username, password);
                return ResponseType.ACCOUNT_CREATED;
            }
            else {
                client.incrementAttempts();
                if (client.getAttempts() > ServerProperties.getMaxLoginAttempts()) {
                    return ResponseType.DISCONNECTED_FAIL_ATTEMPTS;
                }
                else {
                    return ResponseType.USERNAME_TAKEN;
                }
            }
        }
        throw new IllegalStateException("type value is null. digestInput() should never return null.");
    }

    private LoginInputType digestInput() {
        String[] splittedInput = input.split(" ");
        int amountOfWords = splittedInput.length;
        if (splittedInput[0].equals("register") && amountOfWords==3) {
            username = splittedInput[1];
            password = splittedInput[2];
            return LoginInputType.REGISTRATION_ATTEMPT;
        }
        else if (amountOfWords == 2) {
            username = splittedInput[0];
            password = splittedInput[1];
            return LoginInputType.LOGIN_ATTEMPT;
        }
        else {
            return LoginInputType.INVALID_FORMAT;
        }
    }

    ResponseType handleCommandAndGenerateResponse() {
        Objects.requireNonNull(selectedAnalyzer, "selectedAnalyzer not initialized before handling command.");
        return selectedAnalyzer.analyze(client, input.split(" "));
    }
}
