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
        try {
            switch (command[0]) {
                case "login":
                    if (!logged) {
                        System.out.println("Received login command");
                        String[] info = command[1].split(":");
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
                    //TODO IMPLEMENTARE PERSISTENZA DEI PROGETTI
                    String[] info = command[1].split(":");
                    String usernameCreator = info[0];
                    String projectName = info[1];
                    System.out.println("Received create project command");
                    if(!logged) {
                        System.out.println("You must be logged in to create a project");
                        out.println("SERVER: You must be logged in to create a project");
                    } else {
                        if(Database.getDatabase().containsUser(usernameCreator)) {
                            Project project = new Project(usernameCreator, projectName);
                            System.out.println("Project created!");
                            out.println("SERVER: Project created!");
                        } else {
                            throw new MemberNotFoundException();
                        }
                    }
                default:
                    System.out.println("Command not available");
                    out.println("SERVER: Command not available");
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
