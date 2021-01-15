package worth.server;

import java.util.Random;

public class IPGenerator {
    public static String generateIPAddress() {

        Random r = new Random();
        return (r.nextInt(256) + "." + r.nextInt(256) + "." + r.nextInt(256) + "." + r.nextInt(256));
    }
}