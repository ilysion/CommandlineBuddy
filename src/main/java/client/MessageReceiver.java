package client;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

public class MessageReceiver implements Runnable {
    private Socket socket;

    public MessageReceiver(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try (DataInputStream instream = new DataInputStream(socket.getInputStream())) {
            while (!Thread.currentThread().isInterrupted()) {
                byte[] message = new byte[1000];
                instream.read(message);
                System.out.println(new String(message, "UTF-8"));
            }
        } catch (SocketException e1) {
            System.out.println("Socket was closed!");
        } catch (IOException e) {
            System.out.println("error: " + e);
        }
    }
}
