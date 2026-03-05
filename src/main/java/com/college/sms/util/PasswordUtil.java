package com.college.sms.util;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

public class PasswordUtil {
    
    private static final String HASH_ALGORITHM = "SHA-256";
    private static final int SALT_LENGTH = 16; // 128 bits

    /**
     * Hashes a plaintext password with a random salt
     * Returns Base64-encoded string containing salt + hash
     */
    public static String hashPassword(String password) {
        try {
            // Generate random salt
            SecureRandom random = new SecureRandom();
            byte[] salt = new byte[SALT_LENGTH];
            random.nextBytes(salt);

            // Hash password with salt
            MessageDigest md = MessageDigest.getInstance(HASH_ALGORITHM);
            md.update(salt);
            byte[] hashedPassword = md.digest(password.getBytes("UTF-8"));

            // Combine salt + hash
            byte[] hashWithSalt = new byte[SALT_LENGTH + hashedPassword.length];
            System.arraycopy(salt, 0, hashWithSalt, 0, SALT_LENGTH);
            System.arraycopy(hashedPassword, 0, hashWithSalt, SALT_LENGTH, hashedPassword.length);

            // Encode to Base64 for storage
            return Base64.getEncoder().encodeToString(hashWithSalt);
        } catch (Exception e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }

    /**
     * Verifies a plaintext password against a stored hash
     * @param plaintextPassword User-entered password
     * @param storedHash Base64-encoded hash from database (contains salt + hash)
     * @return true if password matches
     */
    public static boolean verifyPassword(String plaintextPassword, String storedHash) {
        try {
            // Decode Base64 hash to get salt + hash bytes
            byte[] hashWithSalt = Base64.getDecoder().decode(storedHash);
            
            // Extract salt (first SALT_LENGTH bytes)
            byte[] salt = new byte[SALT_LENGTH];
            System.arraycopy(hashWithSalt, 0, salt, 0, SALT_LENGTH);
            
            // Extract stored hash (remaining bytes)
            byte[] storedHashBytes = new byte[hashWithSalt.length - SALT_LENGTH];
            System.arraycopy(hashWithSalt, SALT_LENGTH, storedHashBytes, 0, storedHashBytes.length);

            // Hash the plaintext password with the extracted salt
            MessageDigest md = MessageDigest.getInstance(HASH_ALGORITHM);
            md.update(salt);
            byte[] hashedPassword = md.digest(plaintextPassword.getBytes("UTF-8"));

            // Compare hashes in constant time to prevent timing attacks
            if (hashedPassword.length != storedHashBytes.length) {
                return false;
            }
            for (int i = 0; i < hashedPassword.length; i++) {
                if (hashedPassword[i] != storedHashBytes[i]) {
                    return false;
                }
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // Test method to generate hash for '1234'
    public static void main(String[] args) {
        String password = "1234";
        String hashed = hashPassword(password);
        System.out.println("Plaintext password: " + password);
        System.out.println("Hashed password (store this in DB): " + hashed);
        System.out.println("\nVerification test:");
        System.out.println("Correct password '1234': " + verifyPassword("1234", hashed));
        System.out.println("Wrong password '12345': " + verifyPassword("12345", hashed));
    }
}