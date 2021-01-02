package worth.server;

import worth.client.TCPClient;

import java.io.IOException;

public class Main {
    public static void main(String args[]) throws IOException {
        System.out.println("Main before rmi.start");
        UserRegister ur = new UserRegister();
        Runtime.getRuntime().exec("rmiregistry 2020");
        System.setProperty("java.rmi.server.hostname","0.0.0.0");
        ur.RemoteHandler(5455);
        System.out.println("Main after rmi.start");

        TCPConnection connection = new TCPConnection();
        System.out.println("Main before connection.start");
        connection.start(5456);
        System.out.println("Main after connection.start");
        if(Thread.interrupted()) connection.stop();
        System.out.println("Server Started");
    }
}
