package hash;

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import java.util.HashMap;
import java.util.Map;

public class Hash {
    private static final int TIME_COST = 1;
    private static final int MEMORY_COST = 6144;
    private static final int PARALLELISM = 1;

    public static Map<String, String> hashPassword(String password) {
        Argon2 argon2 = Argon2Factory.create();
        String hash = argon2.hash(TIME_COST, MEMORY_COST, PARALLELISM, password.toCharArray());
        Map<String, String> result = new HashMap<>();
        result.put("hash", hash);
        return result;
    }
}