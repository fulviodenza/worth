package worth.client;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class UDPServer {

    private static final int port = 20005;

    public static void send(String msg, String ip, String username) {

        try {
            msg = username + ": " + msg;
            DatagramSocket ds = new DatagramSocket();
            InetAddress group = InetAddress.getByName(ip);
            byte[] buffer = msg.getBytes(StandardCharsets.UTF_8);
            DatagramPacket dp = new DatagramPacket(buffer, buffer.length, group, port);
            ds.send(dp);
            ds.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}