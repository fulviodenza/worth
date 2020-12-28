package worth.server;

import worth.client.TCPClient;

import java.io.IOException;

public class Main {
    public static void main(String args[]) throws IOException {
        UserRegister ur = new UserRegister();
        TCPConnection connection = new TCPConnection();
        Runtime.getRuntime().exec("rmiregistry 2020&");
        System.setProperty("java.rmi.server.hostname","0.0.0.0");
        ur.RemoteHandler(5456);
        System.out.println("Server Started");
        connection.start(5457);
    }
}
