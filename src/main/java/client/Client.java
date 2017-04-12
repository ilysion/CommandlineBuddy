package client;

/**
 * Created by CmdBuddyTeam on 29.03.2017.
 */

import java.io.*;
import java.net.Socket;

public class Client {
    public static void main(String[] args) throws IOException {
        try (Socket sock = new Socket("localhost", 42031)) {

            MessageReceiver msgReceiever = new MessageReceiver(sock);
            new Thread(msgReceiever).start();

            KeyboardScanner keyScanner = new KeyboardScanner(sock);
            new Thread(keyScanner).start();
        }
    }
}