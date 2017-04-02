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
                    if(instream.available()>0){
                        String msg = instream.readUTF();
                        System.out.println(msg);
                    }

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
