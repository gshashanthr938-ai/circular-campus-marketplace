package com.campusmarket.util;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/** Salted PBKDF2 credentials; legacy SHA-256 hashes are upgraded after login. */
public final class PasswordUtil {
    private static final int ITERATIONS = 600000;
    private PasswordUtil() {}
    private static byte[] derive(String password, byte[] salt, int iterations) throws Exception {
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterations, 256);
        try { return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded(); }
        finally { spec.clearPassword(); }
    }
    public static String hash(String plain) {
        try {
            byte[] salt = new byte[16]; new SecureRandom().nextBytes(salt);
            return "pbkdf2$" + ITERATIONS + "$" + Base64.getEncoder().encodeToString(salt) + "$"
                    + Base64.getEncoder().encodeToString(derive(plain, salt, ITERATIONS));
        } catch (Exception e) { throw new IllegalStateException("Password hashing failed",e); }
    }
    public static boolean matches(String plain, String stored) {
        if (plain == null || stored == null) return false;
        try {
            if (stored.startsWith("pbkdf2$")) {
                String[] parts=stored.split("\\$");
                int rounds=Integer.parseInt(parts[1]);
                if(rounds<10000 || rounds>2000000) return false;
                return MessageDigest.isEqual(Base64.getDecoder().decode(parts[3]),
                    derive(plain,Base64.getDecoder().decode(parts[2]),rounds));
            }
            byte[] old=java.util.HexFormat.of().parseHex(stored);
            return MessageDigest.isEqual(old,MessageDigest.getInstance("SHA-256").digest(plain.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) { return false; }
    }
}
