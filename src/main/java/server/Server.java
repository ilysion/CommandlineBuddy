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

public class Server {
    private final static int PORT = ServerProperties.getPort();
    private final static String SERVERNAME = ServerProperties.getName();

    private final ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
    private final Selector selector = Selector.open();
    private final Map<SocketChannel, byte[]> messages = new HashMap<>();
    private final Set<SocketChannel> channels = new HashSet<>();

    private Server() throws IOException {}

    public static void main(String[] args) throws Exception {
        Server server = new Server();
        server.init();
        try {
            server.run();
        } finally {
            server.stop();
        }
    }

    private void init() throws IOException {
        try {

            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.socket().bind(new InetSocketAddress(PORT));

            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        } catch (BindException e) {
            System.err.println("Error: Address is already in use!");
            System.exit(1);
        }
    }

    private void stop() throws IOException {
        selector.close();
        serverSocketChannel.close();
    }

    private void run() throws Exception {
        while (!Thread.currentThread().isInterrupted()) {
            this.selector.select();
            Set<SelectionKey> keys = this.selector.selectedKeys();
            Iterator<SelectionKey> iterator = keys.iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
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

    private void accept(SelectionKey key) throws IOException {
        System.out.println("Accepting connection!");

        SocketChannel socketChannel = serverSocketChannel.accept();
        socketChannel.configureBlocking(false);

        channels.add(socketChannel);

        socketChannel.write(ByteBuffer.wrap(("Welcome to " + SERVERNAME).getBytes()));

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

            if (new String(temp).charAt(0) == '/') {
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

        } catch (Exception e) {
            System.err.println("Error: Client socket closed!");
            e.printStackTrace();
            socketChannel.close();
            channels.remove(socketChannel);
        }

    }

    private void write(SelectionKey key) throws IOException {
        System.out.println("Sending message to users!");
        SocketChannel socketChannel = (SocketChannel) key.channel();
        byte[] message = this.messages.get(socketChannel);
        socketChannel.write(ByteBuffer.wrap(message));
        socketChannel.register(this.selector, SelectionKey.OP_READ);
    }
}