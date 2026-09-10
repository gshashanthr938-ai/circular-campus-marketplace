package com.campusmarket.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

/**
 * Small helper to hash passwords with SHA-256 so we never store them
 * in plain text. (A production system would use bcrypt/Argon2 with a
 * per-user salt; SHA-256 keeps this academic project simple but honest.)
 */
public final class PasswordUtil {

    private PasswordUtil() {
    }

    public static String hash(String plain) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(plain.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(digest.length * 2);
            for (byte b : digest) {
                sb.append(Character.forDigit((b >> 4) & 0xF, 16));
                sb.append(Character.forDigit(b & 0xF, 16));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }

    public static boolean matches(String plain, String storedHash) {
        return hash(plain).equals(storedHash);
    }
}
