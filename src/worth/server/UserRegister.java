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

        if(!Database.getDatabase().containsUser(nickname)) {
            Member m = new Member(nickname, password);
            Database.getDatabase().updateMember(m);
            System.out.printf("SERVER: %s registered\n", nickname);
            return 0;
        } else {
            System.out.printf("SERVER: %s already registered!\n", nickname);
            return 1;
        }
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
