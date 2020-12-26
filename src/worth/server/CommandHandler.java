package worth.server;

public class CommandHandler {

    public CommandHandler() {}

    public void manageLoginCommand(String reg) {
        /*
        manageLoginCommand riceve una stringa del tipo username:password
        e separa username da password per poi iniziare la ricerca nel database
         */
        String[] credentials = reg.split(":");
        String username = credentials[0];
        String password = credentials[1];

        password = password.replace(System.getProperty("line.separator"), "");
        Member memberLogin = new Member(username, password);

    }

}
