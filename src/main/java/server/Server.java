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
    private static final int PORT = 42031;

    public static void main(String[] args) throws IOException {
        BlockingQueue<String> messages = new ArrayBlockingQueue<String>(50);
        List<Socket> connectedClients = new ArrayList<>();
        HashMap<Socket, DataOutputStream> dos = new HashMap<>();
        HashMap<Socket, DataInputStream> dis = new HashMap<>();
        Database database = new Database();

        new Thread(new SendMessage(messages,connectedClients, dos)).start();

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                try {
                    Socket socket = serverSocket.accept();
                    dos.put(socket,new DataOutputStream(socket.getOutputStream()));
                    dis.put(socket,new DataInputStream(socket.getInputStream()));

                    String username;

                    while(true){
                        dos.get(socket).writeUTF("Enter username: ");
                        username = dis.get(socket).readUTF();

                        if (database.userExists(username)) {
                            break;
                        }

                        dos.get(socket).writeUTF("Username doesn't exist! ");
                    }

                    while(true){
                        dos.get(socket).writeUTF("Enter password: ");
                        String password = dis.get(socket).readUTF();

                        if(database.checkpw(username,password)){
                            dos.get(socket).writeUTF("Login successful");
                            break;
                        }

                        dos.get(socket).writeUTF("Wrong password!");

                    }
                    new Thread(new ReceiveMessage(messages, socket, username, dis)).start();
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
        database.closeDB();
    }
}
