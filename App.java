/**
 * ================================================
 * PROG5121 - Part 3
 * Purpose   : Main entry point.This extends Part 2 with
 *             a "Stored Messages" menu and all six
 *             sub-options required by the brief.
 * ================================================
 */
import java.util.Scanner;

public class App {

    private static final Scanner input = new Scanner(System.in);

    public static void main(String[] args) {

        
        // PART 1 — Registration
        
        System.out.println(" ");
        System.out.println("   Welcome to the Registration System   ");
        System.out.println("=\n");

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
            System.out.println("\n=");
            System.out.println("              Login                     ");
            System.out.println("=\n");

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

        
        // PART 2 & 3 — QuickChat (only if login succeeded)
       
        if (loginPassed) {
            // Load any messages already stored on disk before showing the menu
            Message.loadStoredMessagesFromJSON();
            runQuickChat();
        }

        input.close();
    }


    
    // QUICKCHAT MAIN MENU  (updated for Part 3)
    

    private static void runQuickChat() {

        System.out.println("\n=");
        System.out.println("       Welcome to QuickChat.            ");
        System.out.println("=\n");

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
            System.out.println("\n-");
            System.out.println("            QuickChat Menu              ");
            System.out.println("-");
            System.out.println("  1) Send Messages");
            System.out.println("  2) Show Recently Sent Messages");
            System.out.println("  3) Stored Messages");   // NEW — Part 3
            System.out.println("  4) Quit");
            System.out.println("-");
            System.out.print("Enter your choice: ");

            String menuChoice = input.nextLine().trim();

            switch (menuChoice) {
                case "1":
                    sendMessages(numMessages);
                    break;

                case "2":
                    showRecentlySentMessages();
                    break;

                case "3":
                    storedMessagesMenu();     // NEW — Part 3
                    break;

                case "4":
                    running = false;
                    System.out.println(
                        "\nThank you for using QuickChat. Goodbye!");
                    break;

                default:
                    System.out.println(
                        "Invalid option. Please enter 1, 2, 3, or 4.");
            }
        }
    }


    
    // SEND MESSAGES FLOW  (Specifically unchanged from Part 2)
    

    private static void sendMessages(int numMessages) {

        Message lastMessage = null;

        for (int i = 0; i < numMessages; i++) {

            System.out.println("\n======== Message " + (i + 1)
                             + " of " + numMessages + " ========");

            //  Recipient validation 
            String  recipient      = "";
            boolean validRecipient = false;

            while (!validRecipient) {
                System.out.print("Enter recipient cell number: ");
                recipient = input.nextLine().trim();

                Message tempMsg   = new Message(recipient, "placeholder");
                String  cellCheck = tempMsg.checkRecipientCell();
                System.out.println(cellCheck);

                validRecipient = cellCheck.contains("successfully captured");
            }

            // Message text validation
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

            // Create message & validate ID 
            Message message = new Message(recipient, messageText);

            if (!message.checkMessageID()) {
                System.out.println(
                    "Error: Message ID invalid. Retrying this slot.");
                i--;
                continue;
            }

            // Show generated hash
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

            // Display full details if sent 
            if (sendResult.equals("Message successfully sent")) {
                System.out.println("\n--- Message Details ---");
                System.out.println("Message ID   : " + message.getMessageID());
                System.out.println("Message Hash : " + message.getMessageHash());
                System.out.println("Recipient    : " + message.getRecipient());
                System.out.println("Message      : " + message.getMessageText());
            }

            lastMessage = message;
        }

        if (lastMessage != null) {
            System.out.println(
                "\n=");
            System.out.println("Total messages sent: "
                             + lastMessage.returnTotalMessages());
            System.out.println(
                "=");
        }
    }


   
    // SHOW RECENTLY SENT MESSAGES  (The Part 2 retained)
    

    private static void showRecentlySentMessages() {
        if (Message.getSentMessagesArray().isEmpty()) {
            System.out.println("\nNo messages have been sent yet.");
            return;
        }
        System.out.println("\n========== Recently Sent Messages ==========");
        for (String msg : Message.getSentMessagesArray()) {
            System.out.println(msg);
            System.out.println("--");
        }
    }


   
    // STORED MESSAGES MENU  (NEW — Part 3)
    

    private static void storedMessagesMenu() {

        boolean back = false;

        while (!back) {
            System.out.println("\n-");
            System.out.println("        Stored Messages Menu            ");
            System.out.println("-");
            System.out.println("  a) Display sender & recipient of all stored messages");
            System.out.println("  b) Display the longest stored message");
            System.out.println("  c) Search for a message by ID");
            System.out.println("  d) Search all messages for a recipient");
            System.out.println("  e) Delete a message using its hash");
            System.out.println("  f) Display full report of all stored messages");
            System.out.println("  0) Back to main menu");
            System.out.println("-");
            System.out.print("Enter your choice: ");

            String choice = input.nextLine().trim().toLowerCase();

            switch (choice) {

                case "a":
                    System.out.println(
                        Message.displayStoredSendersAndRecipients());
                    break;

                case "b":
                    System.out.println(
                        Message.displayLongestStoredMessage());
                    break;

                case "c":
                    System.out.print("Enter the Message ID to search: ");
                    String searchID = input.nextLine().trim();
                    System.out.println(
                        Message.searchByMessageID(searchID));
                    break;

                case "d":
                    System.out.print("Enter the recipient number to search: ");
                    String searchRecipient = input.nextLine().trim();
                    System.out.println(
                        Message.searchByRecipient(searchRecipient));
                    break;

                case "e":
                    System.out.print("Enter the Message Hash to delete: ");
                    String deleteHash = input.nextLine().trim();
                    System.out.println(
                        Message.deleteByMessageHash(deleteHash));
                    break;

                case "f":
                    System.out.println(
                        Message.displayFullReport());
                    break;

                case "0":
                    back = true;
                    break;

                default:
                    System.out.println(
                        "Invalid option. Please enter a, b, c, d, e, f, or 0.");
            }
        }
    }
}
