package server;

import server.enums.ResponseType;
import server.enums.UserStanding;

import java.io.IOException;
import java.net.BindException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

class Server {
    private final static int PORT = ServerProperties.getPort();
    private final static String SERVERNAME = ServerProperties.getName();

    private final ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
    private final Selector selector = Selector.open();
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

    private void run() throws IOException, SQLException {
        while (!Thread.currentThread().isInterrupted()) {
            this.selector.select();
            Set<SelectionKey> keys = this.selector.selectedKeys();
            Iterator<SelectionKey> iterator = keys.iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                if (key.isAcceptable()) {
                    accept();
                } else if (key.isReadable()) {
                    read(key);
                } else if (key.isWritable()) {
                    write(key);
                }
                iterator.remove();
            }
        }
    }

    private void accept() throws IOException {
        System.out.println("Accepting connection!");

        SocketChannel socketChannel = serverSocketChannel.accept();
        socketChannel.configureBlocking(false);

        channels.add(socketChannel);
        socketChannel.write(ByteBuffer.wrap(("Welcome to " + SERVERNAME).getBytes()));
        socketChannel.write(ByteBuffer.wrap((ResponseType.REQUEST_LOGIN_INPUT.getMessage()).getBytes()));

        clients.add(new Client(socketChannel));

        socketChannel.register(this.selector, SelectionKey.OP_READ);

    }

    private void read(SelectionKey key) throws IOException, SQLException {
        System.out.println("Receieving data from user!");

        SocketChannel socketChannel = (SocketChannel) key.channel();
        ByteBuffer buf = ByteBuffer.allocate(1000);

        try {
            int read = socketChannel.read(buf);

            buf.flip();

            byte[] temp = new byte[read];
            buf.get(temp);
            
            Client client = findClientViaChannel(socketChannel);
            String input = new String(temp, 2 , read - 2, "UTF-8");
            digestInput(client, input);

        } catch (ClosedChannelException e) {
            System.err.println("Error: Client socket closed!");
            e.printStackTrace();
            socketChannel.close();
            channels.remove(socketChannel);
        }

    }

    private void digestInput(Client client, String input) throws ClosedChannelException, SQLException {
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
            //not the optimal way
            client.setUserStanding(Database.getUserStanding(client.getUsername()));
            String msg = formattedDate.format(date) + "[" + client.getUserStanding() + "]" + client.getUsername() + ": " + input.replaceAll("\\s+", "");

            for (Client client1 : clients) {
                if (!client.isNotLoggedIn()) {
                    queueMessageToClient(client1, msg);
                }
            }
        }
    }

    private void write(SelectionKey key) throws IOException {
        System.out.println("Sending message to users!");
        SocketChannel socketChannel = (SocketChannel) key.channel();
        Client client = findClientViaChannel(socketChannel);
        String msg = client.getQueuedMessage();
        if (msg != null) {
            socketChannel.write(ByteBuffer.wrap(msg.getBytes()));
            if (!client.isQueueEmpty()) {
                socketChannel.register(this.selector, SelectionKey.OP_WRITE);
            } else {
                socketChannel.register(this.selector, SelectionKey.OP_READ);
            }
        } else {
            socketChannel.register(this.selector, SelectionKey.OP_READ);
        }
    }

    private void queueMessageToClient(Client client, String message) throws ClosedChannelException {
        client.addToQueue(message);
        client.getSocketChannel().register(this.selector, SelectionKey.OP_WRITE);
    }

    private Client findClientViaChannel(SocketChannel channel) {
        for (Client client : clients) {
            if (client.getSocketChannel().equals(channel)) {
                return client;
            }
        }
        throw new IllegalStateException("Client with this SocketChannel was not in 'Set<Client> clients' at the time of reading from SocketChannel.");
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
            throw new IllegalStateException("\"There should always be at least one user online (the user executing the command).\"");
        }
        return onlineClients;
    }

    private void disconnectClient(Client client) {

        if(client.isQueueEmpty()){
            try {
                client.getSocketChannel().close();
            } catch (IOException e) {
                System.err.println("Error: Client socket already closed!");
                e.printStackTrace();
            }
            channels.remove(client.getSocketChannel());
        }

    }

}