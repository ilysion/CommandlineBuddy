package client;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

/**
 * Created by CmdBuddyTeam on 29.03.2017.
 */

public class KeyboardScanner implements Runnable {
    private Socket sock;

    public KeyboardScanner(Socket sock) {
        this.sock = sock;
    }

    @Override
    public void run(){

        try(Scanner keyboard = new Scanner(System.in);DataOutputStream outstream = new DataOutputStream(sock.getOutputStream())) {
            while(true){
                try{
                    if (keyboard.hasNextLine()) {
                        outstream.writeUTF(keyboard.nextLine());
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
