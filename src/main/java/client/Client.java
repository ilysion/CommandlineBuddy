package client;


import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) throws IOException {
        try (Socket socket = new Socket("localhost", 42031);
             Scanner keyboard = new Scanner(System.in);
             DataOutputStream outstream = new DataOutputStream(socket.getOutputStream())) {

            MessageReceiver msgReceiver = new MessageReceiver(socket);
            new Thread(msgReceiver).start();
            while (true) {
                if (keyboard.hasNextLine()) {
                    outstream.writeUTF(keyboard.nextLine());
                }
            }
        }
    }
}