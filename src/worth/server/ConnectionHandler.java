package worth.server;

import worth.MemberStatus;
import worth.exceptions.MemberNotFoundException;

import java.io.*;
import java.net.Socket;
import java.rmi.RemoteException;
import java.util.Scanner;

public class ConnectionHandler implements Runnable{
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private ServerNotification serverCallback;
    private Scanner sc;
    private boolean logged;
    private Member member;

    public ConnectionHandler(Socket socket, ServerNotification serverCallback) throws RemoteException {
        this.logged = false;
        this.clientSocket = socket;
        sc = new Scanner(System.in);
        this.serverCallback = serverCallback;
    }


    public void commandHandler(String cmd) {
        if(cmd == null) throw new NullPointerException();
        if(cmd.equals(".")) throw new IllegalArgumentException();
        String[] command = cmd.split("@");
        Project p;
        String[] info;
        try {
            switch (command[0]) {
                case "login":
                    if (!logged) {
                        System.out.println("Received login command");
                        info = command[1].split(":");
                        String username = info[0];
                        String password = info[1];

                        if (Database.getDatabase().containsUser(username)) {
                            member = Database.getDatabase().getUser(username);
                            if (member.getPassword().equals(password)) {
                                if (member.getMemberStatus() == MemberStatus.ONLINE) {
                                    System.out.println("User already logged in!");
                                    out.println("SERVER: User already logged in!");
                                } else {
                                    member.setMemberStatus(MemberStatus.ONLINE);
                                    System.out.println("Logged in!");
                                    out.println("SERVER: Logged in!");
                                    logged = true;
                                    serverCallback.sendMemberList(Database.getDatabase().getListUsers());
                                }
                            } else {
                                System.out.println("Wrong Password!");
                                out.println("SERVER: Wrong Password!");
                            }
                        }
                    } else {
                        System.out.println("Already logged in from this terminal");
                        out.println("SERVER: Already logged in from this terminal");
                    }
                    break;
                case "logout":
                    String username = command[1];
                    System.out.println("Received logout command");
                    if(!logged) {
                        System.out.println("No user was logged here");
                        out.println("SERVER: No user was logged here");

                    } else {
                        if(member.getUsername().equals(username)) {
                            logged = false;
                            member.setMemberStatus(MemberStatus.OFFLINE);
                            System.out.println("Logged out");
                            out.println("SERVER: logged out");
                        } else {
                            System.out.println("Cannot logout");
                            out.println("SERVER: cannot logout");
                        }
                    }
                    break;
                case "create_project":
                    String usernameCreator = member.getUsername();
                    String projectName = command[1];
                    System.out.println("Received create project command");
                    if(!logged) {
                        System.out.println("You must be logged in to create a project");
                        out.println("SERVER: You must be logged in to create a project");
                    } else {
                        if(Database.getDatabase().containsUser(usernameCreator)) {
                            Project project = new Project(projectName);
                            project.createDirectory(projectName);
                            project.addMember(usernameCreator);

                            System.out.println("Project created!");
                            out.println("SERVER: Project created!");
                        } else {
                            throw new MemberNotFoundException();
                        }
                    }
                    break;
                case "add_card":
                    /*
                    info[0]: projectName
                    info[1]: cardName
                    info[2]: cardDescription
                     */
                    info = command[1].split(":");
                    projectName = info[0];
                    String cardName = info[1];
                    String cardDescription = info[2];

                    p = new Project(projectName);

                    if(p.isInMemberList(member.getUsername())) {
                        p.createCard(cardName, cardDescription);
                        p.writeTodoList();
                    } else {
                        System.out.println("You are not in member list");
                        out.println("SERVER: You are not in member list");
                    }
                    break;
                case "add_member":
                    info = command[1].split(":");
                    projectName = info[0];
                    username = info[1];

                    p = new Project(projectName);

                    if(Database.containsUser(username)) {
                        if(p.isInMemberList(member.getUsername())) {
                            if (!p.isInMemberList(username)) {
                                p.addMember(username);
                                System.out.println("Member added");
                            } else {
                                System.out.println("User already present in the project");
                            }
                        } else {
                            System.out.println("You cannot add a member to a project if you're not in that project");
                        }
                    } else {
                        System.out.println("User not present");
                    }
                    break;
                case "show_members":
                    projectName = command[1];

                    p = new Project(projectName);
                    if(p.isInMemberList(member.getUsername())) {
                        out.println(p.showMembers());
                    } else {
                        System.out.println("No member in the project");
                    }
                    break;
                case "show_cards":

                    break;
                case "list_projects":
                    for(String s : member.projectList) {
                        System.out.println(s);
                    }
                    break;
                default:
                    System.out.println("Command not available");
                    out.println("SERVER: Command not available");
                    break;
            }
        } catch (MemberNotFoundException | RemoteException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        try {
            out = new PrintWriter(clientSocket.getOutputStream(),true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            while(true) {
                commandHandler(in.readLine());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
