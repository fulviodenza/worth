package worth.server;

import worth.MemberStatus;

import java.io.IOException;
import java.rmi.AlreadyBoundException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class Main {
    public static void main(String[] args) throws IOException, AlreadyBoundException {
        UserRegister ur = new UserRegister();
        Runtime.getRuntime().exec("rmiregistry 2020");
        System.setProperty("java.rmi.server.hostname","0.0.0.0");
        ur.RemoteHandler(5455);

        ServerNotification serverCB = new ServerNotification();
        ServerNotificationInterface stubCB = (ServerNotificationInterface) UnicastRemoteObject.exportObject(serverCB, 0);
        String name = "notification";
        LocateRegistry.createRegistry(7001);
        Registry registryCB = LocateRegistry.getRegistry(7001);
        registryCB.bind(name, stubCB);
        System.out.println("Callback bound");

        TCPConnection connection = new TCPConnection(serverCB);
        connection.start(5456);

        if(Thread.interrupted()) {
            connection.stop();
        }
    }
}
