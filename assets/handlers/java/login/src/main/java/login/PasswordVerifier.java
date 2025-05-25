package login;

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;

public class PasswordVerifier {
    private static final Argon2 argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id);

    public static boolean verifyPassword(String storedHash, String providedPassword) {
        if (storedHash == null || providedPassword == null) {
            return false;
        }
        char[] passwordChars = providedPassword.toCharArray();
        try {
            System.out.println("Verifying password...");
            return argon2.verify(storedHash, passwordChars);
        } catch (IllegalArgumentException e) {
            System.err.println("Password verification failed (hash mismatch or invalid hash): " + e.getMessage());
            return false; // Bei Hash-Mismatch oder anderen Fehler
        } catch (Exception e) {
            System.err.println("Error during password verification: " + e.getMessage());
            return false;
        } finally {
            argon2.wipeArray(passwordChars);
        }
    }
}