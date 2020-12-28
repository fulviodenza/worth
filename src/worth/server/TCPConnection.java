package worth.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPConnection {

    private ServerSocket serverSocket;
    private Socket clientSocket;

    public void start(int port) throws IOException {

        serverSocket = new ServerSocket(port);
        while(true) {
            Thread t = new Thread(new ConnectionHandler(serverSocket.accept()));
            t.start();
        }
    }
    public void stop() throws IOException {
        serverSocket.close();
    }


}
