package main.java.server;

/**
 * Created by CmdBuddyTeam on 29.03.2017.
 */

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server implements Runnable {
    Socket sock;

    public Server(Socket sock) {
        this.sock = sock;
    }

    public static void main(String[] args) throws IOException {

        try(ServerSocket ss = new ServerSocket(1337)) {

            while (true) {
                Socket sock = ss.accept(); // waits for connection
                System.out.println("*Connection established");
                new Thread(new Server(sock)).start();
                System.out.println("New thread started");
            }
        }
    }

    public void run() {

        try (DataOutputStream outstream = new DataOutputStream(sock.getOutputStream());
             DataInputStream instream = new DataInputStream(sock.getInputStream())) {

            while(true){
                if(instream.available()>0){
                    String string = instream.readUTF();
                    System.out.println("Recieved message: " + string);
                    outstream.writeUTF(string);
                }
            }

        } catch (IOException error) {
            System.out.println("!Error occurred: " + error);

        } finally {
            System.out.println("in/out stream closed");

            try{
                sock.close();
                System.out.println("Socket closed \n");
            }
            catch(IOException e){
                System.out.println("Cant close socket, stopping server");
                throw new RuntimeException(e);
            }
        }
    }
}
