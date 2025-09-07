package dev.ayush.authzServiceSimulator.security;

import java.security.SecureRandom;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.security.MessageDigest;

/**
 * Password hashing utility (salt + hash). Swap with BCrypt later.
 */
public class PasswordHasher {

    /**
     * Generate random salt (e.g., 16 bytes -> Base64).
     */
    public String generateSalt() {
        byte[] saltBytes = new byte[16];
        new SecureRandom().nextBytes(saltBytes);
        return Base64.getEncoder().encodeToString(saltBytes);
    }

    /**
     * Hash raw password with salt (e.g., SHA-256 over salt+password).
     * @return hex/base64 digest (consistent format).
     */
    public String hash(String rawPassword, String salt) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest((salt + rawPassword).getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hashBytes);
        }catch (Exception e){
            throw new RuntimeException("Hashing failed", e);
        }
    }

    /**
     * Verify raw password against stored hash+salt.
     */
    public boolean verify(String rawPassword, String salt, String expectedHash) {
        String computedHash = hash(rawPassword, salt);
        return computedHash.equalsIgnoreCase(expectedHash);
    }
}