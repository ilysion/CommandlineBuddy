package server;

/**
 * Created by CmdBuddyTeam on 29.03.2017.
 */

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Server {
    public static void main(String[] args) throws IOException {
        BlockingQueue<String> messages = new ArrayBlockingQueue<String>(50);
        List<Socket> connectedClients = new ArrayList<>();
        HashMap<Socket, DataOutputStream> dos = new HashMap<>();
        HashMap<Socket, DataInputStream> dis = new HashMap<>();


        new Thread(new SendMessage(messages,connectedClients, dos)).start();

        try (ServerSocket serverSocket = new ServerSocket(42031)) {
            while (true) {
                try {
                    Socket socket = serverSocket.accept();
                    dos.put(socket,new DataOutputStream(socket.getOutputStream()));
                    dis.put(socket,new DataInputStream(socket.getInputStream()));
                    new Thread(new ReceiveMessage(messages, socket, dis)).start();
                    connectedClients.add(socket);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        } catch (IOException e2) {
            e2.printStackTrace();
        }
        for (DataOutputStream dataOutputStream : dos.values()) {
            try {
                dataOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        for (DataInputStream dataInputStream : dis.values()) {
            try {
                dataInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
