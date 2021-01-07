package worth.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPConnection {

    private ServerSocket serverSocket;
    private ServerNotification serverNotification;

    public TCPConnection(ServerNotification serverNotification) {
        this.serverNotification = serverNotification;
    }

    public void start(int port) throws IOException {

        serverSocket = new ServerSocket(port);
        while(true) {
            Thread t = new Thread(new ConnectionHandler(serverSocket.accept(), serverNotification));
            t.start();
        }
    }
    public void stop() throws IOException {
        serverSocket.close();
    }


}
