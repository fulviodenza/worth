package worth.client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class UDPConnection {
    String ip;
    public UDPConnection(String ip) {
        this.ip = ip;
    }

    private void receive() {
        byte[] buffer = new byte[1024];
        try {
            InetAddress group = InetAddress.getByName(ip);
            MulticastSocket ms = new MulticastSocket(4000);
            ms.joinGroup(group);
            while(!Thread.currentThread().isInterrupted()) {
                DatagramPacket dp = new DatagramPacket(buffer, buffer.length);
                ms.receive(dp);
                String message = new String()
            }
        }catch (IOException e) {
            e.printStackTrace();
        }
    }
}
