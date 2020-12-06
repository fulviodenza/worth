package worth.server;

import worth.RegisterUserInterface;
import worth.exceptions.EmptyPassword;
import worth.exceptions.UserAlreadyPresent;

import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.RemoteServer;
import java.rmi.server.UnicastRemoteObject;

public class UserRegister extends RemoteServer implements RegisterUserInterface {
    @Override
    public int register(String nickname, String password) throws RemoteException, UserAlreadyPresent, EmptyPassword {
        Member m = new Member(nickname, password);
        return 0;
    }

    public void RemoteHandler(int port) {
        try {
            UserRegister ur = new UserRegister();
            UnicastRemoteObject.exportObject(ur, port);
            Registry registry = LocateRegistry.createRegistry(5455);
            registry.bind("RegisterUserInterface", ur);
        } catch(RemoteException | AlreadyBoundException e) {
            System.err.println("Server Exception: " + e.toString());
            e.printStackTrace();
        }
    }
}
