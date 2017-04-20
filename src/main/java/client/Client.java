package client;


import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) throws IOException {
        try (Socket socket = new Socket("localhost", 1337);
             Scanner keyboard = new Scanner(System.in);
             DataOutputStream outstream = new DataOutputStream(socket.getOutputStream())) {

            MessageReceiver msgReceiver = new MessageReceiver(socket);
            new Thread(msgReceiver).start();
            while (true) {
                if (keyboard.hasNextLine()) {
                    outstream.writeUTF(keyboard.nextLine());
                }
            }
        } catch (ConnectException e) {
            System.out.println("Couldn't connect to the server. Are you sure the server is up?");
            System.exit(1);
        }
    }
}