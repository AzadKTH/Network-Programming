package server.net;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.Queue;

/**
 * Receives messages from clients. All communication to/from any
 * client passes this server.
 */


public class ClientConnection {
    private static final int LINGER_TIME = 5000;
    private final Queue<MsgAndKeyObject> messagesToSend = new ArrayDeque<>();
    private ByteBuffer messageToClient = ByteBuffer.allocateDirect(8152);
    private int portNo = 8080;
    private Selector selector;

    /**
     * Sends the specified message to all connected clients
     *
     * @param msg The message to broadcast.
     */
    void broadcast(String msg, SelectionKey key) {
        ByteBuffer completeMsg = createBroadcastMessage(msg);
        MsgAndKeyObject putInQueue = new MsgAndKeyObject(completeMsg, key);
        synchronized (messagesToSend) {
            messagesToSend.add(putInQueue);
        }
        selector.wakeup();
    }

    private ByteBuffer createBroadcastMessage(String msg) {
        return ByteBuffer.wrap(msg.getBytes());
    }

    private void serve() {
        try {
            initSelector();
            initListeningSocketChannel();
            while (true) {
                synchronized (messagesToSend) {
                    MsgAndKeyObject queueObject;
                    while ((queueObject = messagesToSend.poll()) != null) {
                        Client client = (Client) queueObject.getKey().attachment();
                        client.queueMsgToSend(queueObject.getMessage());
                        queueObject.getKey().interestOps(SelectionKey.OP_WRITE);
                    }
                }
                selector.select();
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    iterator.remove();
                    if (!key.isValid()) {
                        continue;
                    }
                    if (key.isAcceptable()) {
                        startHandler(key);
                    } else if (key.isReadable()) {
                        recvFromClient(key);
                    } else if (key.isWritable()) {
                        sendToClient(key); //(6)
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Server failure.");
        }
    }

    private void startHandler(SelectionKey key) throws IOException {
        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
        SocketChannel clientChannel = serverSocketChannel.accept();
        clientChannel.configureBlocking(false);
        ClientHandler handler = new ClientHandler(this, clientChannel);
        Client client = new Client(handler);
        SelectionKey correctKey = clientChannel.register(selector, SelectionKey.OP_WRITE, client);
        client.handler.setKey(correctKey);
        System.out.println(key.attachment());
        clientChannel.setOption(StandardSocketOptions.SO_LINGER, LINGER_TIME);
    }

    private void recvFromClient(SelectionKey key) throws IOException {
        Client client = (Client) key.attachment();
        try {
            client.handler.recvMsg();
        } catch (IOException clientHasClosedConnection) {
            removeClient(key);
        }
    }

    private void sendToClient(SelectionKey key) throws IOException {
        Client client = (Client) key.attachment();
        try {
            client.sendAll();
            key.interestOps(SelectionKey.OP_READ);
        } catch (IOException clientHasClosedConnection) {
            removeClient(key);
        }
    }

    private void removeClient(SelectionKey clientKey) throws IOException {
        Client client = (Client) clientKey.attachment();
        client.handler.disconnectClient();
        clientKey.cancel();
    }

    private void initSelector() throws IOException {
        selector = Selector.open();
    }

    private void initListeningSocketChannel() throws IOException {
        ServerSocketChannel listeningSocketChannel = ServerSocketChannel.open();
        listeningSocketChannel.configureBlocking(false);
        listeningSocketChannel.bind(new InetSocketAddress(portNo));
        listeningSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
    }

    private class Client {
        private final ClientHandler handler;
        private final Queue<ByteBuffer> messagesToSend = new ArrayDeque<>();

        private Client(ClientHandler handler) {
            this.handler = handler;
        }

        private void queueMsgToSend(ByteBuffer msg) {
            synchronized (messagesToSend) {
                messagesToSend.add(msg.duplicate());
            }
        }

        private void sendAll() throws IOException {
            ByteBuffer msg = null;
            synchronized (messagesToSend) {
                while ((msg = messagesToSend.peek()) != null) {
                    handler.sendMsg(msg);
                    messagesToSend.remove();
                }
            }
        }
    }

    /**
     * @param args Takes one command line argument, the number of the port on which the server will
     *             listen, the default is <code>8080</code>.
     */
    public static void main(String[] args) {
        ClientConnection server = new ClientConnection();
        server.parseArguments(args);
        server.serve();
    }

    private void parseArguments(String[] arguments) {
        if (arguments.length > 0) {
            try {
                portNo = Integer.parseInt(arguments[1]);
            } catch (NumberFormatException e) {
                System.err.println("Invalid port number, using default.");
            }
        }
    }
}
