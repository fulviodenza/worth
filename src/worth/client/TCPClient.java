package worth.client;

import worth.server.ServerNotificationInterface;

import java.io.*;
import java.net.Socket;
import java.rmi.NotBoundException;
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
                    System.out.println("Sending login command");
                    command = new LoginHandler();
                    entireCommand = command.manage(scanner);
                    if(!entireCommand.equals("fail")) {
                        System.out.printf("Sent %s command\n", entireCommand);
                        out.println(entireCommand);
                        String result = in.readLine();
                        System.out.println(result);
                        if(result.contains("[OK]")) {
                            list = new ListUsers();
                            System.out.print(list.manage(scanner));
                        }
                    } else {
                        System.out.println("Character : or @ not allowed");
                    }
                    break;
                case "logout":
                    System.out.println("Sending logout command");
                    System.out.println("write your username");
                    command = new LogoutHandler();
                    entireCommand = command.manage(scanner);
                    if(!entireCommand.equals("fail")) {
                        System.out.printf("Sent %s command\n", entireCommand);
                        out.println(entireCommand);
                        System.out.println(in.readLine());
                    } else {
                        System.out.println("Character : or @ not allowed");
                    }
                    break;
                case "list_users":
                    list = new ListUsers();
                    System.out.println(list.manage(scanner));
                    break;
                case "list_online_users":
                    ListOnlineUsers listOnline = new ListOnlineUsers();
                    System.out.println(listOnline.manage(scanner));
                    break;
                case "create_project":
                    System.out.println("Received create_project command");
                    System.out.println("insert the project name");
                    command = new CreateProject();
                    entireCommand = command.manage(scanner);
                    if(!entireCommand.equals("fail")) {
                        out.println(entireCommand);
                    } else {
                        System.out.println("Character : or @ not allowed");
                    }
                    break;
                case "add_card":
                    System.out.println("Received add_card command");
                    System.out.println("insert the project name, the card name and the description");
                    command = new CreateCard();
                    entireCommand = command.manage(scanner);
                    if(!entireCommand.equals("fail")) {
                        out.println(entireCommand);
                        String result = in.readLine(); //message+username
                        String[] info = result.split(":");
                        UDPServer.send(info[0], info[1], info[2]);
                    } else {
                        System.out.println("Character : or @ not allowed");
                    }
                    break;
                case "add_member":
                    System.out.println("Received add_member command");
                    System.out.println("insert the project name and the username you want to add to project");
                    command = new AddUser();
                    entireCommand = command.manage(scanner);
                    if(!entireCommand.equals("fail")) {
                        out.println(entireCommand);
                    } else {
                        System.out.println("Character : or @ not allowed");
                    }
                    break;
                case "show_members":
                    System.out.println("Received show_members command");
                    System.out.println("insert the project you want show members for");
                    command = new ShowMembers();
                    entireCommand = command.manage(scanner);
                    if(!entireCommand.equals("fail")) {
                        out.println(entireCommand);

                        String memberList = in.readLine();
                        memberList = memberList.replace("$", "\n");
                        System.out.print(memberList);
                    } else {
                        System.out.println("Character : or @ not allowed");
                    }
                    break;
                case "list_projects":
                    System.out.println("Received list_projects command");
                    command = new ListProjects();
                    entireCommand = command.manage(scanner);
                    if(!entireCommand.equals("fail")) {
                        out.println(entireCommand);
                        String result = in.readLine();
                        result = result.replace("$", "\n");
                        System.out.print(result);
                    } else {
                        System.out.println("Character : or @ not allowed");
                    }
                    break;
                case "show_cards":
                    System.out.println("Received show_cards command");
                    System.out.println("insert the project you want show cards for");
                    command = new ShowCards();
                    entireCommand = command.manage(scanner);
                    if(!entireCommand.equals("fail")) {
                        out.print(entireCommand);

                        String cardsList = in.readLine();
                        cardsList = cardsList.replace("$", "\n");
                        System.out.println(cardsList);
                    } else {
                        System.out.println("Character : or @ not allowed");
                    }
                    break;
                case "show_card":
                    System.out.println("Received show_card command");
                    System.out.println("insert the project name and the card name you want to know about");
                    command = new ShowCard();
                    entireCommand = command.manage(scanner);
                    if(!entireCommand.equals("fail")) {
                        out.println(entireCommand);
                        String cardInfo = in.readLine();
                        cardInfo = cardInfo.replace("$", "\n");
                        System.out.println(cardInfo);
                    } else {
                        System.out.println("Character : or @ not allowed");
                    }
                    break;
                case "get_card_history":
                    System.out.println("Received get_card_history command");
                    System.out.println("insert the project name and the card name you want to know the history about");
                    command = new GetCardHistory();
                    entireCommand = command.manage(scanner);
                    if(!entireCommand.equals("fail")) {
                        out.println(entireCommand);
                        String cardInfoHistory = in.readLine().replace(":", "\n");
                        System.out.print(cardInfoHistory);
                    } else {
                        System.out.println("Character : or @ not allowed");
                    }
                    break;
                case "change_status":
                    System.out.println("Received change status command");
                    System.out.println("insert the project name, the card name");
                    command = new ChangeStatus();
                    entireCommand = command.manage(scanner);
                    if(!entireCommand.equals("fail")) {
                        out.println(entireCommand);
                        String result = in.readLine(); //message+username
                        String[] info = result.split(":");
                        UDPServer.send(info[0], info[1], info[2]);
                    } else {
                        System.out.println("Character : or @ not allowed");
                    }
                    break;
                case "send":
                    System.out.println("Received send command");
                    System.out.println("insert the project name, the message");
                    command = new Send();
                    entireCommand = command.manage(scanner);
                    if(!entireCommand.equals("fail")) {
                        out.println(entireCommand);
                        String resultChat = in.readLine();
                        System.out.println(resultChat);
                        String[] data = resultChat.split(":");
                        if (data[0].contains("success")) {
                            UDPServer.send(data[2], data[1], data[3]);
                        }
                    } else {
                        System.out.println("Character : or @ not allowed");
                    }
                    break;
                case "read":
                    System.out.println("Received read command");
                    System.out.println("insert the project name");
                    command = new Read();
                    entireCommand = command.manage(scanner);
                    if(!entireCommand.equals("fail")) {
                        out.println(entireCommand);
                        String[] info = in.readLine().split(":");
                        startChat(info[1], info[0]);
                    } else {
                        System.out.println("Character : or @ not allowed");
                    }
                    break;
                default:
                    System.out.println("Invalid command "+cmd);
            }
        } catch (NoSuchElementException | IOException | NotBoundException e) {
            e.printStackTrace();
        }
    }

    private String readChat(String projectName) throws IOException {
        out.println("read@"+projectName);
        String[] input = in.readLine().split(":");
        String ip = input[1];
        System.out.println(ip);
        System.out.println(ip);
        return ip;
    }

    Thread chat;

    public void startChat(String ip, String projectName) throws IOException {
        UDPClient client = new UDPClient(readChat(projectName));
        chat = new Thread(client);
        chat.start();
    }

    private void stopChat() {
        if (chat != null)
            chat.interrupt();
    }

}