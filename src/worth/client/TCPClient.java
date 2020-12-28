package worth.client;

import java.io.*;
import java.net.Socket;
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
            Scanner scanner = new Scanner(System.in);
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
                    this.startConnection();
                    System.out.println("Received login command");
                    CLICommand command = new LoginHandler();
                    String entireCommand = command.manage(scanner);
                    System.out.printf("Sent %s command\n", entireCommand);
                    out.println(entireCommand);
                    break;
                default:
                    System.out.println("Invalid command");
            }
        } catch (NoSuchElementException | IOException e) {
            e.printStackTrace();
        }
    }
}