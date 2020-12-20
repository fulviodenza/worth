package worth.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

public class ClientConnection extends Thread{
    private static SocketChannel client;
    private static ByteBuffer outBuf;
    private static ByteBuffer inBuf;
    private static ClientConnection instance;
    private static SelectionKey key;
    private static Selector selector;

    public ClientConnection(InetSocketAddress addr) {

        try {
            selector = Selector.open();
            client = SocketChannel.open();
            client.connect(addr);

            if(client.isConnected()) {
                System.out.println("Client: Connected to WORTH server!");
            }

            client.configureBlocking(false);
            key = client.register(selector, SelectionKey.OP_READ);
            outBuf = ByteBuffer.allocate(256);

        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * This operation write in the buffer data the client needs
     * to send to server
     * @param data
     */
    public static synchronized void write(byte[] data) {
        try {
            key.interestOps(SelectionKey.OP_READ | SelectionKey.OP_WRITE);
            outBuf.put(data);
        } catch (CancelledKeyException e) {
            e.printStackTrace();
        }
        selector.wakeup();
    }

    /**
     * This operation sends to the server via the
     * SocketChannel the content in the buffer
     */
    private synchronized void send() {
        /*
        If I have elements in the buffer
         */
        try {
            if (outBuf.position() > 0) {
                outBuf.flip();
                client.write(outBuf);
                outBuf.compact();
            } else {
                key.interestOps(SelectionKey.OP_READ);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static synchronized String read(String data) {
        String message = null;
        if(inBuf.hasRemaining()) {
            message = String.valueOf(inBuf.get());
        }
        return message;
    }

    public static void write(String data) {
        write(new String(data + "\n").getBytes(StandardCharsets.UTF_8));
    }
}


