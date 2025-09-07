package dev.ayush.authzServiceSimulator.persistence;

import dev.ayush.authzServiceSimulator.model.User;

import java.io.*;
import java.time.Instant;
import java.util.*;

/**
 * File-based persistence for users only (tokens not persisted).
 * Format (CSV): id,email,passwordHash,salt,role1;role2
 */
public class FileService {

    /**
     * Ensure data dir and file exist (create if missing).
     */
//    public void ensureDataFiles(String usersPath) {
//
//    }

    /**
     * Save users to file (overwrite).
     */
    public void saveUsers(String usersPath, List<User> users) {
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(usersPath))) {
            for (User user : users) {
                bufferedWriter.write(user.getId() + "," + user.getEmail() + "," + user.getPasswordHash() + "," + user.getSalt() + ",");
                for (String role : user.getRoles()) {
                    bufferedWriter.write(role + ";");
                }
                bufferedWriter.newLine();
            }
            System.out.println("Users saved to " + usersPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Load users from file. @return list of users
     */
    public List<User> loadUsers(String usersPath) {
        List<User> users = new ArrayList<>();

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(usersPath))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                String[] splits = line.split(",");
                User user = getUser(splits);
                users.add(user);
                System.out.println("Loaded: " + user);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return Collections.unmodifiableList(users);
    }

    private static User getUser(String[] splits) {
        int id = Integer.parseInt(splits[0]);                 // unique user id (// ensure uniqueness)
        String email = splits[1];           // unique email (// validate format)
        String passwordHash = splits[2];    // hashed password (// never store plain)
        String salt = splits[3];            // per-user salt

        String[] rolesSplit = splits[4].split(";");
        Set<String> roles = new HashSet<>();
        for (String role : rolesSplit) {
            roles.add(role);
        }
        User user = new User(id, email, passwordHash, salt, roles);
        return user;
    }
}