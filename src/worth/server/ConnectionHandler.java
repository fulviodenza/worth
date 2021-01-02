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
                case "register":
                    System.out.println("Received register command");
                    break;
                case "login":
                    if (!logged) {
                        System.out.println("Received login command");
                        String[] info = command[1].split(":");
                        System.out.println("1");

                        String username = info[0];
                        System.out.println("2");
                        String password = info[1];
                        System.out.println("3");

                        System.out.printf("%b", Database.getDatabase().containsUser(username));
                        Database.printUsers();
                        if (Database.getDatabase().containsUser(username)) {
                            member = Database.getDatabase().getUser(username);
                            if (member.getPassword().equals(password)) {
                                if (member.getMemberStatus() == MemberStatus.ONLINE) {
                                    System.out.println("User already logged in!");
                                } else {
                                    member.setMemberStatus(MemberStatus.ONLINE);
                                    System.out.println("Logged in!");
                                    logged = true;
                                }
                            } else {
                                System.out.println("Wrong Password!");
                            }
                        }
                    } else {
                        System.out.println("Already logged in from this terminal");
                    }
                    break;
                default:
                    System.out.println("Command not available");
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
