package main.java.client;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

/**
 * Created by ilysion on 29.03.2017.
 */
public class KeyboardScanner implements Runnable {
    private Socket sock;

    public KeyboardScanner(Socket sock) {
        this.sock = sock;
    }

    @Override
    public void run(){
        Scanner keyboard = new Scanner(System.in);

        try(DataOutputStream outstream = new DataOutputStream(sock.getOutputStream())) {
            while(true){
                String input = keyboard.nextLine();
                try{
                    outstream.writeUTF(input);
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
