package worth.server;

import worth.MemberStatus;
import worth.exceptions.MemberNotFoundException;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.rmi.RemoteException;
import java.util.Scanner;

public class ConnectionHandler implements Runnable{
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private ServerNotification serverCallback;
    private Scanner sc;
    public static boolean logged;
    public static Member member;

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

                        if(Files.exists(Path.of("../database.json"))) {
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
                            } else {
                                System.out.println("User not present");
                                out.println("User not present");
                            }
                        } else {
                            System.out.println("User not present");
                            out.println("User not present");
                        }
                    } else {
                        System.out.println("Already logged in from this terminal");
                        out.println("SERVER: Already logged in from this terminal");
                    }
                    break;
                case "logout":
                    if(command[1] != null) {
                        String username = command[1];
                        System.out.println("Received logout command");
                        if (!logged) {
                            System.out.println("No user was logged here");
                            out.println("SERVER: No user was logged here");

                        } else {
                            if (member.getUsername().equals(username)) {
                                logged = false;
                                member.setMemberStatus(MemberStatus.OFFLINE);
                                System.out.println("Logged out");
                                out.println("SERVER: logged out");
                            } else {
                                System.out.println(member.getUsername() + " " + username);
                                System.out.println("Cannot logout");
                                out.println("SERVER: cannot logout");
                            }
                        }
                    } else {
                        System.out.println("Received logout command");
                        if (!logged) {
                            System.out.println("No user was logged here");
                            out.println("SERVER: No user was logged here");

                        } else {
                            logged = false;
                            member.setMemberStatus(MemberStatus.OFFLINE);
                            System.out.println("Logged out");
                            out.println("SERVER: logged out");
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
                            if(!Files.exists(Path.of("../projects/" + projectName))) {
                                Project project = new Project(projectName);
                                if (project.createDirectory(projectName) == 0) {
                                    project.addMember(usernameCreator);
                                    Database.updateProjectList(usernameCreator, projectName);
                                    System.out.println("Project created!");
                                    out.println("SERVER: Project created!");
                                } else {
                                    System.out.println("Project already exists");
                                    out.println("SERVER: Project already exists");
                                }
                            } else {
                                System.out.println("Project with the given name already exists!");
                                out.println("Project with the given name already exists!");
                            }
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
                        if(Files.exists(Path.of("../projects/" + projectName))) {
                            if (p.isInMemberList(member.getUsername())) {
                                if (!p.isInCardsList(cardName)) {
                                    p.createCard(cardName, cardDescription);
                                    p.writeTodoList();
                                    System.out.println(p.getIpAddress() + ":" + "New Card Added" + ":" + member.getUsername());
                                    out.println(p.getIpAddress() + ":" + "New Card Added" + ":" + member.getUsername());

                                } else {
                                    System.out.println("Card already present");
                                    out.println("SERVER: Card already present");
                                }
                            } else {
                                System.out.println("You are not in member list");
                                out.println("SERVER: You are not in member list");
                            }
                        } else {
                            System.out.println("The given name does not corresponds to any project in the database");
                            out.println("The given name does not corresponds to any project in the database");
                        }
                    }
                    break;
                case "add_member":
                    info = command[1].split(":");
                    projectName = info[0];
                    String username = info[1];

                    p = new Project(projectName);

                    if(!logged) {
                        System.out.println("You must be logged in to add members");
                        out.println("SERVER: You must be logged in to add members");
                    } else {
                        if(Files.exists(Path.of("../projects/" + projectName))) {
                            if (Database.containsUser(username)) {
                                if (p.isInMemberList(member.getUsername())) {
                                    if (!p.isInMemberList(username)) {
                                        p.addMember(username);
                                        Database.updateProjectList(username, projectName);
                                        System.out.println("Member added");
                                        out.println("Member added");
                                    } else {
                                        System.out.println("User already present in the project");
                                        out.println("User already present in the project");
                                    }
                                } else {
                                    System.out.println("You cannot add a member to a project if you're not in that project");
                                    out.println("You cannot add a member to a project if you're not in that project");
                                }
                            } else {
                                System.out.println("User not present");
                                out.println("User not present");
                            }
                        } else {
                            System.out.println("The given name does not corresponds to any project in the database");
                            out.println("The given name does not corresponds to any project in the database");
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
                        if(Files.exists(Path.of("../projects/" + projectName))) {
                            if (p.isInMemberList(member.getUsername())) {
                                out.println(p.showMembers());
                            } else {
                                System.out.println("No member in the project");
                            }
                        }
                    }
                    break;
                case "show_cards":
                    System.out.println("Received show_cards command");
                    projectName = command[1];

                    p = new Project(projectName);
                    if(logged) {
                        if(Files.exists(Path.of("../projects/" + projectName))) {
                            if (p.isInMemberList(member.getUsername())) {
                                out.println(p.showCards());
                            } else {
                                System.out.println("No member in the project");
                            }
                        } else {
                            System.out.println("The given name does not corresponds to any project in the database");
                            out.println("The given name does not corresponds to any project in the database");
                        }
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
                        if (Files.exists(Path.of("../projects/" + projectName))) {

                            if (p.isInMemberList(member.getUsername())) {
                                out.println(p.showCard(cardName));
                            } else {
                                System.out.println("No member in the project");
                                out.println("No member in the project");
                            }
                        } else {
                            System.out.println("The given name does not corresponds to any project in the database");
                            out.println("The given name does not corresponds to any project in the database");
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
                        if(Files.exists(Path.of("../projects/" + projectName))) {
                            if (p.isInMemberList(member.getUsername())) {
                                p.moveCard(cardName, oldList, newList);
                                System.out.println("Card status changed");
                                out.println(p.getIpAddress() + ":" + "Card status changed to " + newList + ":" + member.getUsername());
                            } else {
                                System.out.println("Only members of the project can change card status");
                                out.println("SERVER: Only members of the project can change card status");
                            }
                        } else {
                            System.out.println("The given name does not corresponds to any project in the database");
                            out.println("The given name does not corresponds to any project in the database");
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
                        if(Files.exists(Path.of("../projects/" + projectName))) {
                            if (p.isInMemberList(member.getUsername())) {
                                outputHistory = p.cardHistory(cardName);
                            }
                            out.println(outputHistory);
                        } else {
                            System.out.println("The given name does not corresponds to any project in the database");
                            out.println("The given name does not corresponds to any project in the database");
                        }
                    }
                    break;
                case "send":
                    System.out.println("Received send command");
                    if(logged) {
                        info = command[1].split(":");
                        projectName = info[0];
                        String msg = info[1];

                        p = new Project(projectName);

                        if(Files.exists(Path.of("../projects/" + projectName))) {
                            if (p.isInMemberList(member.getUsername())) {
                                out.println("success:" + p.getIpAddress() + ":" + msg + ":" + member.getUsername());
                            } else {
                                throw new MemberNotFoundException();
                            }
                        } else {
                            System.out.println("The given name does not corresponds to any project in the database");
                            out.println("The given name does not corresponds to any project in the database");
                        }
                    }
                    break;
                case "read":
                    System.out.println("Received read command");
                    projectName = command[1]; //read@projectName

                    p = new Project(projectName);
                    if (logged) {
                        if(Files.exists(Path.of("../projects/" + projectName))) {
                            if (Database.containsUser(member.getUsername())) {
                                if (p.isInMemberList(member.getUsername())) {
                                    System.out.println(projectName + ":" + p.getIpAddress() + ":" + member.getUsername());
                                    out.println(projectName + ":" + p.getIpAddress());
                                }
                            }
                        } else {
                            System.out.println("The given name does not corresponds to any project in the database");
                            out.println("The given name does not corresponds to any project in the database");
                        }
                    } else {
                        System.out.println("Project does not exists!");
                        out.println("Project does not exists!:[KO]");
                    }
                    break;
                case "delete_project":
                    System.out.println("Received delete project command");
                    projectName = command[1];

                    p = new Project(projectName);

                    if(logged) {
                        if(Files.exists(Path.of("../projects/" + projectName))) {
                            if (p.isInMemberList(member.getUsername())) {
                                if (p.areAllCardsDone()) {
                                    System.out.println("Deleting project");
                                    member.removeFromProject(projectName);
                                    p.deleteDir(new File("../projects/" + projectName + "/"));
                                } else {
                                    System.out.println("Not all cards are in DONE state");
                                }
                            }
                        } else {
                            System.out.println("The given name does not corresponds to any project in the database");
                            out.println("The given name does not corresponds to any project in the database");
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
        } catch (ArrayIndexOutOfBoundsException a) {
            System.out.println("Not enough parameters");
            out.println("Not enough parameters");
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
