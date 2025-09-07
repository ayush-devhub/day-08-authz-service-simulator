package dev.ayush.authzServiceSimulator;

import dev.ayush.authzServiceSimulator.controller.AuthzController;
import dev.ayush.authzServiceSimulator.security.PasswordHasher;
import dev.ayush.authzServiceSimulator.security.TokenStore;
import dev.ayush.authzServiceSimulator.service.AuthzService;
import dev.ayush.authzServiceSimulator.persistence.FileService;

import java.time.Duration;
import java.util.Scanner;

/**
 * Console entry point for Auth Service Simulator.
 */
public class AuthzSimulatorApp {
    public static void main(String[] args) {
        // entry point: wire dependencies, show prompt loop
        FileService fileService = new FileService();
        PasswordHasher hasher = new PasswordHasher();
        TokenStore tokenStore = new TokenStore(Duration.ofMinutes(30));

        AuthzService authService = new AuthzService(hasher, tokenStore);

        AuthzController controller = new AuthzController(authService);

        runLoop(controller);
    }

    /**
     * Show prompt & route lines to controller.
     */
    private static void runLoop(AuthzController controller) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("""
                    ===== Auth Service Simulator =====
                    Type 'HELP' for commands. Type 'EXIT' to quit.
                    """);
        while (true) {
            System.out.print("> ");
            String line = scanner.nextLine().trim();
            controller.handle(line);
        }
    }
}