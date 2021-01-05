package worth.client;

import worth.server.ServerNotification;
import worth.server.ServerNotificationInterface;

import java.io.File;
import java.io.FileNotFoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;

public abstract class CLICommand {

    final Registry registryCB = LocateRegistry.getRegistry(7001);
    final String name = "notification";
    final ServerNotificationInterface server = (ServerNotificationInterface) registryCB.lookup(name);
    final ClientNotification callbackObj = new ClientNotification();
    final ClientNotificationInterface stub = (ClientNotificationInterface) UnicastRemoteObject.exportObject(callbackObj, 0);

    TCPClient client = new TCPClient();

    protected CLICommand() throws RemoteException, NotBoundException {
    }

    public abstract String manage(Scanner scanner) throws RemoteException;
}

class LoginHandler extends CLICommand {

    protected LoginHandler() throws RemoteException, NotBoundException {
        super();
    }

    public String manage(Scanner scanner) throws RemoteException {
        String username = scanner.next();
        String password = scanner.next();
        server.register(stub, username);
        return "login@"+username+":"+password;
    }
}

class LogoutHandler extends CLICommand {

    protected LogoutHandler() throws RemoteException, NotBoundException {
        super();
    }

    public String manage(Scanner scanner) throws RemoteException {
        String username = scanner.next();

        server.unregister(stub, username);
        return "logout@"+username;
    }
}

class CreateProject extends CLICommand {

    protected CreateProject() throws RemoteException, NotBoundException {
        super();
    }

    public String manage(Scanner scanner) {
        String usernameCreator = scanner.next();
        String projectName = scanner.next();
        return "create_project@"+usernameCreator+":"+projectName;
    }
}

class ListUsers extends CLICommand {

    protected ListUsers() throws RemoteException, NotBoundException {
        super();
    }

    public String manage(Scanner scanner) {
        File file = new File("users.txt");
        Scanner fileScanner = null;
        try {
            fileScanner = new Scanner(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        String output = "";
        while(fileScanner.hasNextLine()) {
            String data = fileScanner.nextLine();
            output += data + "\n";
        }
        fileScanner.close();
        return output;
    }
}

class ListOnlineUsers extends CLICommand {

    protected ListOnlineUsers() throws RemoteException, NotBoundException {
        super();
    }

    public String manage(Scanner scanner) {
        File file = new File("./users.txt");
        Scanner fileScanner = null;
        try {
            fileScanner = new Scanner(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        String output = "";
        while(fileScanner.hasNextLine()) {
            String data = fileScanner.nextLine();
            if(data.contains("online")) {
                output += data + "\n";
            }
        }
        fileScanner.close();
        return output;
    }
}