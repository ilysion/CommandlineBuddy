package client;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;



public class MessageReceiver implements Runnable{
    private Socket sock;

    MessageReceiver(Socket sock) {
        this.sock = sock;
    }

    @Override
    public void run(){
        try(DataInputStream instream = new DataInputStream(sock.getInputStream())) {
            while (true) {
            String msg = instream.readUTF();
            System.out.println(msg);
            }
        }
        catch(IOException e){
            throw new RuntimeException(e);
        }
    }
}
