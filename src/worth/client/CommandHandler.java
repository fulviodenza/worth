package worth.client;

import java.util.NoSuchElementException;
import java.util.Scanner;

public abstract class CommandHandler {

    public void startCompute(String msg) {
        Scanner scanner = new Scanner(msg != null ? msg : "");
        compute(scanner);
        scanner.close();
    }

    public abstract void compute(Scanner scanner);
}

class DefaultCommandHandler extends CommandHandler {

    public void compute(Scanner scanner) {
        try {
            String cmd = scanner.next();
            switch(cmd) {
                case "register":
                    try {
                        RemoteHandlerClient rui = new RemoteHandlerClient();
                        String username = scanner.next();
                        String password = scanner.next();
                        rui.registerStub(username, password);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case "other_command":
                    System.out.println("Do something");
            }
        } catch (NoSuchElementException e) {
            e.printStackTrace();
        }
    }
}
