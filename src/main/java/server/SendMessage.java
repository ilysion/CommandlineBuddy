package server;

import java.io.DataOutputStream;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;


public class SendMessage implements Runnable {
    private final BlockingQueue<String> messages;
    private final List<Socket> connectedClients;
    private final ConcurrentHashMap<Socket, DataOutputStream> dos;

    SendMessage(BlockingQueue<String> messages, List<Socket> connectedClients, ConcurrentHashMap<Socket, DataOutputStream> dos) {
        this.messages = messages;
        this.connectedClients = connectedClients;
        this.dos = dos;
    }

    @Override
    public void run() {
        try {
            while (true) {
            String message = messages.take();
            for (Socket connectedClient : connectedClients) {
                if (connectedClient.isConnected()) {
                    dos.get(connectedClient).writeUTF(message);
                }
            }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}