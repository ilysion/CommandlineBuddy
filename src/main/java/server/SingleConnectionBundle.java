package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/*This class is used for passing the Socket and DataStreams down the call chain (as seen in the server side flow chart)*/
public class SingleConnectionBundle {
    private Socket socket;
    private DataInputStream dis;
    private DataOutputStream dos;

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
