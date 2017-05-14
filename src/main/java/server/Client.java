package server;

import org.springframework.util.Assert;
import server.enums.LoginStage;

import java.nio.channels.SocketChannel;

public class Client {
    private String username;
    private final SocketChannel socketChannel;
    private LoginStage stage = LoginStage.LOGGING_IN;
    private int failedLoginAttempts = 0;

    public void incrementAttempts() {
        failedLoginAttempts++;
    }

    public int getAttempts() {
        return failedLoginAttempts;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Client(SocketChannel socketChannel) {
        this.socketChannel = socketChannel;
    }

    public String getUsername() {
        Assert.notNull(username, "getUsername() called before username field was initialized.");
        return username;
    }

    public SocketChannel getSocketChannel() {
        return socketChannel;
    }

    public LoginStage getStage() {
        return stage;
    }

    public void setStage(LoginStage stage) {
        if (stage != null) {
            this.stage = stage;
        }
    }

    public boolean isNotLoggedIn() {
        return stage.equals(LoginStage.LOGGING_IN);
    }
}
