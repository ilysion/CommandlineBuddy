package client;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.util.Objects;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) throws IOException {
        boolean quit = false;

        System.out.println("To connect to a server, type connect IP:PORT in CMD.");
        System.out.println("To quit the program type :quit");

        try (Scanner keyboard = new Scanner(System.in)) {
            while (!quit) {
                if (keyboard.hasNextLine()) {
                    String message = keyboard.nextLine();
                    if (message.startsWith("connect") && message.contains(":") && message.split("[ :]").length == 3) {
                        // TODO ipv4 validator
                        String[] address = message.split("[ :]");
                        try (Socket socket = new Socket(address[1], Integer.parseInt(address[2]));
                             DataOutputStream dos = new DataOutputStream(socket.getOutputStream())) {

                            System.out.println("Connected to " + address[1] + ":" + address[2]);

                            MessageReceiver messageReceiver = new MessageReceiver(socket);
                            Thread msgThread = new Thread(messageReceiver);

                            msgThread.start();

                            while (!quit) {
                                if (keyboard.hasNextLine()) {
                                    message = keyboard.nextLine();
                                    if (!socket.isClosed()) {
                                        if (Objects.equals(message, ":quit")) {
                                            System.out.println("Shutting down!");
                                            msgThread.interrupt();
                                            quit = true;
                                        } else {
                                            dos.writeUTF(message);
                                        }
                                    } else {
                                        quit = true;
                                    }
                                }
                            }

                        } catch (ConnectException e) {
                            System.err.println("Unable to connect to the server. Are you sure the server is up?");
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }

                    } else if (Objects.equals(message, ":quit")) {
                        System.out.println("Shutting down!");
                        quit = true;
                    } else {
                        System.out.println("Please enter a valid command!");
                    }
                }
            }
        }
    }
}