package worth.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ConnectionHandler extends Thread{

    final DataInputStream dis;
    final DataOutputStream dos;
    final Socket s;


    // Constructor
    public ConnectionHandler(Socket s, DataInputStream dis, DataOutputStream dos)
    {
        this.s = s;
        this.dis = dis;
        this.dos = dos;
    }

    @Override
    public void run()
    {
        String received;
        String toreturn;
        while (true)
        {
            try {

                // Ask user what he wants
                dos.writeUTF("What do you want?[Date | Time]..\n"+
                        "Type Exit to terminate connection.");

                // receive the answer from client
                received = dis.readUTF();

                if(received.equals("Exit"))
                {
                    System.out.println("Client " + this.s + " sends exit...");
                    System.out.println("Closing this connection.");
                    this.s.close();
                    System.out.println("Connection closed");
                    break;
                }

                // write on output stream based on the
                // answer from the client
                switch (received) {
                    /*
                    Il client manda una stringa del tipo login@username:password
                    il server separa login da @username:password e successivamente
                    separa username da password
                     */
                    case "login" :
                        //TODO
                        dos.writeUTF("contenuto risultato login operation");
                        break;
                    default:
                        dos.writeUTF("Invalid input");
                        break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            // closing resources
            this.dis.close();
            this.dos.close();

        } catch(IOException e){
            e.printStackTrace();
        }
    }
}
