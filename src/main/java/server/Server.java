package server;

import java.io.IOException;
import java.net.BindException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.text.SimpleDateFormat;
import java.util.*;

public class Server implements Runnable {
    private final int PORT = ServerProperties.getPortValue();

    private final ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
    private final Selector selector = Selector.open();
    private final Map<SocketChannel, byte[]> messages = new HashMap<>();
    private final Set<SocketChannel> channels = new HashSet<>();
    private final List<ConnectionBundle> bundles = new ArrayList<>();

    public void sendMessageToUser(String message, String username) {
        //TODO: A method like this must be created in order to merge with threadedversion.
        // How to implement this?
        // I suggest that this be implemented via a Map of SocketChannels and Strings (usernames).
        // This way the correct SocketChannel can be accessed with the username.
        // It must be noted that connected clients that have not yet logged in should not be in this list,
        // as they don't have an username.
    }

    private Server() throws IOException {
        try {
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.socket().bind(new InetSocketAddress(PORT));
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        } catch (BindException e) {
            System.out.println("Error: Address is already in use!");
            System.exit(1);
        }
        //TODO: The following catch block is unnecessary?
        catch (Exception e2) {
            throw new RuntimeException(e2);
        }
    }

    public static void main(String[] args) throws IOException {
        new Thread(new Server()).start();
    }

    private void stop() {
        try {

            selector.close();
            serverSocketChannel.socket().close();
            serverSocketChannel.close();

        }
        //TODO: The following rethrow should be replaced with a throw in method signature.
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                this.selector.select();
                //TODO: Raw use of parametrized class below. Why not "Set<SelectionKey>" and "Iterator<SelectionKey>"?
                Set keys = this.selector.selectedKeys();
                Iterator iterator = keys.iterator();
                while (iterator.hasNext()) {
                    SelectionKey key = (SelectionKey) iterator.next();
                    if (key.isAcceptable()) {
                        accept(key);
                    } else if (key.isReadable()) {
                        read(key);
                    } else if (key.isWritable()) {
                        write(key);
                    }
                    iterator.remove();
                }
            }
        }
        //TODO: The Exception below can be specified to IOException.
        catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            stop();
        }
    }

    private void accept(SelectionKey key) throws IOException {
        System.out.println("Accepting connection!");

        SocketChannel socketChannel = serverSocketChannel.accept();

        socketChannel.configureBlocking(false);

        channels.add(socketChannel);

        socketChannel.register(this.selector, SelectionKey.OP_READ);
    }

    private void read(SelectionKey key) throws IOException {
        System.out.println("Receieving data from user!");

        SocketChannel socketChannel = (SocketChannel) key.channel();
        ByteBuffer buf = ByteBuffer.allocate(1000);

        try {
            int read = socketChannel.read(buf);

            buf.flip();

            byte[] temp = new byte[1000];
            buf.get(temp, 0, read);
            String input = new String(temp);

            if (input.charAt(0) == '/') {
                //TODO commands
            } else {
                Date date = new Date();
                SimpleDateFormat formattedDate = new SimpleDateFormat("[kk:mm] ");
                String msg = formattedDate.format(date) + new String(temp).replaceAll("\\s+", "");;
                temp = msg.getBytes();

                for (SocketChannel channel : channels) {
                    this.messages.put(channel, temp);
                    channel.register(this.selector, SelectionKey.OP_WRITE);
                }
            }
        }
        //TODO: Can the Exception below be made more specific?
        catch (Exception e) {
            System.out.println("Client socket closed");
            socketChannel.close();
            channels.remove(socketChannel);
        }


    }

    private void write(SelectionKey key) throws IOException {
        //TODO: Doesn't this just send the message only to the channel registered with the key of this method's argument?
        System.out.println("Sending message to users!");
        SocketChannel socketChannel = (SocketChannel) key.channel();
        byte[] message = this.messages.get(socketChannel);
        socketChannel.write(ByteBuffer.wrap(message));
        socketChannel.register(this.selector, SelectionKey.OP_READ);
    }
}