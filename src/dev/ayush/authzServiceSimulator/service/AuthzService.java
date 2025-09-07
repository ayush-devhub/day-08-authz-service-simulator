package dev.ayush.authzServiceSimulator.service;

import dev.ayush.authzServiceSimulator.model.User;
import dev.ayush.authzServiceSimulator.security.PasswordHasher;
import dev.ayush.authzServiceSimulator.security.TokenStore;

import java.time.Duration;
import java.util.*;

/**
 * Core auth business logic: register, login, logout, me, change password, list users.
 */
public class AuthzService {
    private List<User> users;           // in-memory user store
    private PasswordHasher hasher;      // dependency: hashing
    private TokenStore tokenStore;      // dependency: sessions

    /**
     * Wire dependencies; initialize stores.
     */
    public AuthzService(PasswordHasher hasher, TokenStore tokenStore) {
        this.hasher = hasher;
        this.tokenStore = tokenStore;
        users = new ArrayList<>();
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    /**
     * Register new user; returns created user or throws on duplicate email.
     */
    public User register(int id, String email, String rawPassword, Set<String> roles) {

        for (User user : users) {
            if (user.getEmail().equalsIgnoreCase(email)) throw new RuntimeException("Email already registered.");
        }

        String salt = hasher.generateSalt();
        String hash = hasher.hash(rawPassword, salt);

        User user = new User(id, email, hash, salt, roles);
        users.add(user);
        return user;
    }

    /**
     * Login -> returns token string if credentials valid.
     */
    public String login(String email, String rawPassword) {
        User user = null;

        for (User u : users) {
            if (u.getEmail().equalsIgnoreCase(email)) user = u;
        }

        if (user == null) throw new RuntimeException("Email not registered.");

        Boolean logged = hasher.verify(rawPassword, user.getSalt(), user.getPasswordHash());
        if (logged) {
            return tokenStore.issue(user.getId());
        } else {
            System.out.println("Invalid password. Try again.");
            return null;
        }
    }

    /**
     * Logout -> revoke token; return success flag.
     */
    public boolean logout(String token) {
        return tokenStore.revoke(token);
    }

    /**
     * Resolve current user from token; may return null.
     */
    public User me(String token) {
        int userId = tokenStore.validate(token);
        for (User user : users) {
            if (user.getId() == userId) return user;
        }
        return null;
    }

    /**
     * Change password (requires email + old + new). @return success flag
     */
    public boolean changePassword(String email, String oldRaw, String newRaw) {
        User user = getUserByEmail(email);
        if (user != null) {
            if (hasher.verify(oldRaw, user.getSalt(), user.getPasswordHash())) {
                user.setPasswordHash(hasher.hash(newRaw, user.getSalt()));
                return true;
            }else{
                System.out.println("Incorrect password. Try again.");
                return false;
            }
        }else{
            System.out.println("Email not registered: " + email);
            return false;
        }
    }

    private boolean deleteUser(String token, int userId){
        // Authenticating client is an ADMIN
        User requester = me(token);

        if (requester == null) return false;

        if (!requester.getRoles().contains("ADMIN")){
            return false;
        }

        for (User user : users) {
            if (user.getId() == userId) users.remove(user);
            return true;
        }

        return false; 
    }

    private User getUserByEmail(String email) {
        for (User user : users) {
            if (user.getEmail().equalsIgnoreCase(email)) return user;
        }
        return null;
    }

    /**
     * @return unmodifiable users list (for listing).
     */
    public List<User> getAllUsers() {
        return Collections.unmodifiableList(users);
    }


}