package dev.ayush.authzServiceSimulator.controller;

import dev.ayush.authzServiceSimulator.model.User;
import dev.ayush.authzServiceSimulator.persistence.FileService;
import dev.ayush.authzServiceSimulator.service.AuthzService;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * CLI "controller" mapping commands to service calls.
 * Commands:
 * REGISTER <id> <email> <password> [rolesCsv]
 * LOGIN <email> <password>
 * ME <token>
 * CHANGE_PASSWORD <email> <old> <new>
 * LOGOUT <token>
 * LIST_USERS
 * DELETE_USER <token> <id>   (ADMIN only)
 * SAVE
 * LOAD
 * EXIT
 */
public class AuthzController {
    private AuthzService authzService; // dependency

    /**
     * Inject AuthService.
     */
    public AuthzController(AuthzService authzService) {
        this.authzService = authzService;
    }

    /**
     * Parse and execute one command line.
     */
    public void handle(String line) {
        if (line.isEmpty()) return;
        String[] splits = line.split(" ");
        String command = splits[0];


        // REGISTER <id> <email> <password> role1;role2
        if (command.equals("REGISTER")) {
            int id = Integer.parseInt(splits[1]);
            String email = splits[2];
            String rawPassword = splits[3];
            Set<String> set = new HashSet<>(List.of(splits[4].split(";")));
            User user = authzService.register(id, email, rawPassword, set);
            System.out.println("User created: " + user);
        }

        // LOGIN <email> <password>
        if (command.equals("LOGIN")) {
            String email = splits[1];
            String password = splits[2];
            String token = authzService.login(email, password);
            System.out.println("Token: " + token);
        }

//        ME <token>
        if (command.equals("ME")) {
            String token = splits[1];
            User user = authzService.me(token);
            System.out.println("Current User: " + user);
            for (String role : user.getRoles()) {
                System.out.print(role + ";");
            }
        }

        // CHANGE_PASSWORD <email> <old> <new>
        if (command.equals("CHANGE_PASSWORD")) {
            String email = splits[1];
            String oldPassword = splits[2];
            String newPassword = splits[3];

            boolean passwordChanged = authzService.changePassword(email, oldPassword, newPassword);
            if (passwordChanged) {
                System.out.println("Password changed: OK");
            }
        }

        // LOGOUT <token>
        if (command.equals("LOGOUT")) {
            String token = splits[1];
            if (authzService.logout(token)) {
                System.out.println("Logged out: OK");
            } else {
                System.out.println("Log out failed!");
            }
        }

        //  LIST_USERS
        if (command.equals("LIST_USERS")) {
            List<User> users = authzService.getAllUsers();
            System.out.println("Users: ");
            for (User user : users) {
                System.out.println(user);
            }
        }

//        DELETE_USER <token> <id>   (ADMIN only)
        if (command.equals("DELETE_USER")) {
            String token = splits[1];
            int id = Integer.parseInt(splits[2]);
            if (authzService.deleteUser(token, id)) {
                System.out.println("User deleted: id=" + id);
            } else {
                System.out.println("User deletion failed. id=" + id);
            }
        }


        // SAVE
        if (command.equals("SAVE")) {
            List<User> users = authzService.getAllUsers();
            FileService fileService = new FileService();
            fileService.saveUsers("data/users.txt", users);
        }

        // LOAD
        if (command.equals("LOAD")) {

            FileService fileService = new FileService();
            List<User> users = fileService.loadUsers("data/users.txt");
            authzService.setUsers(users);
            System.out.println("Users loaded from data/users.txt");
        }

        // EXIT
        if (command.equals("EXIT")) {
            System.out.println("Goodbye");
            System.exit(0);
        }
    }

    /**
     * Print minimal help text with examples.
     */
    public void help() {
        System.out.println("""
                REGISTER <id> <email> <password> role1;role2
                LOGIN <email> <password>
                ME <token>
                CHANGE_PASSWORD <email> <old> <new>
                LOGOUT <token>
                LIST_USERS
                DELETE_USER <token> <id>   (ADMIN only)
                SAVE
                LOAD
                HELP
                EXIT
                """);
    }
}