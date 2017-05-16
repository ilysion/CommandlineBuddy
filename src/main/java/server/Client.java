package server;

import server.enums.LoginStage;

import java.nio.channels.SocketChannel;
import java.util.ArrayDeque;
import java.util.Objects;
import java.util.Queue;

public class Client {
    private String username;
    private final SocketChannel socketChannel;
    private LoginStage stage = LoginStage.LOGGING_IN;
    private int failedLoginAttempts = 0;
    private Queue<String> queuedMessages = new ArrayDeque<String>();

    public void addToQueue(String message) {
        this.queuedMessages.add(message);
    }

    public String getQueuedMessage() {
        return queuedMessages.poll();
    }

    public boolean isQueueEmpty(){
        return queuedMessages.size() == 0;
    }

    void incrementAttempts() {
        failedLoginAttempts++;
    }

    int getAttempts() {
        return failedLoginAttempts;
    }

    void setUsername(String username) {
        this.username = username;
    }

    public Client(SocketChannel socketChannel) {
        this.socketChannel = socketChannel;
    }

    public String getUsername() {
        return Objects.requireNonNull(username, "getUsername() called before username field was initialized.");
    }

    SocketChannel getSocketChannel() {
        return socketChannel;
    }

    public LoginStage getStage() {
        return stage;
    }

    void setStageToLoggedIn() {
            this.stage = LoginStage.LOGGED_IN;
    }

    boolean isNotLoggedIn() {
        return stage.equals(LoginStage.LOGGING_IN);
    }
}
