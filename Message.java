/**
 * ================================================
 * PROG5121 - Part 2
 * Author    : Your Full Name
 * Student No: Your Student Number
 * Date      : May 2026
 * Purpose   : Handles message creation, validation,
 *             sending, storing, and hashing
 * ================================================
 */
import java.util.ArrayList;
import java.util.Random;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;

public class Message {

    // ── Fields ────────────────────────────────────
    private String messageID;
    private int    numMessagesSent;
    private String recipient;
    private String messageText;
    private String messageHash;

    // Shared across all Message objects (static)
    private static int               totalMessagesSent = 0;
    private static ArrayList<String> sentMessages      = new ArrayList<>();

    private static final int MESSAGE_ID_LENGTH = 10;

    // ── Constructor ───────────────────────────────
    public Message(String recipient, String messageText) {
        this.recipient       = recipient;
        this.messageText     = messageText;
        this.messageID       = generateMessageID();
        this.numMessagesSent = totalMessagesSent + 1;
    }


    // ══════════════════════════════════════════════
    // VALIDATION METHODS
    // ══════════════════════════════════════════════

    /*
     * Checks that the auto-generated message ID
     * is no more than 10 characters long.
     */
    public boolean checkMessageID() {
        return messageID != null && messageID.length() <= MESSAGE_ID_LENGTH;
    }

    /*
     * Validates the recipient cell number:
     * - No more than 10 characters
     * - Must start with '+' (international code)
     */
    public String checkRecipientCell() {
        if (recipient == null || recipient.isEmpty()) {
            return "Cell phone number is incorrectly formatted or does not "
                 + "contain an international code. Please correct the number "
                 + "and try again.";
        }

        boolean startsWithPlus = recipient.startsWith("+");
        boolean correctLength  = recipient.length() <= 10;

        if (startsWithPlus && correctLength) {
            return "Cell phone number successfully captured.";
        }

        return "Cell phone number is incorrectly formatted or does not "
             + "contain an international code. Please correct the number "
             + "and try again.";
    }


    // ══════════════════════════════════════════════
    // HASH METHOD
    // ══════════════════════════════════════════════

    /*
     * Auto-generates the Message Hash in the format:
     *   FirstTwoOfID:MessageNumber:FirstWordLastWord
     * All in uppercase.
     * Example: 00:0:HITONIGHT
     */
    public String createMessageHash() {
        String idPrefix  = messageID.substring(0, 2);
        String msgNumber = String.valueOf(numMessagesSent - 1);

        String[] words     = messageText.trim().split("\\s+");
        String   firstWord = words[0];
        String   lastWord  = words[words.length - 1];

        // Strip punctuation from last word
        lastWord = lastWord.replaceAll("[^a-zA-Z0-9]", "");

        this.messageHash = (idPrefix + ":" + msgNumber + ":"
                          + firstWord + lastWord).toUpperCase();
        return messageHash;
    }


    // ══════════════════════════════════════════════
    // SEND / DISREGARD / STORE
    // ══════════════════════════════════════════════

    /*
     * Processes the user's action choice (passed in from App):
     *   "1" - Send Message
     *   "2" - Disregard Message
     *   "3" - Store Message to send later
     */
    public String sentMessage(String choice) {
        switch (choice.trim()) {
            case "1":
                createMessageHash();
                totalMessagesSent++;
                numMessagesSent = totalMessagesSent;
                sentMessages.add(buildMessageDisplay());
                return "Message successfully sent";

            case "2":
                return "Press 0 to delete the message";

            case "3":
                storeMessage();
                return "Message successfully stored";

            default:
                return "Invalid choice. Message was not processed.";
        }
    }


    // ══════════════════════════════════════════════
    // DISPLAY / TOTAL METHODS
    // ══════════════════════════════════════════════

    /*
     * Returns all messages sent during this session
     * in the required order:
     *   Message ID, Message Hash, Recipient, Message
     */
    public String printMessages() {
        if (sentMessages.isEmpty()) {
            return "No messages have been sent yet.";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("\n========== Sent Messages ==========\n");
        for (String msg : sentMessages) {
            sb.append(msg).append("\n");
            sb.append("-----------------------------------\n");
        }
        return sb.toString();
    }

    /*
     * Returns the total number of messages sent
     * during the current session.
     */
    public int returnTotalMessages() {
        return totalMessagesSent;
    }


    // ══════════════════════════════════════════════
    // JSON STORE METHOD
    // ══════════════════════════════════════════════

    /*
     * Stores the message into messages.json as a
     * proper JSON array using only standard Java I/O.
     *
     * Result in messages.json:
     * [
     *   {
     *     "messageID": "1234567890",
     *     "messageHash": "12:0:HITONIGHT",
     *     "recipient": "+27718693002",
     *     "message": "Hi Mike, can you join us for dinner tonight?"
     *   }
     * ]
     */
    public void storeMessage() {
        // Ensure hash is generated before storing
        if (messageHash == null) {
            createMessageHash();
        }

        // Build the new JSON object for this message
        String newEntry = "  {\n"
            + "    \"messageID\": \""   + messageID   + "\",\n"
            + "    \"messageHash\": \"" + messageHash + "\",\n"
            + "    \"recipient\": \""   + recipient   + "\",\n"
            + "    \"message\": \""     + messageText + "\"\n"
            + "  }";

        File jsonFile = new File("messages.json");

        try {
            if (!jsonFile.exists() || jsonFile.length() == 0) {
                // File does not exist — create a fresh JSON array
                FileWriter writer = new FileWriter(jsonFile);
                writer.write("[\n");
                writer.write(newEntry + "\n");
                writer.write("]");
                writer.close();

            } else {
                // File exists — read it, strip closing ],
                // append new entry, re-close the array
                BufferedReader reader = new BufferedReader(
                                            new FileReader(jsonFile));
                StringBuilder existing = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    existing.append(line).append("\n");
                }
                reader.close();

                // Remove closing ] and trailing whitespace
                String content = existing.toString().trim();
                if (content.endsWith("]")) {
                    content = content.substring(0,
                              content.length() - 1).trim();
                }

                // Add comma separator if array already has entries
                if (!content.trim().equals("[")) {
                    content = content + ",\n";
                } else {
                    content = content + "\n";
                }

                // Write back with new entry and closing ]
                FileWriter writer = new FileWriter(jsonFile);
                writer.write(content);
                writer.write(newEntry + "\n");
                writer.write("]");
                writer.close();
            }

            System.out.println("Message successfully stored to messages.json");

        } catch (IOException e) {
            System.out.println("Error storing message: " + e.getMessage());
        }
    }


    // ══════════════════════════════════════════════
    // PRIVATE HELPERS
    // ══════════════════════════════════════════════

    private String generateMessageID() {
        Random        random = new Random();
        StringBuilder sb     = new StringBuilder();
        for (int i = 0; i < MESSAGE_ID_LENGTH; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }

    private String buildMessageDisplay() {
        return "Message ID   : " + messageID   + "\n"
             + "Message Hash : " + messageHash + "\n"
             + "Recipient    : " + recipient   + "\n"
             + "Message      : " + messageText;
    }


    // ══════════════════════════════════════════════
    // GETTERS
    // ══════════════════════════════════════════════
    public String getMessageID()       { return messageID;       }
    public String getMessageHash()     { return messageHash;     }
    public String getRecipient()       { return recipient;       }
    public String getMessageText()     { return messageText;     }
    public int    getNumMessagesSent() { return numMessagesSent; }
}