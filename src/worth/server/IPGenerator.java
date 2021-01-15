package worth.server;

import java.util.concurrent.ThreadLocalRandom;

public class IPGenerator {
    public static String generateIPAddress() {

        return (ThreadLocalRandom.current().nextInt(224, 239+1) + "." + ThreadLocalRandom.current().nextInt(0, 256) + "." + ThreadLocalRandom.current().nextInt(0, 256+1) + "." + ThreadLocalRandom.current().nextInt(0, 256+1));
    }
}