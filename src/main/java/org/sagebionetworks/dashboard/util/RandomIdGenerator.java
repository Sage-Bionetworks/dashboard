package org.sagebionetworks.dashboard.util;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.util.Random;

/**
 * Generates random string IDs.
 */
public class RandomIdGenerator {

    /**
     * Creates a ID generator that generates 4-char random IDs.
     * ID length 4, on an alphabet [a-zA-Z0-9], allows 14,776,336 unique IDs.
     */
    public RandomIdGenerator() {
        this(4);
    }

    /**
     * Creates a ID generator that generates random strings of a particular length.
     *
     * @param length The number of characters of the IDs generated.
     */
    public RandomIdGenerator(final int idLength) {

        if (idLength < 1) {
            throw new IllegalArgumentException("ID length must be at least 1.");
        }

        // Initialize random
        final String algorithm = "NativePRNG";
        final String provider = "SUN";
        try {
            random = SecureRandom.getInstance(algorithm, provider);
            random.nextBytes(new byte[4]); // Force seeding
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            throw new RuntimeException(e);
        }

        // Initialize the alphabet [a-zA-Z0-9]
        alphabet = new char[62];
        for (int i = 0; i < 26; i++) {
            alphabet[i] = (char) ('a' + i);
            int j = i + 26;
            alphabet[j] = (char) ('A' + i);
        }
        for (int i = 0; i < 10; i++) {
            int j = i + 26 + 26;
            alphabet[j] = (char) ('0' + i);
        }

        length = idLength;
    }

    /**
     * Generates a random ID.
     */
    public String newId() {
        char[] id = new char[length];
        for (int i = 0; i < length; i++) {
            int r = random.nextInt(alphabet.length);
            id[i] = alphabet[r];
        }
        return new String(id);
    }

    private final Random random;
    private final char[] alphabet;
    private final int length;
}
