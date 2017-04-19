package server;

import org.mindrot.jbcrypt.BCrypt;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

public class User implements Runnable {
    private final Socket socket;
    private final BlockingQueue<String> messages;
    private DataOutputStream dos;
    private DataInputStream dis;
    private String username;

    User(BlockingQueue<String> messages, Socket socket, ConcurrentHashMap<Socket, DataOutputStream> dos, ConcurrentHashMap<Socket, DataInputStream> dis) throws IOException {
        this.messages = messages;
        this.socket = socket;
        this.dos = dos.get(this.socket);
        this.dis = dis.get(this.socket);
    }

    @Override
    public void run() {
        try {
            while (true) {
                dos.writeUTF("Username: ");
                String username = dis.readUTF();
                dos.writeUTF("Password: ");
                String password = dis.readUTF();
                String passwordFromDB = Database.getDB().getPassword(username);
                if (passwordFromDB != null) {
                    if (BCrypt.checkpw(password, passwordFromDB)) {
                        this.username = username;
                        dos.writeUTF("Login successful!");
                        break;
                    }
                    else {
                        dos.writeUTF("Invalid login.");
                    }
                }
                else {
                    dos.writeUTF("Invalid login.");
                }
            }
            while (true) {
                Date date = new Date();
                SimpleDateFormat formattedDate = new SimpleDateFormat("kk:mm");
                String message = "[" + formattedDate.format(date) + "] " + username + ": " + dis.readUTF();
                messages.put(message);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
