package worth.client;

import worth.server.ServerNotification;
import worth.server.ServerNotificationInterface;

import java.io.File;
import java.io.FileNotFoundException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
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
        if(username.contains(":") || username.contains("@") || password.contains("@") || password.contains(":")) {
            return "fail";
        } else {
            server.register(stub, username);
            return "login@" + username + ":" + password;
        }
    }
}

class LogoutHandler extends CLICommand {

    protected LogoutHandler() throws RemoteException, NotBoundException {
        super();
    }

    public String manage(Scanner scanner) throws RemoteException {
        String username = scanner.next();

        server.unregister(stub, username);
        if(username.contains(":") || username.contains("@")) {
            return "fail";
        } else {
            return "logout@" + username;
        }
    }
}

class CreateProject extends CLICommand {

    protected CreateProject() throws RemoteException, NotBoundException {
        super();
    }

    public String manage(Scanner scanner) {
        String projectName = scanner.next();
        if(projectName.contains(":") || projectName.contains("@")) {
            return "fail";
        } else {
            System.out.println("create_project@" + projectName);
            return "create_project@" + projectName;
        }
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
            if(data.contains("ONLINE")) {
                output += data + "\n";
            }
        }
        fileScanner.close();
        return output;
    }
}

class CreateCard extends CLICommand {

    protected CreateCard() throws RemoteException, NotBoundException {
    }

    public String manage(Scanner scanner) {
        String projectName = scanner.next();
        String cardName = scanner.next();
        String cardDescription = scanner.nextLine();
        if(projectName.contains(":") || projectName.contains("@") || cardName.contains(":") || cardName.contains("@") || cardDescription.contains(":") || cardDescription.contains("@")) {
            return "fail";
        }
        System.out.println("Sending command add_card@"+projectName+":"+cardName+":"+cardDescription);
        return "add_card@"+projectName+":"+cardName+":"+cardDescription;
    }
}

class AddUser extends CLICommand {

    protected AddUser() throws RemoteException, NotBoundException {
    }

    public String manage(Scanner scanner) {
        String projectName = scanner.next();
        String usernameToAdd = scanner.next();
        if(projectName.contains(":") || projectName.contains("@") || usernameToAdd.contains(":") || usernameToAdd.contains("@")) {
            return "fail";
        } else {
            System.out.println("Sending command add_member@" + projectName + ":" + usernameToAdd);
            return "add_member@" + projectName + ":" + usernameToAdd;
        }
    }
}

class ShowMembers extends CLICommand {

    protected ShowMembers() throws RemoteException, NotBoundException {
    }

    public String manage(Scanner scanner) {
        String projectName = scanner.next();
        if(projectName.contains(":") || projectName.contains("@")){
            return "fail";
        } else {
            System.out.println("Sending command show_members@" + projectName);
            return "show_members@" + projectName;
        }
    }
}

class ListProjects extends CLICommand {

    protected ListProjects() throws RemoteException, NotBoundException {
    }

    public String manage(Scanner scanner) {
        System.out.println("Sending command list_projects");
        return "list_projects";
    }
}

class ShowCards extends CLICommand {

    protected ShowCards() throws RemoteException, NotBoundException {
    }

    public String manage(Scanner scanner) {
        String projectName = scanner.next();
        System.out.println("projectName");
        if(projectName.contains(":") || projectName.contains("@")){
            return "fail";
        } else {
            System.out.println("Sending command show_cards@" + projectName);
            return "show_cards@" + projectName;
        }
    }
}

class ShowCard extends CLICommand {

    protected ShowCard() throws RemoteException, NotBoundException {
    }

    public String manage(Scanner scanner) {
        String projectName = scanner.next();
        String cardName = scanner.next();

        if(projectName.contains(":") || projectName.contains("@") || cardName.contains(":") || cardName.contains("@")){
            return "fail";
        } else {
            System.out.println("Sending command show_card@" + projectName + ":" + cardName);
            return "show_card@" + projectName + ":" + cardName;
        }
    }
}

class GetCardHistory extends CLICommand {
    protected GetCardHistory() throws RemoteException, NotBoundException {
    }

    public String manage(Scanner scanner) {
        String projectName = scanner.next();
        String cardName = scanner.next();
        if(projectName.contains(":") || projectName.contains("@") || cardName.contains(":") || cardName.contains("@")){
            return "fail";
        } else {
            System.out.println("Sending command get_card_history@" + projectName + ":" + cardName);
            return "get_card_history@" + projectName + ":" + cardName;
        }
    }
}

class ChangeStatus extends CLICommand {
    protected ChangeStatus() throws RemoteException, NotBoundException {
    }

    public String manage(Scanner scanner) {
        String projectName = scanner.next();
        String cardName = scanner.next();
        String startingList = scanner.next();
        String endingList = scanner.next();

        if(projectName.contains(":") || projectName.contains("@") || cardName.contains("@") || cardName.contains(":") || startingList.contains(":") || startingList.contains("@") || endingList.contains(":") || endingList.contains("@")){
            return "fail";
        } else {
            System.out.println("Sending command change_status@" + projectName + ":" + cardName + ":" + startingList + ":" + endingList);
            return "change_status@" + projectName + ":" + cardName + ":" + startingList + ":" + endingList;
        }
    }
}

class Send extends CLICommand {
    protected Send() throws RemoteException, NotBoundException {
    }

    public String manage(Scanner scanner) {
        String projectName = scanner.next();
        String message = scanner.nextLine();

        if(projectName.contains(":") || projectName.contains("@") || message.contains(":") || message.contains("@")) {
            return "fail";
        } else {
            System.out.println("Sending command send@" + projectName + ":" + message);
            return "send@" + projectName + ":" + message;
        }
    }
}

class Read extends CLICommand {
    protected Read() throws RemoteException, NotBoundException {
    }

    public String manage(Scanner scanner) {
        String projectName = scanner.next();

        if(projectName.contains(":") || projectName.contains("@")) {
            return "fail";
        } else {
            System.out.println("Sending command read@" + projectName);
            return "read@" + projectName;
        }
    }
}

class DeleteProject extends CLICommand {
    protected DeleteProject() throws RemoteException, NotBoundException {
    }

    public String manage(Scanner scanner) {
        String projectName = scanner.next();
        if (projectName.contains(":") || projectName.contains("@")) {
            return "fail";
        } else {
            System.out.println("Sending command delete_project@" + projectName);
            return "delete_project@" + projectName;
        }
    }
}