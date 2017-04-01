package server;

import java.io.DataOutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.BlockingQueue;

/**
 * Created on 2017-04-01.
 */
public class SendMessage implements Runnable {
    private final BlockingQueue<String> messages;
    private final List<Socket> connectedClients;
    private final HashMap<Socket,DataOutputStream> dos;

    public SendMessage(BlockingQueue<String> messages, List<Socket> connectedClients, HashMap<Socket, DataOutputStream> dos) {
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
                    dos.get(connectedClient).writeUTF(message);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}