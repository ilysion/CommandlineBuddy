package server;

import server.runnable.ChatRefresher;

import java.io.IOException;

public class Listener {
    private final UserDataForInMemory userData;

    public Listener(UserDataForInMemory userDataForInMemory) {
        this.userData = userDataForInMemory;
    }

    public void startListening() throws IOException {
        new Thread (new ChatRefresher(userData)).start();
        while (userData.getBundle().getSocket().isConnected()) {
            String userInput = userData.getBundle().getDis().readUTF();
            String[] splittedUserInput = userInput.split(" ");
            new CommandDigester(userData, splittedUserInput).digest();
        }
    }

}
