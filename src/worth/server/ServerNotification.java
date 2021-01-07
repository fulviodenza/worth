package worth.server;

import worth.client.ClientNotificationInterface;
import java.rmi.RemoteException;
import java.rmi.server.RemoteObject;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ServerNotification extends RemoteObject implements ServerNotificationInterface {

    private List<ClientNotificationInterface> clients;
    private List<String> usersList;

    public ServerNotification() throws RemoteException {
        super();
        clients = new ArrayList<>();
        usersList = new ArrayList<>();

    }

    public synchronized void register(ClientNotificationInterface client, String member) throws RemoteException{

        if(!clients.contains(client)) {
            usersList.add(member);
            clients.add(client);
            System.out.println("New client registered");
        }
    }

    public synchronized void unregister(ClientNotificationInterface client, String member) throws RemoteException {
        if(clients.contains(client)) {
            clients.remove(client);
            System.out.println("Client removed");
        }
        if(usersList.contains(member)) {
            usersList.remove(member);
            System.out.println("member removed");
        }
        System.out.println("Client unregistered");
    }

    public void sendMemberList(String memberList) throws RemoteException {
        doCallbacks(memberList);
    }

    private synchronized void doCallbacks(String memberList) throws RemoteException {
        Iterator i = clients.iterator();
        while (i.hasNext()) {
            ClientNotificationInterface client = (ClientNotificationInterface) i.next();
            client.notify(memberList);
        }
    }
}
