package worth.server;

import worth.MemberStatus;
import worth.exceptions.MemberNotFoundException;

import java.io.*;
import java.net.Socket;
import java.nio.file.Paths;
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
                                    out.println("[KO] SERVER: User already logged in!");
                                } else {
                                    member.setMemberStatus(MemberStatus.ONLINE);
                                    System.out.println(member.getUsername() + " logged in!");
                                    out.println("[OK] SERVER: Logged in!");
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
                            Database.updateProjectList(usernameCreator, projectName);
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

                    if(!logged) {
                        System.out.println("You must be logged in to add card");
                        out.println("SERVER: You must be logged in to add card");
                    } else {
                        if (p.isInMemberList(member.getUsername())) {
                            if(!p.isInCardsList(cardName)) {
                                p.createCard(cardName, cardDescription);
                                p.writeTodoList();
                                out.println("New card added"+":"+p.getIpAddress()+":"+member.getUsername());
                            } else {
                                System.out.println("Card already present");
                                out.println("SERVER: Card already present");
                            }
                        } else {
                            System.out.println("You are not in member list");
                            out.println("SERVER: You are not in member list");
                        }
                    }
                    break;
                case "add_member":
                    info = command[1].split(":");
                    projectName = info[0];
                    username = info[1];

                    p = new Project(projectName);

                    if(!logged) {
                        System.out.println("You must be logged in to add members");
                        out.println("SERVER: You must be logged in to add members");
                    } else {
                        if (Database.containsUser(username)) {
                            if (p.isInMemberList(member.getUsername())) {
                                if (!p.isInMemberList(username)) {
                                    p.addMember(username);
                                    Database.updateProjectList(username, projectName);
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
                    }
                    break;
                case "show_members":
                    projectName = command[1];

                    p = new Project(projectName);

                    if(!logged) {
                        System.out.println("You must be logged in to show members");
                        out.println("SERVER: You must be logged in to show members");
                    } else {
                        if (p.isInMemberList(member.getUsername())) {
                            out.println(p.showMembers());
                        } else {
                            System.out.println("No member in the project");
                        }
                    }
                    break;
                case "show_cards":
                    System.out.println("Received show_cards command");
                    projectName = command[1];

                    p = new Project(projectName);
                    if(p.isInMemberList(member.getUsername())) {
                        out.println(p.showCards());
                    } else {
                        System.out.println("No member in the project");
                    }
                    break;
                case "list_projects":
                    if(!logged) {
                        System.out.println("You must be logged in to list projects");
                        out.println("SERVER: You must be logged in to list projects");
                    } else {
                        StringBuilder output = new StringBuilder();
                        for (String s : member.projectList) {
                            output.append(s).append("$");
                        }
                        out.println(output);
                    }
                    break;
                case "show_card":
                    info = command[1].split(":");
                    projectName = info[0];
                    cardName = info[1];
                    p = new Project(projectName);

                    if(!logged) {
                        System.out.println("You must be logged in to show card");
                        out.println("SERVER: You must be logged in to show card");
                    } else {
                        if (p.isInMemberList(member.getUsername())) {
                            out.println(p.showCard(cardName));
                        } else {
                            System.out.println("No member in the project");
                            out.println("No member in the project");
                        }
                    }
                    break;
                case "change_status":
                    info = command[1].split(":");
                    projectName = info[0];
                    cardName = info[1];
                    String oldList = info[2];
                    String newList = info[3];
                    p = new Project(projectName);

                    if(!logged) {
                        System.out.println("You must be logged in to change card status");
                        out.println("SERVER: You must be logged in to change card status");
                    } else {
                        if (p.isInMemberList(member.getUsername())) {
                            p.moveCard(cardName, oldList, newList);
                            System.out.println("Card status changed");
                            out.println("Card status changed to "+newList+":"+p.getIpAddress()+":"+member.getUsername());
                        } else {
                            System.out.println("Only members of the project can change card status");
                            out.println("SERVER: Only members of the project can change card status");
                        }
                    }
                    break;
                case "get_card_history":
                    info = command[1].split(":");
                    projectName = info[0];
                    cardName = info[1];
                    String outputHistory = null;

                    p = new Project(projectName);

                    if(!logged) {
                        System.out.println("You must be logged in to get card history");
                        out.println("SERVER: You must be logged in to get card history");
                    } else {
                        if (p.isInMemberList(member.getUsername())) {
                            outputHistory = p.cardHistory(cardName);
                        }
                        out.println(outputHistory);
                    }
                    break;
                case "send":
                    System.out.println("Received send command");
                    if(logged) {
                        info = command[1].split(":");
                        projectName = info[0];
                        String msg = info[1];

                        p = new Project(projectName);

                        if (p.isInMemberList(member.getUsername())) {
                            out.println("success:"+p.getIpAddress()+":"+msg+":"+member.getUsername());
                        } else {
                            throw new MemberNotFoundException();
                        }
                    }
                    break;
                case "read":
                    System.out.println("Received read command");
                    projectName = command[1]; //read@projectName

                    p = new Project(projectName);

                    if(logged) {
                        if(Database.containsUser(member.getUsername())) {
                            if(p.isInMemberList(member.getUsername())) {
                                System.out.println(projectName+":"+p.getIpAddress()+":"+member.getUsername());
                                out.println(projectName+":"+p.getIpAddress());
                            }
                        }
                    }
                    break;
                case "delete_project":
                    System.out.println("Received delete project command");
                    projectName = command[1];

                    p = new Project(projectName);

                    if(logged) {
                        if(p.isInMemberList(member.getUsername())) {
                            if(p.areAllCardsDone()) {
                                System.out.println("Deleting project");
                                p.deleteDir(new File("../projects/"+projectName+"/"));
                            } else {
                                System.out.println("Not all cards are in DONE state");
                            }
                        }
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
