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
    private final static int PORT = 1337;
    private final ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
    private Selector selector = Selector.open();
    private final Map<SocketChannel, byte[]> messages = new HashMap<>();
    private final Set<SocketChannel> channels = new HashSet<>();

    private Server() throws IOException {
        try {
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.socket().bind(new InetSocketAddress(PORT));

            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        } catch (BindException e) {
            System.out.println("Error: Address is already in use!");
            System.exit(1);
        } catch (Exception e2) {
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

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                this.selector.select();
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
        } catch (Exception e) {
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

        int read = socketChannel.read(buf);

        buf.flip();

        byte[] temp = new byte[1000];
        buf.get(temp, 0, read);

        if (new String(temp).charAt(0) == '/') {
            //TODO commands
        } else {
            this.messages.put(socketChannel, temp);
            socketChannel.register(this.selector, SelectionKey.OP_WRITE);
        }

    }

    private void write(SelectionKey key) throws IOException {
        System.out.println("Sending message to users!");

        SocketChannel socketChannel = (SocketChannel) key.channel();

        byte[] message = this.messages.get(socketChannel);

        Date date = new Date();
        SimpleDateFormat formattedDate = new SimpleDateFormat("kk;mm");
        String temp = formattedDate.format(date) + " | " + new String(message);

        message = temp.getBytes();

        for (SocketChannel channel : channels) {
            channel.write(ByteBuffer.wrap(message));
        }

        socketChannel.register(this.selector, SelectionKey.OP_READ);
    }

}