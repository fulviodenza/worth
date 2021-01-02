package worth.client;

import java.util.Scanner;

public abstract class CLICommand {

    TCPClient client = new TCPClient();

    public abstract String manage(Scanner scanner);
}

class LoginHandler extends CLICommand {

    public String manage(Scanner scanner) {
        String username = scanner.next();
        String password = scanner.next();
        String finalCommand = "login@"+username+":"+password;
        return finalCommand;
    }
}

class LogoutHandler extends CLICommand {

    public String manage(Scanner scanner) {
        return "logout";
    }
}