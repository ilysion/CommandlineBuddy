package server;

import server.runnable.LoginHandler;
import server.singletons.ServerProperties;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

//TODO: I PROPOSE THAT THE SERVER-SIDE (everything in the server package) SHOULD EVENTUALLY BE MOVED INTO A DIFFERENT PROJECT FROM THE CLIENT-SIDE.
class Server {
    public static void main(String[] args) throws IOException, InterruptedException {
        //TODO: DOES THIS ACTUALLY WORK FOR CONNECTING OVER INTERNET? IF NOT, THEN INTERNET MUST BE IMPLEMENTED!
        ServerSocket serverSocket = new ServerSocket(ServerProperties.getPortValue());
        while (true) {
            Socket socket = serverSocket.accept();
            /*this is for protection against attacks: prevents server overload (probably, right?) -danb*/
            Thread.sleep(100);
            new Thread(new LoginHandler(socket)).start();
        }
    }
}
