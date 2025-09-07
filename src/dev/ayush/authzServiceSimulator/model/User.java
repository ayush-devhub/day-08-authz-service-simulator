package dev.ayush.authzServiceSimulator.model;

import java.util.*;

/**
 * Domain model for a registered user.
 */
public class User {
    private final int id;                 // unique user id (// ensure uniqueness)
    private final String email;           // unique email (// validate format)
    private String passwordHash;    // hashed password (// never store plain)
    private String salt;            // per-user salt
    private Set<String> roles;      // e.g., USER, ADMIN

    /** Constructor: initialize immutable/core fields. */
    public User(int id, String email, String passwordHash, String salt, Set<String> roles) {
        this.id = id;
        this.email = email;
        this.passwordHash = passwordHash;
        this.salt = salt;
        this.roles = roles;
    }

    public int getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getSalt() {
        return salt;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    /** @return unmodifiable view of roles */
    public Set<String> getRoles() {
        return Collections.unmodifiableSet(roles);
    }

    /** String view for logs (never include password/salt). */
    @Override public String toString() {
        return "id=" + getId() + ", email=" + getEmail();
    }
}