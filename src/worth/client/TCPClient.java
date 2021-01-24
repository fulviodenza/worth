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
    public static boolean alreadyLogged = false;

    public TCPClient() {
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

            final CLICommand[] command = new CLICommand[1];
            String entireCommand;
            Scanner scanner = new Scanner(System.in);
            ListUsers list;

            switch(cmd) {
                case "info":
                    BufferedReader br = new BufferedReader(new FileReader("../manPage.txt"));
                    String line;
                    while ((line = br.readLine()) != null) {
                        System.out.println(line);
                    }
                    break;
                case "exit":
                    if (alreadyLogged) {
                        System.out.println("Sending logout command");
                        System.out.println("write your username");
                        command[0] = new LogoutHandler();
                        entireCommand = command[0].manage(scanner);
                        if (!entireCommand.equals("fail")) {
                            System.out.printf("Sent %s command\n", entireCommand);
                            out.println(entireCommand);
                            System.out.println("Exiting...");
                            stopChat();
                            stopConnection();
                            System.out.println(in.readLine());

                        } else {
                            System.out.println("Character : or @ not allowed");
                        }
                    } else {
                        System.out.println("Exiting...");
                        stopConnection();
                        System.exit(0);
                    }


                    break;
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
                    alreadyLogged = true;
                    System.out.println("Sending login command");
                    command[0] = new LoginHandler();
                    entireCommand = command[0].manage(scanner);
                    if (!entireCommand.equals("fail")) {
                        System.out.printf("Sent %s command\n", entireCommand);
                        out.println(entireCommand);
                        String result = in.readLine();
                        System.out.println(result);
                        if (result.contains("[OK]")) {
                            list = new ListUsers();
                            System.out.print(list.manage(scanner));
                        }
                    } else {
                        System.out.println("Character : or @ not allowed");
                    }
                    break;
                case "logout":
                    alreadyLogged = false;
                    System.out.println("Sending logout command");
                    System.out.println("write your username");
                    command[0] = new LogoutHandler();
                    entireCommand = command[0].manage(scanner);
                    if (!entireCommand.equals("fail")) {
                        System.out.printf("Sent %s command\n", entireCommand);
                        out.println(entireCommand);
                        stopChat();
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
                    command[0] = new CreateProject();
                    entireCommand = command[0].manage(scanner);
                    if (!entireCommand.equals("fail")) {
                        out.println(entireCommand);
                    } else {
                        System.out.println("Character : or @ not allowed");
                    }
                    System.out.println(in.readLine());
                    break;
                case "add_card":
                    System.out.println("Received add_card command");
                    System.out.println("insert the project name, the card name and the description");
                    command[0] = new CreateCard();
                    entireCommand = command[0].manage(scanner);
                    if (!entireCommand.equals("fail")) {
                        out.println(entireCommand);
                        String result = in.readLine(); //message+username
                        String[] info = result.split(":");
                        System.out.println(info[1]);
                        UDPServer.send(info[1], info[0], info[2]);
                    } else {
                        System.out.println("Character : or @ not allowed");
                    }
                    break;
                case "add_member":
                    System.out.println("Received add_member command");
                    System.out.println("insert the project name and the username you want to add to project");
                    command[0] = new AddUser();
                    entireCommand = command[0].manage(scanner);
                    if (!entireCommand.equals("fail")) {
                        out.println(entireCommand);
                    } else {
                        System.out.println("Character : or @ not allowed");
                    }
                    System.out.println(in.readLine());
                    break;
                case "show_members":
                    System.out.println("Received show_members command");
                    System.out.println("insert the project you want show members for");
                    command[0] = new ShowMembers();
                    entireCommand = command[0].manage(scanner);
                    if (!entireCommand.equals("fail")) {
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
                    command[0] = new ListProjects();
                    entireCommand = command[0].manage(scanner);
                    if (!entireCommand.equals("fail")) {
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
                    command[0] = new ShowCards();
                    entireCommand = command[0].manage(scanner);
                    if (!entireCommand.equals("fail")) {
                        out.println(entireCommand);
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
                    command[0] = new ShowCard();
                    entireCommand = command[0].manage(scanner);
                    if (!entireCommand.equals("fail")) {
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
                    command[0] = new GetCardHistory();
                    entireCommand = command[0].manage(scanner);
                    if (!entireCommand.equals("fail")) {
                        out.println(entireCommand);
                        String cardInfoHistory = in.readLine().replace(":", " ");
                        System.out.print(cardInfoHistory);
                    } else {
                        System.out.println("Character : or @ not allowed");
                    }
                    break;
                case "change_status":
                    System.out.println("Received change status command");
                    System.out.println("insert the project name, the card name");
                    command[0] = new ChangeStatus();
                    entireCommand = command[0].manage(scanner);
                    if (!entireCommand.equals("fail")) {
                        out.println(entireCommand);
                        String result = in.readLine(); //message+username
                        if(result.contains("any project")) {
                            String[] info = result.split(":");
                            UDPServer.send(info[1], info[0], info[2]);
                        } else {
                            System.out.println(result);
                        }
                    } else {
                        System.out.println("Character : or @ not allowed");
                    }
                    break;
                case "send":
                    System.out.println("Received send command");
                    System.out.println("insert the project name, the message");
                    command[0] = new Send();
                    entireCommand = command[0].manage(scanner);
                    if (!entireCommand.equals("fail")) {
                        out.println(entireCommand);
                        String resultChat = in.readLine();
                        String[] data = resultChat.split(":");
                        if (data[0].contains("success")) {
                            UDPServer.send(data[2], data[1], data[3]);
                        } else {
                            System.out.println(resultChat);
                        }
                    } else {
                        System.out.println("Character : or @ not allowed");
                    }
                    break;
                case "read":
                    System.out.println("Received read command");
                    if (alreadyLogged) {
                        System.out.println("insert the project name");
                        command[0] = new Read();
                        entireCommand = command[0].manage(scanner);
                        if (!entireCommand.equals("fail")) {
                            out.println(entireCommand);
                            String[] info = in.readLine().split(":");
                            if(info[1].contains("[KO]")) {
                                System.out.println(info[0]);
                            }else {
                                startChat(info[1], info[0]);
                            }
                        } else {
                            System.out.println("Character : or @ not allowed");
                        }
                    } else {
                        System.out.println("You must be logged in!");
                    }
                    break;
                case "delete_project":
                    System.out.println("Received delete project");
                    if(alreadyLogged) {
                        command[0] = new DeleteProject();
                        entireCommand = command[0].manage(scanner);
                        System.out.println(entireCommand);

                        if (!entireCommand.equals("fail")) {
                            out.println(entireCommand);
                        } else {
                            System.out.println("Character : or @ not allowed or refused action");
                        }
                    } else {
                        System.out.println("You must be logged in!");
                    }
                    break;
                default:
                    System.out.println("Invalid command "+cmd);
            }
        } catch (NoSuchElementException | IOException | NotBoundException e) {
            e.printStackTrace();
        } catch (ArrayIndexOutOfBoundsException a) {
            System.out.println("Not enough parameters");
        }
    }



    private String readChat(String projectName) throws IOException {
        out.println("read@"+projectName);
        String[] input = in.readLine().split(":");
        String ip = input[1];
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