package server;

import server.enums.ResponseType;
import server.enums.UserStanding;

import java.io.IOException;
import java.net.BindException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.text.SimpleDateFormat;
import java.util.*;

class Server {
    private final static int PORT = ServerProperties.getPort();
    private final static String SERVERNAME = ServerProperties.getName();

    private final ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
    private final Selector selector = Selector.open();

    private final Map<SocketChannel, byte[]> messages = new HashMap<>();
    private final Set<SocketChannel> channels = new HashSet<>();
    private final Set<Client> clients = new HashSet<>();

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
        socketChannel.write(ByteBuffer.wrap((ResponseType.REQUEST_LOGIN_INPUT.getMessage()).getBytes()));

        clients.add(new Client(socketChannel));

        socketChannel.register(this.selector, SelectionKey.OP_READ);

    }

    private void read(SelectionKey key) throws IOException {
        System.out.println("Receieving data from user!");

        SocketChannel socketChannel = (SocketChannel) key.channel();
        ByteBuffer buf = ByteBuffer.allocate(1000);

        try {
            int read = socketChannel.read(buf);

            buf.flip();

            byte[] temp = new byte[read];
            buf.get(temp);
            Client client = findClientViaChannel(socketChannel);
            String input = new String(temp, 1 , read - 1, "UTF-8");
            digestInput(client, input);

        } catch (ClosedChannelException e) {
            System.err.println("Error: Client socket closed!");
            e.printStackTrace();
            socketChannel.close();
            channels.remove(socketChannel);
        }

    }

    private void digestInput(Client client, String input) throws ClosedChannelException {
        InputHandler handler = new InputHandler(client, input);
        if (client.isNotLoggedIn()) {
            ResponseType response = handler.handleLoginAndGenerateResponse();
            queueMessageToClient(client, response.getMessage());
            if (response.equals(ResponseType.DISCONNECTED_FAIL_ATTEMPTS)) {
                disconnectClient(client);
            }
        }
        else if (handler.isCommand()) {
            ResponseType response = handler.handleCommandAndGenerateResponse();
            queueMessageToClient(client, response.getMessage());
            if (response.equals(ResponseType.DISCONNECTED_FAIL_ATTEMPTS) || response.equals(ResponseType.DISCONNECT_REQUEST)) {
                disconnectClient(client);
            }
            else if (response.equals(ResponseType.INCOMING_ACTIVE_USERS)) {
                String listedOnlineUsers = getFormattedOnlineUsers();
                queueMessageToClient(client, listedOnlineUsers);
            }
        }
        else if (!UserStanding.SILENCED.equals(Database.getUserStanding(client.getUsername()))){
            Date date = new Date();
            SimpleDateFormat formattedDate = new SimpleDateFormat("[kk:mm] ");
            String msg = formattedDate.format(date) + input.replaceAll("\\s+", "");

            byte[] temp = msg.getBytes();
            for (SocketChannel channel : channels) {
                this.messages.put(channel, temp);
                channel.register(this.selector, SelectionKey.OP_WRITE);
            }
        }
    }

    private void write(SelectionKey key) throws IOException {
        System.out.println("Sending message to users!");
        SocketChannel socketChannel = (SocketChannel) key.channel();
        byte[] message = this.messages.get(socketChannel);
        socketChannel.write(ByteBuffer.wrap(message));
        socketChannel.register(this.selector, SelectionKey.OP_READ);
    }

    private void queueMessageToClient(Client client, String message) {
        //TODO: To be implemented.
    }

    private Client findClientViaChannel(SocketChannel channel) {
        for (Client client : clients) {
            if (client.getSocketChannel().equals(channel)) {
                return client;
            }
        }
        throw new RuntimeException("Client with this SocketChannel was not in 'Set<Client> clients' at the time of reading from SocketChannel.");
    }

    private String getFormattedOnlineUsers() {
        Set<Client> onlineClients = getOnlineClients();
        StringBuilder listedUsers = new StringBuilder();
        for (Client client : onlineClients) {
            listedUsers.append(client.getUsername()).append("\n");
        }
        return listedUsers.toString();
    }


    private Set<Client> getOnlineClients() {
        HashSet<Client> onlineClients = new HashSet<>();
        for (Client client : clients) {
            if (!client.isNotLoggedIn()) {
                onlineClients.add(client);
            }
        }
        if (onlineClients.isEmpty()) {
            throw new RuntimeException("\"There should always be at least one user online (the user executing the command).\"");
        }
        return onlineClients;
    }

    private void disconnectClient(Client client) {
        //TODO: To be implemented. The client should be disconnected AFTER he has received the message queued to him.
    }

}