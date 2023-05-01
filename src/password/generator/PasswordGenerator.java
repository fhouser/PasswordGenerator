package password.generator;

import java.security.SecureRandom;

public class PasswordGenerator {
    public static final String LOWER_CASE = "abcdefghijklmnopqrstuvwxyz";
    public static final String UPPER_CASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public static final String DIGITS = "0123456789";
    public static final String SYMBOLS = "!@#$%^&*()_+-=[]{}|;':\",./<>?";

    public static final String ALL_CHARACTERS = LOWER_CASE + UPPER_CASE + DIGITS + SYMBOLS;

    private static final SecureRandom RANDOM = new SecureRandom();

    public static String generatePassword(final int length, final String characters) {
        StringBuilder passwordBuilder = new StringBuilder();

        for (int i = 0; i < length; i++) {
            int randomIndex = RANDOM.nextInt(characters.length());
            passwordBuilder.append(characters.charAt(randomIndex));
        }

        return passwordBuilder.toString();
    }
}