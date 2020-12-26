package worth.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPConnection {

    private ServerSocket serverSocket;
    private Socket clientSocket;

    public void start(int port) throws IOException {

        serverSocket = new ServerSocket(port);
        System.out.println("A new client is connected : " + clientSocket);

        // obtaining input and out streams
        DataInputStream dis = new DataInputStream(clientSocket.getInputStream());
        DataOutputStream dos = new DataOutputStream(clientSocket.getOutputStream());

        System.out.println("Assigning new thread for this client");

        // create a new thread object
        Thread t = new ConnectionHandler(clientSocket, dis, dos);
        t.start();
    }
}
