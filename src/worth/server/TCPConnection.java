package worth.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class TCPConnection {

    ThreadPoolExecutor pool = (ThreadPoolExecutor) Executors.newCachedThreadPool();
    private ServerSocket serverSocket;
    private ServerNotification serverNotification;

    public TCPConnection(ServerNotification serverNotification) {
        this.serverNotification = serverNotification;
    }

    public void start(int port) throws IOException {

        serverSocket = new ServerSocket(port);
        while(true) {
            pool.execute(new ConnectionHandler(serverSocket.accept(), serverNotification));
            //Thread t = new Thread(new ConnectionHandler(serverSocket.accept(), serverNotification));
            //t.start();
        }
    }
    public void stop() {
        try {
            serverSocket.close();
        } catch (IOException e) {
            System.out.println("Client Disconnected");
        }
    }


}
