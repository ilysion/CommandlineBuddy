package client;

/**
 * Created by CmdBuddyTeam on 29.03.2017.
 */

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) throws IOException {
        try (Socket socket = new Socket("localhost", 42031)) {
            MessageReceiver msgReceiver = new MessageReceiver(socket);
            new Thread(msgReceiver).start();

            try (Scanner keyboard = new Scanner(System.in)) {
                try (DataOutputStream outstream = new DataOutputStream(socket.getOutputStream())) {
                    while (true) {
                        try {
                            if (keyboard.hasNextLine()) {
                                outstream.writeUTF(keyboard.nextLine());
                            }
                        } catch (IOException e) {
                            System.out.println("error: " + e);
                            break;
                        }
                    }
                }
            }
        }
    }
}