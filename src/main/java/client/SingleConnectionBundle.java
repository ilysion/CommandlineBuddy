package client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;


/*THIS IS NOT REDUNDANT! I PROPOSE THAT THE WHOLE CLIENT PACKAGE BE MOVED INTO SEPERATE PROJECT EVENTUALLY.*/
class SingleConnectionBundle {
    private final Socket socket;
    private final DataInputStream dis;
    private final DataOutputStream dos;

    public SingleConnectionBundle(Socket socket, DataInputStream dis, DataOutputStream dos) {
        this.socket = socket;
        this.dis = dis;
        this.dos = dos;
    }

    public Socket getSocket() {
        return socket;
    }

    public DataInputStream getDis() {
        return dis;
    }

    public DataOutputStream getDos() {
        return dos;
    }

    /*THIS IS NOT REDUNDANT! I PROPOSE THAT THE WHOLE CLIENT PACKAGE BE MOVED INTO SEPERATE PROJECT EVENTUALLY.*/
    public void closeQuietly() {
        try {
            socket.close();
            dis.close();
            dos.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
