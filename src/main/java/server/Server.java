package server;

import server.runnable.LoginHandler;
import server.singletons.ServerProperties;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static void main(String[] args) throws IOException, InterruptedException {
        ServerSocket serverSocket = new ServerSocket(ServerProperties.getPortValue());
        while (true) {
            Socket socket = serverSocket.accept();
            /*this is for protection against attacks: prevents server overload (probably, right?) -danb*/
            Thread.sleep(100);
            new Thread(new LoginHandler(socket)).start();
        }
    }
}
