package io.github.intisy.gradle.online.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * @author Finn Birich
 */
public class FileUtils {
    /**
     * Generates a unique string by hashing the input string using the SHA-256 algorithm.
     *
     * @param input The input string to be hashed.
     * @return A unique string representation of the input string, encoded in Base64 URL format without padding.
     * @throws RuntimeException if the SHA-256 algorithm is not found.
     */
    public static String generateUniqueString(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
        } catch (NoSuchAlgorithmException var3) {
            throw new RuntimeException("SHA-256 algorithm not found", var3);
        }
    }
}
