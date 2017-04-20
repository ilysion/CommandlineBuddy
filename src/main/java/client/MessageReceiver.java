package client;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Created by CmdBuddyTeam on 29.03.2017.
 */

public class MessageReceiver implements Runnable{
    private Socket sock;

    public MessageReceiver(Socket sock) {
        this.sock = sock;
    }

    @Override
    public void run(){
        try(DataInputStream instream = new DataInputStream(sock.getInputStream())) {
            while(true){
                try{
                    byte[] message = new byte[1000];
                    instream.read(message);
                    System.out.println(new String(message));
                }
                catch(IOException e){
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
