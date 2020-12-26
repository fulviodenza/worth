package worth.client;

import java.util.NoSuchElementException;
import java.util.Scanner;

public class DefaultCommandHandler {

    public static void compute(String cmd) {
        try {
            Scanner scanner = new Scanner(System.in);
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
                    System.out.println("Do something");
            }
        } catch (NoSuchElementException e) {
            e.printStackTrace();
        }
    }
}
