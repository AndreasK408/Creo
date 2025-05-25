package user_update;

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;

public class PasswordHasher {
    private static final int ITERATIONS = 1;
    private static final int MEMORY_KB = 6144;
    private static final int PARALLELISM = 4;
    private static final Argon2 argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id);

    public static String hashPassword(String password) {
        if (password == null) {
            throw new NullPointerException("Password cannot be null.");
        }
        char[] passwordChars = password.toCharArray();
        try {
            return argon2.hash(ITERATIONS, MEMORY_KB, PARALLELISM, passwordChars);
        } catch (Exception e) {
            throw new RuntimeException("Password hashing failed", e);
        } finally {
            argon2.wipeArray(passwordChars);
        }
    }
}