package dev.ayush.authzServiceSimulator.security;

import java.time.*;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * In-memory token store with expiry (simulated sessions).
 */
public class TokenStore {
    private static class Session {
        int userId;                // owner user id
        Instant expiresAt;         // expiry instant
    }

    private Map<String, Session> tokens; // token -> session
    private Duration ttl;                // e.g., 30 minutes

    /**
     * Initialize map and default TTL.
     */
    public TokenStore(Duration ttl) {
        tokens = new HashMap<>();
        this.ttl = ttl;
    }

    /**
     * Issue a new token for a user id. @return token string
     */
    public String issue(int userId) {
        String token = UUID.randomUUID().toString() + Long.toHexString(ThreadLocalRandom.current().nextLong());

        Session session = new Session();
        session.userId = userId;
        session.expiresAt = Instant.now().plus(ttl);

        tokens.put(token, session);
        return token;
    }

    /**
     * Validate token -> returns userId or -1 if invalid/expired.
     */
    public int validate(String token) {
        Session session = tokens.get(token);
        if (session == null) return -1;

        if (Instant.now().isAfter(session.expiresAt)) {
            tokens.remove(token);
            return -1;

        }
        return session.userId;
    }

    /**
     * Revoke token. @return success flag
     */
    public boolean revoke(String token) {
        return tokens.remove(token) != null;
    }

    /**
     * Remove expired tokens proactively.
     */
    public void cleanupExpired() {
        Instant now = Instant.now();
        tokens.entrySet().removeIf(entry -> now.isAfter(entry.getValue().expiresAt));
    }
}