package worth.client;

import worth.server.ServerNotification;
import worth.server.ServerNotificationInterface;

import java.io.*;
import java.net.Socket;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class TCPClient {
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;


    public TCPClient() {
        boolean alreadyLogged = false;
    }

    public void startConnection() throws IOException {
        if (clientSocket == null)
            clientSocket = new Socket("localhost", 5456);
        if (out == null)
            out = new PrintWriter(clientSocket.getOutputStream(), true);
        if (in == null)
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    }

    public void stopConnection() throws IOException {
        in.close();
        out.close();
        clientSocket.close();
    }

    public void compute(String cmd) throws IOException {
        try {
            this.startConnection();
            Registry registryCB = LocateRegistry.getRegistry(7001);
            String name = "notification";
            ServerNotificationInterface server = (ServerNotificationInterface) registryCB.lookup(name);
            ClientNotification callbackObj = new ClientNotification();
            ClientNotificationInterface stub = (ClientNotificationInterface) UnicastRemoteObject.exportObject(callbackObj, 0);

            CLICommand command;
            String entireCommand;
            Scanner scanner = new Scanner(System.in);
            ListUsers list;
            switch(cmd) {
                case "register":
                    try {
                        RemoteHandlerClient rui = new RemoteHandlerClient();
                        System.out.println("Insert Username and Password");
                        System.out.print("> ");
                        String username = scanner.next();
                        String password = scanner.next();
                        rui.registerStub(username, password);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case "login":
                    //this.startConnection();
                    System.out.println("Sending login command");
                    command = new LoginHandler();
                    list = new ListUsers();
                    entireCommand = command.manage(scanner);
                    System.out.println(list.manage(scanner));
                    System.out.printf("Sent %s command\n", entireCommand);
                    out.println(entireCommand);
                    System.out.println(in.readLine());
                    break;
                case "logout":
                    System.out.println("Sending logout command");
                    command = new LogoutHandler();
                    entireCommand = command.manage(scanner);
                    System.out.printf("Sent %s command\n", entireCommand);
                    out.println(entireCommand);
                    System.out.println(in.readLine());
                    break;
                case "create_project":
                    break;
                case "list_users":
                    list = new ListUsers();
                    System.out.println(list.manage(scanner));
                    break;
                case "list_online_users":
                    ListOnlineUsers listOnline = new ListOnlineUsers();
                    System.out.println(listOnline.manage(scanner));
                    break;
                default:
                    System.out.println("Invalid command");
            }
        } catch (NoSuchElementException | IOException | NotBoundException e) {
            e.printStackTrace();
        }
    }
}