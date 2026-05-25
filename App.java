/**
 * ================================================
 * PROG5121 - Part 1 & 2
 * Author    : Your Full Name
 * Student No: Your Student Number
 * Date      : May 2026
 * Purpose   : Main entry point — Registration, Login,
 *             and QuickChat messaging menu
 * ================================================
 */
import java.util.Scanner;

public class App {

    // ONE Scanner for the entire program
    private static final Scanner input = new Scanner(System.in);

    public static void main(String[] args) {

        
        // PART 1 — Registration
        
        System.out.println("========================================");
        System.out.println("   Welcome to the Registration System   ");
        System.out.println("========================================\n");

        System.out.print("Enter your first name: ");
        String firstName = input.nextLine();

        System.out.print("Enter your last name: ");
        String lastName = input.nextLine();

        System.out.print("Choose a username: ");
        String username = input.nextLine();

        System.out.print("Choose a password: ");
        String password = input.nextLine();

        System.out.print("Enter your cell number: ");
        String cellNumber = input.nextLine();

        Login newUser = new Login(firstName, lastName,
                                  username, password,
                                  cellNumber);

        String registrationResult = newUser.registerUser();
        System.out.println("\n--- Registration Status ---");
        System.out.println(registrationResult);

        boolean registrationPassed =
                registrationResult.contains("successfully captured");

        
        // PART 1 — Login
        
        boolean loginPassed = false;

        if (registrationPassed) {
            System.out.println("\n========================================");
            System.out.println("              Login                     ");
            System.out.println("========================================\n");

            System.out.print("Enter your username: ");
            String loginUsername = input.nextLine();

            System.out.print("Enter your password: ");
            String loginPassword = input.nextLine();

            String loginResult = newUser.returnLoginStatus(
                    loginUsername, loginPassword);

            System.out.println("\n--- Login Status ---");
            System.out.println(loginResult);

            loginPassed = loginResult.contains("great to see you");
        }

        
        // PART 2 — QuickChat (only if login succeeded)
        
        if (loginPassed) {
            runQuickChat();
        }

        input.close();
    }


  
    // QUICKCHAT MAIN MENU
    

    private static void runQuickChat() {

        System.out.println("\n   ");
        System.out.println("  Welcome to QuickChat.");
        System.out.println("\n   ");

        // Ask how many messages to send upfront
        int numMessages = 0;
        while (numMessages <= 0) {
            System.out.print("How many messages do you wish to send? ");
            String raw = input.nextLine().trim();
            try {
                numMessages = Integer.parseInt(raw);
                if (numMessages <= 0) {
                    System.out.println("Please enter a number greater than 0.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a whole number.");
            }
        }

        boolean running = true;

        while (running) {
            System.out.println("\n  ");
            System.out.println("            QuickChat Menu              ");
            System.out.println("  ");
            System.out.println("  1) Send Messages");
            System.out.println("  2) Show Recently Sent Messages");
            System.out.println("  3) Quit");
            System.out.println("  ");
            System.out.print("Enter your choice: ");

            String menuChoice = input.nextLine().trim();

            switch (menuChoice) {
                case "1":
                    sendMessages(numMessages);
                    break;

                case "2":
                    // Feature still in development
                    System.out.println("\nComing Soon.");
                    break;

                case "3":
                    running = false;
                    System.out.println(
                        "\nThank you for using QuickChat. Goodbye!");
                    break;

                default:
                    System.out.println(
                        "Invalid option. Please enter 1, 2, or 3.");
            }
        }
    }


     
    // SEND MESSAGES FLOW
     

    private static void sendMessages(int numMessages) {

        Message lastMessage = null;

        for (int i = 0; i < numMessages; i++) {

            System.out.println("\n Message " + (i + 1)
                             + " of " + numMessages + " ");

            //  Recipient validation 
            String recipient    = "      ";
            boolean validRecipient = false;

            while (!validRecipient) {
                System.out.print("Enter recipient cell number: ");
                recipient = input.nextLine().trim();

                Message tempMsg   = new Message(recipient, "placeholder");
                String  cellCheck = tempMsg.checkRecipientCell();
                System.out.println(cellCheck);

                validRecipient = cellCheck.contains("successfully captured");
            }

            //  Message text validation 
            String  messageText  = "";
            boolean validMessage = false;

            while (!validMessage) {
                System.out.print("Enter your message (max 250 characters): ");
                messageText = input.nextLine();

                if (messageText.length() > 250) {
                    int excess = messageText.length() - 250;
                    System.out.println(
                        "Please enter a message of less than 250 characters.");
                    System.out.println(
                        "Message exceeds 250 characters by " + excess
                        + "; please reduce the size.");
                } else {
                    System.out.println("Message ready to send.");
                    validMessage = true;
                }
            }

            //  Create message & validate ID 
            Message message = new Message(recipient, messageText);

            if (!message.checkMessageID()) {
                System.out.println(
                    "Error: Message ID invalid. Retrying this slot.");
                i--;
                continue;
            }

            //  Show generated hash 
            String hash = message.createMessageHash();
            System.out.println("Message Hash : " + hash);

            // Send / Disregard / Store 
            System.out.println("\nWhat would you like to do with this message?");
            System.out.println("  1) Send Message");
            System.out.println("  2) Disregard Message");
            System.out.println("  3) Store Message to send later");
            System.out.print("Enter your choice: ");
            String actionChoice = input.nextLine().trim();

            String sendResult = message.sentMessage(actionChoice);
            System.out.println(sendResult);

            //  Display full details if sent 
            if (sendResult.equals("Message successfully sent")) {
                System.out.println("\n--- Message Details ---");
                System.out.println("Message ID   : " + message.getMessageID());
                System.out.println("Message Hash : " + message.getMessageHash());
                System.out.println("Recipient    : " + message.getRecipient());
                System.out.println("Message      : " + message.getMessageText());
            }

            lastMessage = message;
        }

        // Display total messages sent after all have been processed
        if (lastMessage != null) {
            System.out.println(
                "\n  ");
            System.out.println("Total messages sent: "
                             + lastMessage.returnTotalMessages());
            System.out.println(
                "  ");
        }
    }
}
