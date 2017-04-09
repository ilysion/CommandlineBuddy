package client;

/**
 * Created by CmdBuddyTeam on 29.03.2017.
 */

import java.io.*;
import java.net.Socket;

public class Client {
    public static void main(String[] args) throws IOException {
        Socket sock = new Socket("localhost", 42031);

        MessageReceiver msgReceiever = new MessageReceiver(sock);
        new Thread(msgReceiever).start();
        System.out.println("message reciever thread started");

        KeyboardScanner keyScanner = new KeyboardScanner(sock);
        new Thread(keyScanner).start();
    }
}