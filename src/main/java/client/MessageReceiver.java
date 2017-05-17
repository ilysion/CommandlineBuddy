package client;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

/**
 * Created by CmdBuddyTeam on 29.03.2017.
 */

public class MessageReceiver implements Runnable{
    private Socket socket;

    public MessageReceiver(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run(){
        try(DataInputStream instream = new DataInputStream(socket.getInputStream())) {
            while(!Thread.currentThread().isInterrupted()) {
                try {
                    int arv = instream.read();
                    byte[] message = new byte[arv];
                    instream.read(message);
                    System.out.println(new String(message, "UTF-8"));
                } catch (SocketException e1) {
                    System.out.println("Socket was closed!");
                    break;
                } catch (IOException e) {
                    System.out.println("error: " + e);
                    break;
                }
            }
        }
        catch(IOException e){
            System.out.println("error: " + e);
        }
    }
}
