package main.java.client;

/**
 * Created by Starrimus on 29.03.2017.
 */

import java.io.*;
import java.net.Socket;

public class Client {
    public static void main(String[] args) throws IOException {
        Socket sock = new Socket("localhost", 1337);

        MessageReceiver msgReciever = new MessageReceiver(sock);
        new Thread(msgReciever).start();
        System.out.println("message reciever thread started");

        KeyboardScanner keyScanner = new KeyboardScanner(sock);
        new Thread(keyScanner).start();
        System.out.println("keyboard thread started");

        System.out.println("message: ");


    }
}