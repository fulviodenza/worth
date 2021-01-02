package worth.server;

import worth.MemberStatus;
import worth.exceptions.MemberNotFoundException;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class ConnectionHandler implements Runnable{
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private Scanner sc;
    private boolean logged;
    private Member member;

    public ConnectionHandler(Socket socket) {
        this.logged = false;
        this.clientSocket = socket;
        sc = new Scanner(System.in);
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
                                    Database.getDatabase().printUsers();
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
                    System.out.println("Received logout command");
                    if(!logged) {
                        System.out.println("No user was logged here");
                        out.println("SERVER: No user was logged here");

                    } else {
                        logged = false;
                        member.setMemberStatus(MemberStatus.OFFLINE);
                        System.out.println("Logged out");
                        out.println("SERVER: logged out");
                    }
                    break;
                default:
                    System.out.println("Command not available");
                    out.println("SERVER: Command not available");
            }
        } catch (MemberNotFoundException e) {
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
