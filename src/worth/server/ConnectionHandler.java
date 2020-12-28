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
                        String[] info = command[1].split(":");
                        String username = info[0];
                        String password = info[1];

                        if (Database.containsUser(username)) {
                            member = Database.getUser(username);
                            if (member.getPassword().equals(password)) {
                                if (member.getMemberStatus() == MemberStatus.ONLINE) {
                                    System.out.println("User already logged in!");
                                } else {
                                    System.out.println("Logged in!");
                                }
                            } else {
                                System.out.println("Wrong Password!");
                            }
                        }
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
