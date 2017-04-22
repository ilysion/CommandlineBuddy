package client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

//TODO: I PROPOSE THAT THE CLIENT-SIDE (everything in the client package) SHOULD EVENTUALLY BE MOVED INTO A DIFFERENT PROJECT FROM THE SERVER-SIDE.
class Client {
    public static void main(String[] args) throws IOException {
        try (Socket socket = new Socket("localhost", 42031);
             Scanner keyboard = new Scanner(System.in);
             DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
             DataInputStream dis = new DataInputStream(socket.getInputStream())
        ) {
            SingleConnectionBundle bundle = new SingleConnectionBundle(socket, dis, dos);
            new Thread(new MessageReceiver(bundle)).start();
            while (socket.isConnected()) {
                if (keyboard.hasNextLine()) {
                    dos.writeUTF(keyboard.nextLine());
                }
            }
            System.out.println("Disconnected from server.");
        }
    }
}