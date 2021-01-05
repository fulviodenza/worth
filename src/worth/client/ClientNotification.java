package worth.client;

import java.io.FileWriter;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.RemoteObject;
import java.util.logging.FileHandler;

public class ClientNotification extends RemoteObject implements ClientNotificationInterface {

    public ClientNotification() throws RemoteException {
        super();
    }

    public void notify(String memberList) throws RemoteException {

        memberList = memberList.replace("$", "\n");
        try {

            FileWriter w = new FileWriter("users.txt");
            w.write(memberList);
            w.flush();
            w.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
