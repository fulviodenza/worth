package worth.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;

public class Main {
    public static void main(String args[]) throws IOException {
        TCPClient clientConnection = new TCPClient();

        while(!Thread.interrupted()) {
            try {
                System.out.print("> ");
                InputStreamReader streamReader = new InputStreamReader(System.in);
                BufferedReader bufferedReader = new BufferedReader(streamReader);
                String command = bufferedReader.readLine();
                clientConnection.compute(command);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
