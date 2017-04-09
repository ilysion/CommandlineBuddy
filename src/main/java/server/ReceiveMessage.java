package server;

import java.io.DataInputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.BlockingQueue;

/**
 * Created on 2017-04-01.
 */
public class ReceiveMessage implements Runnable {
    private final Socket socket;
    private final String username;
    private final BlockingQueue<String> messages;
    private final HashMap<Socket,DataInputStream> dis;

    public ReceiveMessage(BlockingQueue<String> messages, Socket socket, String username, HashMap<Socket, DataInputStream> dis) {
        this.messages = messages;
        this.socket = socket;
        this.username = username;
        this.dis = dis;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Date date = new Date();
                SimpleDateFormat formattedDate = new SimpleDateFormat("kk:mm");
                String message = "[" + formattedDate.format(date) + "] " +username + ": " + dis.get(socket).readUTF();
                messages.put(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
