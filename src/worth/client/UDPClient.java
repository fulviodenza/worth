package worth.client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class UDPClient implements Runnable{
    String ip;
    public UDPClient(String ip) {
        this.ip = ip;
    }

    private void receive() {
        byte[] buffer = new byte[1024];
        try {
            InetAddress group = InetAddress.getByName(ip);
            MulticastSocket ms = new MulticastSocket(20005);
            ms.joinGroup(group);
            while(!Thread.currentThread().isInterrupted() && TCPClient.alreadyLogged) {
                DatagramPacket dp = new DatagramPacket(buffer, buffer.length);
                ms.receive(dp);
                String message = new String(dp.getData(), dp.getOffset(), dp.getLength());
                System.out.print(">>" + message + "\n");
            }
            ms.leaveGroup(group);
            ms.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
receive();
    }
}
