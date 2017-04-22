package server.runnable;

import server.*;
import server.conversion.ResponseConverter;
import server.conversion.ResponseType;
import server.singletons.InMemoryDatabase;
import server.singletons.Database;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;

/*This class is ugly and not elegant. It should be cleaned up. -danb*/
public class LoginHandler implements Runnable {
    private final SingleConnectionBundle connectionBundle;

    public LoginHandler(Socket socket) {
        try {
            this.connectionBundle = new SingleConnectionBundle(socket, new DataInputStream(socket.getInputStream()), new DataOutputStream(socket.getOutputStream()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run() {
        try {
            loginInputCycle();
        } catch (IOException | InterruptedException | SQLException e) {
            throw new RuntimeException(e);
        } finally {
            connectionBundle.closeQuietly();
        }
    }

    private void loginInputCycle() throws IOException, InterruptedException, SQLException {
        String loginCycleResult = loginCycle();
        /*NB! if loginCycleResult is null, then the user failed to log in before reaching max amount of attempts*/
        if (loginCycleResult != null) {
            connectUser(loginCycleResult);
        }
        else {
            connectionBundle.getDos().writeUTF(ResponseConverter.convertToStreamable(ResponseType.FORCE_DISCONNECTED));
            connectionBundle.closeQuietly();
        }
    }

    private void connectUser(String correctUsername) throws SQLException, IOException {
        UserDataForInMemory userData = new UserDataForInMemory(correctUsername, connectionBundle);
        InMemoryDatabase.safeAdd(userData);
        new Listener(userData).startListening();
    }

    private String loginCycle() throws InterruptedException, IOException, SQLException {
        for (int i = 0; i < 5; i++) {
            if (connectionBundle.getSocket().isConnected()) {
            /*TYPE will be used in the client side application to digest different types of responses. -danb*/
            /*"register" command can be changed later. -danb*/
                connectionBundle.getDos().writeUTF(ResponseConverter.convertToStreamable(ResponseType.REQUEST_LOGIN_INPUT));
                String userLoginInput = connectionBundle.getDis().readUTF();
                final String[] splittedUserInput = userLoginInput.split(" ");
                if (isLoginAttempt(splittedUserInput)) {
                    if (Database.isValidLogin(splittedUserInput[0], splittedUserInput[1])) {
                        return splittedUserInput[0];
                    }
                    else {
                        connectionBundle.getDos().writeUTF(ResponseConverter.convertToStreamable(ResponseType.INVALID_LOGIN_INPUT));
                    }
                }
                else if (isUserCreationAttempt(splittedUserInput)) {
                    if (isUserCreationSuccessful(splittedUserInput)) {
                        connectionBundle.getDos().writeUTF(ResponseConverter.convertToStreamable(ResponseType.ACCOUNT_CREATED));
                    }
                    else {
                        connectionBundle.getDos().writeUTF(ResponseConverter.convertToStreamable(ResponseType.USERNAME_TAKEN));
                    }
                }
                else {
                    connectionBundle.getDos().writeUTF(ResponseConverter.convertToStreamable(ResponseType.INVALID_LOGIN_FORMAT));
                }
            }
        }
        return null;
    }

    private boolean isUserCreationSuccessful(String[] splittedUserInput) throws SQLException {
        final String proposedUsername = splittedUserInput[1];
        final String proposedPassword = splittedUserInput[2];
        if (Database.isUsernameFree(proposedUsername)) {
            Database.createNewAccount(proposedUsername, proposedPassword);
            return true;
        }
        else {
            return false;
        }
    }

    private boolean isLoginAttempt(String[] splittedUserInput) {
        return splittedUserInput.length == 2;
    }

    private boolean isUserCreationAttempt(String[] splittedUserInput) {
        /*"register" command can be changed if needed. -danb*/
        return splittedUserInput.length == 3 && splittedUserInput[0] == "register";
    }

}
