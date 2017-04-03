package server;

/**
 * Created by CmdBuddyTeam on 29.03.2017.
 */

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
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
        EncryptPw encrypter = new EncryptPw();


        new Thread(new SendMessage(messages,connectedClients, dos)).start();

        try (ServerSocket serverSocket = new ServerSocket(42031)) {
            while (true) {
                try {
                    Socket socket = serverSocket.accept();
                    dos.put(socket,new DataOutputStream(socket.getOutputStream()));
                    dis.put(socket,new DataInputStream(socket.getInputStream()));

                    //Login
                    String password;
                    String username;

                    while(true){
                        dos.get(socket).writeUTF("Enter username: ");
                        username = dis.get(socket).readUTF();
                        DBgetpw getPw = new DBgetpw();
                        password = getPw.getUserPw(username);
                        if(password != null){
                            break;
                        }
                        dos.get(socket).writeUTF("Username doesn't exist! ");
                    }

                    while(true){
                        dos.get(socket).writeUTF("Enter password: ");
                        String enteredPassword = dis.get(socket).readUTF();
                        enteredPassword = encrypter.encrypt(enteredPassword);

                        if(password.equals(enteredPassword)){
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
    }
}
