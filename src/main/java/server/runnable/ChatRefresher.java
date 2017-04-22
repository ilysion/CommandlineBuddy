package server.runnable;

import server.singletons.Database;
import server.UserDataForInMemory;

import java.io.IOException;
import java.util.List;

public class ChatRefresher implements Runnable {
    private final UserDataForInMemory userData;

    public ChatRefresher(UserDataForInMemory userData) {
        this.userData = userData;
    }

    @Override
    public void run() {
        try {
            while (userData.getBundle().getSocket().isConnected()) {
                userData.getBundle().getDos().writeUTF(getChatLog());
                Thread.sleep(1000);
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            userData.getBundle().closeQuietly();
        }
    }

    private String getChatLog() {
        String chatLogString = "[]";
        List<String> chatLog = Database.getChatLog();
        for (String chatEntry : chatLog) {
            chatLogString += "[" + chatEntry + "] ";
        }
        return chatLogString;
    }
}
