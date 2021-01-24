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
                        System.out.println(Constant.ANSI_BLUE+"Received login command"+Constant.ANSI_RESET);
                        info = command[1].split(":");
                        String username = info[0];
                        String password = info[1];

                        if(Files.exists(Path.of("../database.json"))) {
                            if (Database.getDatabase().containsUser(username)) {
                                member = Database.getDatabase().getUser(username);
                                if (member.getPassword().equals(password)) {
                                    if (member.getMemberStatus() == MemberStatus.ONLINE) {
                                        System.out.println(Constant.ANSI_RED+"User already logged in!"+Constant.ANSI_RESET);
                                        out.println(Constant.ANSI_RED+"[KO] SERVER: User already logged in!"+Constant.ANSI_RESET);
                                    } else {
                                        member.setMemberStatus(MemberStatus.ONLINE);
                                        System.out.println(member.getUsername() + " logged in!"+Constant.ANSI_RESET);
                                        out.println(Constant.ANSI_GREEN+"[OK] SERVER: Logged in!"+Constant.ANSI_RESET);
                                        logged = true;
                                        serverCallback.sendMemberList(Database.getDatabase().getListUsers());
                                    }
                                } else {
                                    System.out.println(Constant.ANSI_RED+"Wrong Password!"+Constant.ANSI_RESET);
                                    out.println(Constant.ANSI_RED+"SERVER: Wrong Password!"+Constant.ANSI_RESET);
                                }
                            } else {
                                System.out.println(Constant.ANSI_RED+"User not present"+Constant.ANSI_RESET);
                                out.println(Constant.ANSI_RED+"User not present"+Constant.ANSI_RESET);
                            }
                        } else {
                            System.out.println(Constant.ANSI_RED+"User not present"+Constant.ANSI_RESET);
                            out.println(Constant.ANSI_RED+"User not present"+Constant.ANSI_RESET);
                        }
                    } else {
                        System.out.println(Constant.ANSI_RED+"Already logged in from this terminal"+Constant.ANSI_RESET);
                        out.println(Constant.ANSI_RED+"SERVER: Already logged in from this terminal"+Constant.ANSI_RESET);
                    }
                    break;
                case "logout":
                    if(command[1] != null) {
                        String username = command[1];
                        System.out.println(Constant.ANSI_BLUE+"Received logout command"+Constant.ANSI_RESET);
                        if (!logged) {
                            System.out.println(Constant.ANSI_RED+"No user was logged here"+Constant.ANSI_RESET);
                            out.println(Constant.ANSI_RED+"SERVER: No user was logged here"+Constant.ANSI_RESET);

                        } else {
                            if (member.getUsername().equals(username)) {
                                logged = false;
                                member.setMemberStatus(MemberStatus.OFFLINE);
                                System.out.println(Constant.ANSI_GREEN+"Logged out"+Constant.ANSI_RESET);
                                out.println(Constant.ANSI_GREEN+"SERVER: logged out"+Constant.ANSI_RESET);
                            } else {
                                System.out.println(member.getUsername() + " " + username);
                                System.out.println(Constant.ANSI_RED+"Cannot logout"+Constant.ANSI_RESET);
                                out.println(Constant.ANSI_RED+"SERVER: cannot logout"+Constant.ANSI_RESET);
                            }
                        }
                    } else {
                        System.out.println(Constant.ANSI_BLUE+"Received logout command"+Constant.ANSI_RESET);
                        if (!logged) {
                            System.out.println(Constant.ANSI_RED+"No user was logged here"+Constant.ANSI_RESET);
                            out.println(Constant.ANSI_RED+"SERVER: No user was logged here"+Constant.ANSI_RESET);

                        } else {
                            logged = false;
                            member.setMemberStatus(MemberStatus.OFFLINE);
                            System.out.println(Constant.ANSI_GREEN+"Logged out"+Constant.ANSI_RESET);
                            out.println(Constant.ANSI_GREEN+"SERVER: logged out"+Constant.ANSI_RESET);
                        }
                    }
                    break;
                case "create_project":
                    String usernameCreator = member.getUsername();
                    String projectName = command[1];
                    System.out.println(Constant.ANSI_BLUE+"Received create project command"+Constant.ANSI_RESET);
                    if(!logged) {
                        System.out.println(Constant.ANSI_RED+"You must be logged in to create a project"+Constant.ANSI_RESET);
                        out.println(Constant.ANSI_RED+"SERVER: You must be logged in to create a project"+Constant.ANSI_RESET);
                    } else {
                        if(Database.getDatabase().containsUser(usernameCreator)) {
                            if(!Files.exists(Path.of("../projects/" + projectName))) {
                                Project project = new Project(projectName);
                                if (project.createDirectory(projectName) == 0) {
                                    project.addMember(usernameCreator);
                                    Database.updateProjectList(usernameCreator, projectName);
                                    System.out.println(Constant.ANSI_GREEN+"Project created!"+Constant.ANSI_RESET);
                                    out.println(Constant.ANSI_GREEN+"SERVER: Project created!"+Constant.ANSI_RESET);
                                } else {
                                    System.out.println(Constant.ANSI_RED+"Project already exists"+Constant.ANSI_RESET);
                                    out.println(Constant.ANSI_RED+"SERVER: Project already exists"+Constant.ANSI_RESET);
                                }
                            } else {
                                System.out.println(Constant.ANSI_RED+"Project with the given name already exists!"+Constant.ANSI_RESET);
                                out.println(Constant.ANSI_RED+"Project with the given name already exists!"+Constant.ANSI_RESET);
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
                        System.out.println(Constant.ANSI_RED+"You must be logged in to add card"+Constant.ANSI_RESET);
                        out.println(Constant.ANSI_RED+"SERVER: You must be logged in to add card"+Constant.ANSI_RESET);
                    } else {
                        if(Files.exists(Path.of("../projects/" + projectName))) {
                            if (p.isInMemberList(member.getUsername())) {
                                if (!p.isInCardsList(cardName)) {
                                    p.createCard(cardName, cardDescription);
                                    p.writeTodoList();
                                    System.out.println(p.getIpAddress() + ":" + "New Card Added" + ":" + member.getUsername());
                                    out.println(p.getIpAddress() + ":" + "New Card Added" + ":" + member.getUsername());

                                } else {
                                    System.out.println(Constant.ANSI_RED+"Card already present"+Constant.ANSI_RESET);
                                    out.println(Constant.ANSI_RED+"SERVER: Card already present"+Constant.ANSI_RESET);
                                }
                            } else {
                                System.out.println(Constant.ANSI_RED+"You are not in member list"+Constant.ANSI_RESET);
                                out.println(Constant.ANSI_RED+"SERVER: You are not in member list"+Constant.ANSI_RESET);
                            }
                        } else {
                            System.out.println(Constant.ANSI_RED+"The given name does not corresponds to any project in the database"+Constant.ANSI_RESET);
                            out.println(Constant.ANSI_RED+"The given name does not corresponds to any project in the database"+Constant.ANSI_RESET);
                        }
                    }
                    break;
                case "add_member":
                    info = command[1].split(":");
                    projectName = info[0];
                    String username = info[1];

                    p = new Project(projectName);

                    if(!logged) {
                        System.out.println(Constant.ANSI_RED+"You must be logged in to add members"+Constant.ANSI_RESET);
                        out.println(Constant.ANSI_RED+"SERVER: You must be logged in to add members"+Constant.ANSI_RESET);
                    } else {
                        if(Files.exists(Path.of("../projects/" + projectName))) {
                            if (Database.containsUser(username)) {
                                if (p.isInMemberList(member.getUsername())) {
                                    if (!p.isInMemberList(username)) {
                                        p.addMember(username);
                                        Database.updateProjectList(username, projectName);
                                        System.out.println(Constant.ANSI_GREEN+"Member added"+Constant.ANSI_RESET);
                                        out.println(Constant.ANSI_GREEN+"SERVER: Member added"+Constant.ANSI_RESET);
                                    } else {
                                        System.out.println(Constant.ANSI_RED+"User already present in the project"+Constant.ANSI_RESET);
                                        out.println(Constant.ANSI_RED+"SERVER: User already present in the project"+Constant.ANSI_RESET);
                                    }
                                } else {
                                    System.out.println(Constant.ANSI_RED+"You cannot add a member to a project if you're not in that project"+Constant.ANSI_RESET);
                                    out.println(Constant.ANSI_RED+"SERVER: You cannot add a member to a project if you're not in that project"+Constant.ANSI_RESET);
                                }
                            } else {
                                System.out.println(Constant.ANSI_RED+"User not present"+Constant.ANSI_RESET);
                                out.println(Constant.ANSI_RED+"SERVER: User not present"+Constant.ANSI_RESET);
                            }
                        } else {
                            System.out.println(Constant.ANSI_RED+"The given name does not corresponds to any project in the database"+Constant.ANSI_RESET);
                            out.println(Constant.ANSI_RED+"SERVER: The given name does not corresponds to any project in the database"+Constant.ANSI_RESET);
                        }
                    }
                    break;
                case "show_members":
                    projectName = command[1];

                    p = new Project(projectName);

                    if(!logged) {
                        System.out.println(Constant.ANSI_RED+"You must be logged in to show members"+Constant.ANSI_RESET);
                        out.println(Constant.ANSI_RED+"SERVER: You must be logged in to show members"+Constant.ANSI_RESET);
                    } else {
                        if(Files.exists(Path.of("../projects/" + projectName))) {
                            if (p.isInMemberList(member.getUsername())) {
                                out.println(Constant.ANSI_YELLOW+p.showMembers()+Constant.ANSI_RESET);
                            } else {
                                System.out.println(Constant.ANSI_RED+"No member in the project"+Constant.ANSI_RESET);
                                out.println(Constant.ANSI_RED+"SERVER: No member in the project"+Constant.ANSI_RESET);
                            }
                        }
                    }
                    break;
                case "show_cards":
                    System.out.println(Constant.ANSI_BLUE+"Received show_cards command"+Constant.ANSI_RESET);
                    projectName = command[1];

                    p = new Project(projectName);
                    if(logged) {
                        if(Files.exists(Path.of("../projects/" + projectName))) {
                            if (p.isInMemberList(member.getUsername())) {
                                out.println(Constant.ANSI_YELLOW+p.showCards()+Constant.ANSI_RESET);
                            } else {
                                System.out.println(Constant.ANSI_RED+"No member in the project"+Constant.ANSI_RESET);
                                out.println(Constant.ANSI_RED+"SERVER: No member in the project"+Constant.ANSI_RESET);
                            }
                        } else {
                            System.out.println(Constant.ANSI_RED+"The given name does not corresponds to any project in the database"+Constant.ANSI_RESET);
                            out.println(Constant.ANSI_RED+"The given name does not corresponds to any project in the database"+Constant.ANSI_RESET);
                        }
                    }
                    break;
                case "list_projects":
                    if(!logged) {
                        System.out.println(Constant.ANSI_RED+"You must be logged in to list projects"+Constant.ANSI_RESET);
                        out.println(Constant.ANSI_RED+"SERVER: You must be logged in to list projects"+Constant.ANSI_RESET);
                    } else {
                        StringBuilder output = new StringBuilder();
                        for (String s : member.projectList) {
                            output.append(s).append("$");
                        }
                        out.println(Constant.ANSI_YELLOW+output+Constant.ANSI_RESET);
                    }
                    break;
                case "show_card":
                    info = command[1].split(":");
                    projectName = info[0];
                    cardName = info[1];
                    p = new Project(projectName);

                    if(!logged) {
                        System.out.println(Constant.ANSI_RED+"You must be logged in to show card"+Constant.ANSI_RESET);
                        out.println(Constant.ANSI_RED+"SERVER: You must be logged in to show card"+Constant.ANSI_RESET);
                    } else {
                        if (Files.exists(Path.of("../projects/" + projectName))) {

                            if (p.isInMemberList(member.getUsername())) {
                                out.println(Constant.ANSI_YELLOW+p.showCard(cardName));
                            } else {
                                System.out.println(Constant.ANSI_RED+"No member in the project"+Constant.ANSI_RESET);
                                out.println(Constant.ANSI_RED+"SERVER: No member in the project"+Constant.ANSI_RESET);
                            }
                        } else {
                            System.out.println(Constant.ANSI_RED+"The given name does not corresponds to any project in the database"+Constant.ANSI_RESET);
                            out.println(Constant.ANSI_RED+"SERVER: The given name does not corresponds to any project in the database"+Constant.ANSI_RESET);
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
                        System.out.println(Constant.ANSI_RED+"You must be logged in to change card status"+Constant.ANSI_RESET);
                        out.println(Constant.ANSI_RED+"SERVER: You must be logged in to change card status"+Constant.ANSI_RESET);
                    } else {
                        if(Files.exists(Path.of("../projects/" + projectName))) {
                            if (p.isInMemberList(member.getUsername())) {
                                p.moveCard(cardName, oldList, newList);
                                System.out.println(Constant.ANSI_GREEN+"Card status changed"+Constant.ANSI_RESET);
                                out.println(p.getIpAddress() + ":" + "Card status changed to " + newList + ":" + member.getUsername());
                            } else {
                                System.out.println(Constant.ANSI_RED+"Only members of the project can change card status"+Constant.ANSI_RESET);
                                out.println(Constant.ANSI_RED+"SERVER: Only members of the project can change card status"+Constant.ANSI_RESET);
                            }
                        } else {
                            System.out.println(Constant.ANSI_RED+"The given name does not corresponds to any project in the database"+Constant.ANSI_RESET);
                            out.println(Constant.ANSI_RED+"SERVER: The given name does not corresponds to any project in the database"+Constant.ANSI_RESET);
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
                        System.out.println(Constant.ANSI_RED+"You must be logged in to get card history"+Constant.ANSI_RESET);
                        out.println(Constant.ANSI_RED+"SERVER: You must be logged in to get card history"+Constant.ANSI_RESET);
                    } else {
                        if(Files.exists(Path.of("../projects/" + projectName))) {
                            if (p.isInMemberList(member.getUsername())) {
                                outputHistory = p.cardHistory(cardName);
                            }
                            out.println(Constant.ANSI_GREEN+outputHistory+Constant.ANSI_RESET);
                        } else {
                            System.out.println(Constant.ANSI_RED+"The given name does not corresponds to any project in the database"+Constant.ANSI_RESET);
                            out.println(Constant.ANSI_RED+"SERVER: The given name does not corresponds to any project in the database"+Constant.ANSI_RESET);
                        }
                    }
                    break;
                case "send":
                    System.out.println(Constant.ANSI_BLUE+"Received send command"+Constant.ANSI_RESET);
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
                            System.out.println(Constant.ANSI_RED+"The given name does not corresponds to any project in the database"+Constant.ANSI_RESET);
                            out.println(Constant.ANSI_RED+"The given name does not corresponds to any project in the database"+Constant.ANSI_RESET);
                        }
                    }
                    break;
                case "read":
                    System.out.println(Constant.ANSI_BLUE+"Received read command"+Constant.ANSI_RESET);
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
                            System.out.println(Constant.ANSI_RED+"The given name does not corresponds to any project in the database"+Constant.ANSI_RESET);
                            out.println(Constant.ANSI_RED+"SERVER: The given name does not corresponds to any project in the database"+Constant.ANSI_RESET);
                        }
                    } else {
                        System.out.println(Constant.ANSI_RED+"Project does not exists!"+Constant.ANSI_RESET);
                        out.println(Constant.ANSI_RED+"SERVER: Project does not exists!:[KO]"+Constant.ANSI_RESET);
                    }
                    break;
                case "delete_project":
                    System.out.println(Constant.ANSI_BLUE+"Received delete project command"+Constant.ANSI_RESET);
                    projectName = command[1];

                    p = new Project(projectName);

                    if(logged) {
                        if(Files.exists(Path.of("../projects/" + projectName))) {
                            if (p.isInMemberList(member.getUsername())) {
                                if (p.areAllCardsDone()) {
                                    System.out.println(Constant.ANSI_GREEN+"Deleting project"+Constant.ANSI_RESET);
                                    out.println(Constant.ANSI_GREEN+"SERVER: Deleting project"+Constant.ANSI_RESET);
                                    member.removeFromProject(projectName);
                                    p.deleteDir(new File("../projects/" + projectName + "/"));
                                } else {
                                    System.out.println(Constant.ANSI_RED+"Not all cards are in DONE state"+Constant.ANSI_RESET);
                                    out.println(Constant.ANSI_RED+"SERVER: Not all cards are in DONE state"+Constant.ANSI_RESET);
                                }
                            }
                        } else {
                            System.out.println(Constant.ANSI_RED+"The given name does not corresponds to any project in the database"+Constant.ANSI_RESET);
                            out.println(Constant.ANSI_RED+"SERVER: The given name does not corresponds to any project in the database"+Constant.ANSI_RESET);
                        }
                    }
                    break;
                default:
                    System.out.println(Constant.ANSI_RED+"Command not available"+Constant.ANSI_RESET);
                    out.println(Constant.ANSI_RED+"SERVER: Command not available"+Constant.ANSI_RESET);
                    break;
            }
        } catch (MemberNotFoundException | RemoteException e) {
            e.printStackTrace();
        } /*catch (ArrayIndexOutOfBoundsException a) {
            System.out.println("Not enough parameters");
            out.println("Not enough parameters");
        }*/
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
