package com.example.bankingapp;

import org.junit.Test;
import static org.junit.Assert.*;
import com.example.bankingapp.util.HashUtils;

public class PasswordEncryptionTest {

    @Test
    public void testSha256Encryption() {
        String input = "1234";
        // Erwarteter SHA-256 Hash für "1234"
        String expectedHash = "03ac674216f3e15c761ee1a5e255f067953623c8b388b4459e13f978d7c846f4";
        
        String actualHash = HashUtils.sha256(input);
        
        assertEquals("Der Passwort-Hash stimmt nicht überein!", expectedHash, actualHash);
    }

    @Test
    public void testEmptyStringEncryption() {
        String input = "";
        // Erwarteter SHA-256 Hash für einen leeren String
        String expectedHash = "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855";
        
        String actualHash = HashUtils.sha256(input);
        
        assertEquals(expectedHash, actualHash);
    }
}
