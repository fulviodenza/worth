package worth.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class ServerConnection {

    private static final String POISON_PILL = "POISON_PILL";
    static Selector selector;
    static SelectionKey acceptKey;
    static ServerSocketChannel serverSocketChannel;
    ByteBuffer buffer = ByteBuffer.allocate(4096);

    static SocketChannel client;

    static {
        try {
            selector = Selector.open();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start(int port) {
        try {
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.bind(new InetSocketAddress(port));
            acceptKey = serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);


        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        while(!Thread.interrupted()) {
            System.out.println("Server: Waiting for requests");
            try {
                selector.select();
                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> iter = selectedKeys.iterator();

                while(iter.hasNext()) {
                    SelectionKey key = iter.next();

                    if(key.isAcceptable()) {
                        try {
                            register(selector, serverSocketChannel);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if(key.isReadable()) {
                        try {
                            answerWithEcho(buffer, key);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    iter.remove();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void register(Selector selector, ServerSocketChannel serverSocketChannel) throws IOException{
        SocketChannel client = serverSocketChannel.accept();
        client.configureBlocking(false);
        client.register(selector, SelectionKey.OP_READ);
    }

    private static void answerWithEcho(ByteBuffer buffer, SelectionKey key) throws IOException{
        SocketChannel client = (SocketChannel) key.channel();
        client.read(buffer);
        if(new String(buffer.array()).trim().equals(POISON_PILL)) {
            client.close();
            System.out.println("Server: Not accepting client messages anymore");
        } else {
            buffer.flip();
            client.write(buffer);
            buffer.clear();
        }
    }
}
