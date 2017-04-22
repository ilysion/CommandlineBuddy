package client;

import java.io.IOException;
import java.util.List;

class MessageReceiver implements Runnable {
    private final SingleConnectionBundle bundle;

    MessageReceiver(SingleConnectionBundle bundle) {
        this.bundle = bundle;
    }

    @Override
    public void run() {
        try {
            while (bundle.getSocket().isConnected()) {
                String serverResponse = bundle.getDis().readUTF();
                new ResponseDigester().printResponse(serverResponse);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            bundle.closeQuietly();
        }
    }




}
