package worth.client;

import worth.RegisterUserInterface;
import worth.exceptions.EmptyPassword;
import worth.exceptions.UserAlreadyPresent;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class RemoteHandlerClient {

    public RemoteHandlerClient() throws RemoteException {}

    public void registerStub(String username, String password) {
        try {
            Registry registry = LocateRegistry.getRegistry(5455);
            RegisterUserInterface stub = (RegisterUserInterface) registry.lookup("RegisterUserInterface");
            if(stub.register(username, password) == 0) {
                System.out.println("You have been successfully registered\n");
            } else {
                System.out.println("You're already present in the Database!\n");
            }
        } catch (Exception | UserAlreadyPresent | EmptyPassword e) {
            e.printStackTrace();
        }
    }
}
