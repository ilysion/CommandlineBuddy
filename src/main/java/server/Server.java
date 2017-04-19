package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
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
        new Thread(new SendMessage(messages, connectedClients, dos)).start();
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket socket = serverSocket.accept();
                dos.put(socket, new DataOutputStream(socket.getOutputStream()));
                dis.put(socket, new DataInputStream(socket.getInputStream()));
                new Thread(new User(messages, socket, dos, dis)).start();
                connectedClients.add(socket);
            }
        } finally {
            for (DataOutputStream dataOutputStream : dos.values()) {
                dataOutputStream.close();
            }
            for (DataInputStream dataInputStream : dis.values()) {
                dataInputStream.close();
            }
        }
    }
}
