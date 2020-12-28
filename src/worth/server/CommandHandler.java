package worth.server;

import worth.MemberStatus;
import worth.exceptions.MemberNotFoundException;

public class CommandHandler {

    public CommandHandler() {}

    public void manageLoginCommand(String reg) throws MemberNotFoundException {
        /*
        manageLoginCommand riceve una stringa del tipo username:password
        e separa username da password per poi iniziare la ricerca nel database
         */
        String[] credentials = reg.split(":");
        String username = credentials[0];
        String password = credentials[1];

        password = password.replace(System.getProperty("line.separator"), "");
        Member memberLogin = new Member(username, password);
        if(!Database.containsUser(username)) {
            throw new MemberNotFoundException("Member not found");
        } else {
            if(Database.getUser(username).getPassword().equals(password)) {
                Database.getUser(username).setMemberStatus(MemberStatus.ONLINE);
                System.out.println("Login Successful");
            }
        }
    }

}
