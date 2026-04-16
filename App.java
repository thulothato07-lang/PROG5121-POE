/**
 * ================================================
 * PROG5121 - Part 1
 * Author    : Your Full Name
 * Student No: Your Student Number
 * Date      : April 2026
 * Purpose   : Main entry point for the application
 * ================================================
 */
import java.util.Scanner;

public class App {

    public static void main(String[] args) {

        Scanner input = new Scanner(System.in);

        System.out.println("========================================");
        System.out.println("   Welcome to the Registration System   ");
        System.out.println("========================================\n");

        // ── Collect user details ──────────────────
        System.out.print("Enter your first name    : ");
        String firstName = input.nextLine();

        System.out.print("Enter your last name     : ");
        String lastName = input.nextLine();

        System.out.print("Choose a username        : ");
        String username = input.nextLine();

        System.out.print("Choose a password        : ");
        String password = input.nextLine();

        System.out.print("Enter your cell number   : ");
        String cellNumber = input.nextLine();

        // ── Attempt registration ──────────────────
        Login newUser = new Login(firstName, lastName,
                                  username, password,
                                  cellNumber);

        String registrationResult = newUser.registerUser();

        System.out.println("\n--- Registration Status ---");
        System.out.println(registrationResult);

        // ── Attempt login if registration passed ──
        boolean registrationPassed =
            registrationResult.contains("successfully captured");

        if (registrationPassed) {
            System.out.println("\n========================================");
            System.out.println("              Login                     ");
            System.out.println("========================================\n");

            System.out.print("Enter your username : ");
            String loginUsername = input.nextLine();

            System.out.print("Enter your password : ");
            String loginPassword = input.nextLine();

            String loginResult = newUser.returnLoginStatus(
                loginUsername, loginPassword
            );

            System.out.println("\n--- Login Status ---");
            System.out.println(loginResult);
        }

        input.close();
    }
}