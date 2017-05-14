package server;

import org.springframework.util.Assert;
import server.enums.LoginInputType;
import server.enums.LoginStage;
import server.enums.ResponseType;
import server.enums.UserStanding;

public class InputHandler {

    private final Client client;
    private final String input;
    private String username;
    private String password;

    public InputHandler(Client client, String input) {
        this.client = client;
        this.input = input;
    }

    public ResponseType handleLoginAndGenerateResponse() {
        LoginInputType type = digestInput();
        if (Database.getUserStanding(client.getUsername()).equals(UserStanding.BANNED)) {
            return ResponseType.NOT_ALLOWED;
        }
        else if (type.equals(LoginInputType.INVALID_FORMAT)) {
            return ResponseType.INVALID_LOGIN_FORMAT;
        }
        else if (type.equals(LoginInputType.LOGIN_ATTEMPT)) {
            Assert.notNull(username, "Username value null after recognizing LOGIN_ATTEMPT.");
            Assert.notNull(password, "Password value null after recognizing LOGIN_ATTEMPT.");
            boolean isCorrect = Database.checkLogin(username, password);
            if (isCorrect) {
                client.setStage(LoginStage.LOGGED_IN);
                client.setUsername(username);
                return ResponseType.GENERAL_SUCCESS;
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
        else {
            Assert.notNull(username, "Username value null after recognizing REGISTRATION_ATTEMPT.");
            Assert.notNull(password, "Password value null after recognizing REGISTRATION_ATTEMPT.");
            boolean isCorrect = Database.isUsernameFree(username);
            if (isCorrect) {
                Database.createNewAccount(username, password);
                return ResponseType.ACCOUNT_CREATED;
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
    }

    private LoginInputType digestInput() {
        String[] splittedInput = input.split(" ");
        int amountOfWords = splittedInput.length;
        if (splittedInput[0].equals("reg") && amountOfWords==3) {
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
}
