package server;

import java.io.DataOutputStream;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created on 2017-04-01.
 */

public class SendMessage implements Runnable {
    private final BlockingQueue<String> messages;
    private final List<Socket> connectedClients;
    private final ConcurrentHashMap<Socket, DataOutputStream> dos;

    public SendMessage(BlockingQueue<String> messages, List<Socket> connectedClients, ConcurrentHashMap<Socket, DataOutputStream> dos) {
        this.messages = messages;
        this.connectedClients = connectedClients;
        this.dos = dos;
    }

    @Override
    public void run() {
        while (true) {
            try {
                String message = messages.take();
                for (Socket connectedClient : connectedClients) {
                    if (connectedClient.isConnected()) {
                        dos.get(connectedClient).writeUTF(message);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}