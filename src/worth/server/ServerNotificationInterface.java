package worth.server;

import worth.client.ClientNotificationInterface;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface ServerNotificationInterface extends Remote {

    public void sendMemberList(String memberList) throws RemoteException;
    public void register(ClientNotificationInterface client, String member) throws RemoteException;
    public void unregister(ClientNotificationInterface client, String member) throws RemoteException;

}
