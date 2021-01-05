package worth.client;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ClientNotificationInterface extends Remote {

    public void notify(String memberList) throws RemoteException;
}
