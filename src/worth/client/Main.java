package worth.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;

public class Main {
    public static void main(String args[]) {
        //ClientConnection cc = new ClientConnection(new InetSocketAddress("0.0.0.0", 5454));
        //cc.start();

        while(!Thread.interrupted()) {
            try {
                System.out.print("> ");
                InputStreamReader streamReader = new InputStreamReader(System.in);
                BufferedReader bufferedReader = new BufferedReader(streamReader);
                String command = bufferedReader.readLine();
                DefaultCommandHandler.compute(command);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
