package client;

import com.sun.xml.internal.fastinfoset.util.CharArray;

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

                        if(ipValidator(message)){

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
                            }
                        }
                        else{
                            System.out.println("Please enter a valid ip!");
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

    private static boolean ipValidator(String message){
        String ip = message.split(" ")[1];
        String portString = ip.split(":")[1];
        char[] portNumbers = portString.toCharArray();

        for (int i = 0; i < portNumbers.length; i++) {
            if (portNumbers[i] == '0'){
                return false;
            }
            else{
                break;
            }
        }

        int port = Integer.parseInt(portString);
        if (port < 0 || port > 65535){
            return false;
        }
        if (ip.split(":")[0].equals("localhost")){
            return true;
        }

        for (int i = 0; i < 4; i++) {
            String ipString = ip.split(":")[0].split("\\.")[i];
            if(ipString.length() == 2 && ipString.startsWith("0")){
                return false;
            }
            if(ipString.length() == 3 && ipString.startsWith("0") || ipString.length() == 3 && ipString.startsWith("00")){
                return false;
            }

            int ipPart = Integer.parseInt(ipString);
            if (ipPart < 0 || ipPart > 256){
                return false;
            }
        }
        return true;
    }
}
