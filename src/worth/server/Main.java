package worth.server;

import worth.client.TCPClient;

import java.io.IOException;

public class Main {
    public static void main(String args[]) throws IOException {
        UserRegister ur = new UserRegister();
        Runtime.getRuntime().exec("rmiregistry 2020");
        System.setProperty("java.rmi.server.hostname","0.0.0.0");
        ur.RemoteHandler(5455);

        TCPConnection connection = new TCPConnection();
        connection.start(5456);
        if(Thread.interrupted()) connection.stop();
        System.out.println("Server Started");
    }
}
