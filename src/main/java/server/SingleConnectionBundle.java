package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/*This class is used for passing the Socket and DataStreams down the call chain (as seen in the server side flow chart)*/
public class SingleConnectionBundle {
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

    //TODO: THERE SHOULD BE A SOME SORT OF MESSAGE TO THE USER WHEN THE USER GETS DISCONNECTED. ALSO THIS
    //TODO: I THINK THIS METHOD SHOULD INCORPORATED INTO 'InMemoryDatabase' METHOD 'safeRemove' (not yet used. definitely must be used though.)
    /*THIS IS NOT REDUNDANT! I PROPOSE THAT THE WHOLE SERVER PACKAGE BE MOVED INTO SEPERATE PROJECT EVENTUALLY.*/
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
