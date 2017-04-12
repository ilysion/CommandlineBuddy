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
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

public class Server {
    private static final int PORT = 42031;

    public static void main(String[] args) throws Exception {
        BlockingQueue<String> messages = new ArrayBlockingQueue<String>(50);
        List<Socket> connectedClients = new ArrayList<>();
        ConcurrentHashMap<Socket, DataOutputStream> dos = new ConcurrentHashMap<>();
        ConcurrentHashMap<Socket, DataInputStream> dis = new ConcurrentHashMap<>();

        new Thread(new SendMessage(messages,connectedClients, dos)).start();

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                try {
                    Socket socket = serverSocket.accept();
                    dos.put(socket,new DataOutputStream(socket.getOutputStream()));
                    dis.put(socket,new DataInputStream(socket.getInputStream()));
                    new Thread(new User(messages, socket, dos, dis)).start();
                    connectedClients.add(socket);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        } finally {
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
}
