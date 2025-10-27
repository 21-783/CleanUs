package com.example.Cleanus.account;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

@Component
public class AccountHasher {
    @Value("${ledger.account.hash-salt:default-salt}")
    private String salt;

    public String hashAccountNumber (String raw) {
        try {
            var md = MessageDigest.getInstance("SHA-256");
            var src = (salt + ":" +raw.trim()).getBytes(StandardCharsets.UTF_8);
            return toHex(md.digest(src));
        } catch (Exception e) { throw new IllegalStateException(e); }
    }

    public String last4(String raw) {
        String d = raw.replaceAll("\\D", "");
        return d.length() <= 4 ? d : d.substring(d.length() - 4);
    }

    private static String toHex(byte[] b) {
        StringBuilder sb = new StringBuilder(b.length * 2);
        for (byte x : b) sb.append(String.format("%02x", x));
        return sb.toString();
    }
}
