/**
 
 * PROG5121 - Part 3
 * Purpose   : This handles message creation, validation,
 *             sending, storing, hashing, and the
 *             five Part-3 arrays with all display/
 *             search/delete/report features.
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

    //  Instance fields
    private String messageID;
    private int    numMessagesSent;
    private String recipient;
    private String messageText;
    private String messageHash;

    private static final int MESSAGE_ID_LENGTH = 10;

    
    // PART 3 — FIVE ARRAYS (static, shared)
    
    private static ArrayList<String> sentMessagesArray      = new ArrayList<>();
    private static ArrayList<String> disregardedMessages    = new ArrayList<>();
    private static ArrayList<String> storedMessagesArray    = new ArrayList<>(); 
// loaded from JSON
    private static ArrayList<String> messageHashArray       = new ArrayList<>();
    private static ArrayList<String> messageIDArray         = new ArrayList<>();

    // Keep full Message objects for stored messages so we can
    // display/search/delete their fields easily.
    private static ArrayList<Message> storedMessageObjects  = new ArrayList<>();

    // Running total
    private static int totalMessagesSent = 0;


    // Constructor
    public Message(String recipient, String messageText) {
        this.recipient       = recipient;
        this.messageText     = messageText;
        this.messageID       = generateMessageID();
        this.numMessagesSent = totalMessagesSent + 1;
    }

    // Package-level constructor used when rebuilding objects from JSON
    Message(String messageID, String messageHash,
            String recipient, String messageText) {
        this.messageID    = messageID;
        this.messageHash  = messageHash;
        this.recipient    = recipient;
        this.messageText  = messageText;
        this.numMessagesSent = 0; // not relevant for stored-only objects
    }


    
    // VALIDATION METHODS  (Specifically unchanged from Part 2)
    

    public boolean checkMessageID() {
        return messageID != null && messageID.length() <= MESSAGE_ID_LENGTH;
    }

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


    
    // HASH METHOD  (Specifically unchanged from Part 2)
    

    public String createMessageHash() {
        String idPrefix  = messageID.substring(0, 2);
        String msgNumber = String.valueOf(numMessagesSent - 1);

        String[] words     = messageText.trim().split("\\s+");
        String   firstWord = words[0];
        String   lastWord  = words[words.length - 1];
        lastWord = lastWord.replaceAll("[^a-zA-Z0-9]", "");

        this.messageHash = (idPrefix + ":" + msgNumber + ":"
                          + firstWord + lastWord).toUpperCase();
        return messageHash;
    }


   
    // SEND / DISREGARD / STORE  (updated for Part 3)
    

    public String sentMessage(String choice) {
        switch (choice.trim()) {

            case "1": // Send
                createMessageHash();
                totalMessagesSent++;
                numMessagesSent = totalMessagesSent;
                sentMessagesArray.add(messageText);
                messageHashArray.add(messageHash);
                messageIDArray.add(messageID);
                return "Message successfully sent";

            case "2": // Disregard
                disregardedMessages.add(messageText);
                return "Press 0 to delete the message";

            case "3": // Store
                storeMessage();                       // writes to JSON
                storedMessagesArray.add(messageText);
                messageHashArray.add(messageHash);
                messageIDArray.add(messageID);
                storedMessageObjects.add(this);       // keep full object
                return "Message successfully stored";

            default:
                return "Invalid choice. Message was not processed.";
        }
    }


    
    // PART 3 — STORED MESSAGES MENU FEATURES
    

    /**
     * 2a. Display sender (ID) and recipient of ALL stored messages.
     */
    public static String displayStoredSendersAndRecipients() {
        loadStoredMessagesFromJSON();
        if (storedMessageObjects.isEmpty()) {
            return "No stored messages found.";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("\n===== Stored Messages — Sender & Recipient =====\n");
        for (Message m : storedMessageObjects) {
            sb.append("Message ID : ").append(m.messageID).append("\n");
            sb.append("Recipient  : ").append(m.recipient).append("\n");
            sb.append("-\n");
        }
        return sb.toString();
    }

    /**
     * 2b. Display the longest stored message.
     */
    public static String displayLongestStoredMessage() {
        loadStoredMessagesFromJSON();
        if (storedMessageObjects.isEmpty()) {
            return "No stored messages found.";
        }
        Message longest = storedMessageObjects.get(0);
        for (Message m : storedMessageObjects) {
            if (m.messageText.length() > longest.messageText.length()) {
                longest = m;
            }
        }
        return "\n= Longest Stored Message =\n"
             + "Recipient : " + longest.recipient   + "\n"
             + "Message   : " + longest.messageText + "\n";
    }

    /**
     * 2c. Search by message ID — return recipient and message.
     */
    public static String searchByMessageID(String searchID) {
        loadStoredMessagesFromJSON();
        // Search stored objects first
        for (Message m : storedMessageObjects) {
            if (m.messageID.equals(searchID)) {
                return "\n===== Message Found =====\n"
                     + "Recipient : " + m.recipient   + "\n"
                     + "Message   : " + m.messageText + "\n";
            }
        }
        // Also search messageIDArray (sent messages)
        for (int i = 0; i < messageIDArray.size(); i++) {
            if (messageIDArray.get(i).equals(searchID)) {
                return "\n===== Message Found =====\n"
                     + "Message ID : " + searchID + "\n"
                     + "Message    : " + sentMessagesArray.get(
                           Math.min(i, sentMessagesArray.size() - 1)) + "\n";
            }
        }
        return "Message ID \"" + searchID + "\" not found.";
    }

    /**
     * 2d. Search all stored messages for a particular recipient.
     */
    public static String searchByRecipient(String searchRecipient) {
        loadStoredMessagesFromJSON();
        StringBuilder sb = new StringBuilder();
        sb.append("\n===== Messages for Recipient: ")
          .append(searchRecipient).append(" =====\n");
        boolean found = false;
        for (Message m : storedMessageObjects) {
            if (m.recipient.equals(searchRecipient)) {
                sb.append("Message : ").append(m.messageText).append("\n");
                sb.append("-\n");
                found = true;
            }
        }
        if (!found) {
            return "No stored messages found for recipient \""
                 + searchRecipient + "\".";
        }
        return sb.toString();
    }

    /**
     * 2e. Delete a stored message using its hash.
     *     Removes from storedMessageObjects AND rewrites messages.json.
     */
    public static String deleteByMessageHash(String hash) {
        loadStoredMessagesFromJSON();
        Message toDelete = null;
        for (Message m : storedMessageObjects) {
            if (m.messageHash != null &&
                m.messageHash.equalsIgnoreCase(hash)) {
                toDelete = m;
                break;
            }
        }
        if (toDelete == null) {
            return "No message found with hash \"" + hash + "\".";
        }

        String deletedText = toDelete.messageText;
        storedMessageObjects.remove(toDelete);
        storedMessagesArray.remove(toDelete.messageText);
        messageHashArray.remove(toDelete.messageHash);
        messageIDArray.remove(toDelete.messageID);

        // Rewrite messages.json without the deleted entry
        rewriteJSONFile();

        return "Message: \"" + deletedText + "\" successfully deleted.";
    }

    /**
     * 2f. Display a full report of all stored messages
     *     (Message Hash, Recipient, Message).
     */
    public static String displayFullReport() {
        loadStoredMessagesFromJSON();
        if (storedMessageObjects.isEmpty()) {
            return "No stored messages to report.";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("\n=stored Messages Report =\n");
        for (Message m : storedMessageObjects) {
            sb.append("Message Hash : ").append(m.messageHash).append("\n");
            sb.append("Recipient    : ").append(m.recipient).append("\n");
            sb.append("Message      : ").append(m.messageText).append("\n");
            sb.append("--\n");
        }
        return sb.toString();
    }


    
    // DISPLAY / TOTAL METHODS  (Part 2 retained)
    

    public String printMessages() {
        if (sentMessagesArray.isEmpty()) {
            return "No messages have been sent yet.";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("\n== Sent Messages =\n");
        for (String msg : sentMessagesArray) {
            sb.append(msg).append("\n");
            sb.append("----\n");
        }
        return sb.toString();
    }

    public int returnTotalMessages() {
        return totalMessagesSent;
    }


    
    // JSON STORE METHOD  (Specifically unchanged from Part 2)
    

    public void storeMessage() {
        if (messageHash == null) {
            createMessageHash();
        }

        String newEntry = "  {\n"
            + "    \"messageID\": \""   + messageID   + "\",\n"
            + "    \"messageHash\": \"" + messageHash + "\",\n"
            + "    \"recipient\": \""   + recipient   + "\",\n"
            + "    \"message\": \""     + messageText + "\"\n"
            + "  }";

        File jsonFile = new File("messages.json");

        try {
            if (!jsonFile.exists() || jsonFile.length() == 0) {
                FileWriter writer = new FileWriter(jsonFile);
                writer.write("[\n");
                writer.write(newEntry + "\n");
                writer.write("]");
                writer.close();
            } else {
                BufferedReader reader = new BufferedReader(
                                            new FileReader(jsonFile));
                StringBuilder existing = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    existing.append(line).append("\n");
                }
                reader.close();

                String content = existing.toString().trim();
                if (content.endsWith("]")) {
                    content = content.substring(0,
                              content.length() - 1).trim();
                }
                if (!content.trim().equals("[")) {
                    content = content + ",\n";
                } else {
                    content = content + "\n";
                }

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


    
    // LOAD STORED MESSAGES FROM JSON  (Part 3)
    

    /**
     * Reads messages.json and populates storedMessageObjects.
     * Called before every stored-message operation so the
     * in-memory list always reflects what is on disk.
     * Skips objects that are already loaded (avoids duplicates).
     */
    public static void loadStoredMessagesFromJSON() {
        File jsonFile = new File("messages.json");
        if (!jsonFile.exists() || jsonFile.length() == 0) {
            return;
        }

        try {
            BufferedReader reader = new BufferedReader(
                                        new FileReader(jsonFile));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            reader.close();

            String content = sb.toString();

            // Simple hand-rolled JSON parser (no external libraries)
            // Split on "}" to get individual entries
            String[] entries = content.split("\\}");

            // Collect IDs already loaded to prevent duplicates
            ArrayList<String> loadedIDs = new ArrayList<>();
            for (Message m : storedMessageObjects) {
                loadedIDs.add(m.messageID);
            }

            for (String entry : entries) {
                String id   = extractJSONValue(entry, "messageID");
                String hash = extractJSONValue(entry, "messageHash");
                String rec  = extractJSONValue(entry, "recipient");
                String msg  = extractJSONValue(entry, "message");

                if (id != null && !id.isEmpty() && !loadedIDs.contains(id)) {
                    Message m = new Message(id, hash, rec, msg);
                    storedMessageObjects.add(m);
                    storedMessagesArray.add(msg);
                    if (hash != null) messageHashArray.add(hash);
                    messageIDArray.add(id);
                    loadedIDs.add(id);
                }
            }

        } catch (IOException e) {
            System.out.println("Error reading messages.json: " + e.getMessage());
        }
    }

    /**
     * Extracts the value of a key from a JSON fragment.
     * Example: extractJSONValue(entry, "recipient") -> "+27834557896"
     */
    private static String extractJSONValue(String jsonFragment, String key) {
        String search = "\"" + key + "\"";
        int keyIndex = jsonFragment.indexOf(search);
        if (keyIndex == -1) return null;

        int colonIndex = jsonFragment.indexOf(":", keyIndex);
        if (colonIndex == -1) return null;

        int firstQuote = jsonFragment.indexOf("\"", colonIndex);
        if (firstQuote == -1) return null;

        int secondQuote = jsonFragment.indexOf("\"", firstQuote + 1);
        if (secondQuote == -1) return null;

        return jsonFragment.substring(firstQuote + 1, secondQuote);
    }

    /**
     * Rewrites messages.json from the current storedMessageObjects list.
     * Used after a deletion.
     */
    private static void rewriteJSONFile() {
        File jsonFile = new File("messages.json");
        try {
            FileWriter writer = new FileWriter(jsonFile);
            writer.write("[\n");
            for (int i = 0; i < storedMessageObjects.size(); i++) {
                Message m = storedMessageObjects.get(i);
                if (m.messageHash == null) m.createMessageHash();
                String entry = "  {\n"
                    + "    \"messageID\": \""   + m.messageID   + "\",\n"
                    + "    \"messageHash\": \"" + m.messageHash + "\",\n"
                    + "    \"recipient\": \""   + m.recipient   + "\",\n"
                    + "    \"message\": \""     + m.messageText + "\"\n"
                    + "  }";
                writer.write(entry);
                if (i < storedMessageObjects.size() - 1) {
                    writer.write(",\n");
                } else {
                    writer.write("\n");
                }
            }
            writer.write("]");
            writer.close();
        } catch (IOException e) {
            System.out.println("Error rewriting messages.json: " + e.getMessage());
        }
    }


    
    // PRIVATE HELPERS
    

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


    
    // GETTERS
    
    public String getMessageID()              { return messageID;              }
    public String getMessageHash()            { return messageHash;            }
    public String getRecipient()              { return recipient;              }
    public String getMessageText()            { return messageText;            }
    public int    getNumMessagesSent()        { return numMessagesSent;        }

    public static ArrayList<String> getSentMessagesArray()   { return sentMessagesArray;   }
    public static ArrayList<String> getDisregardedMessages() { return disregardedMessages; }
    public static ArrayList<String> getStoredMessagesArray() { return storedMessagesArray; }
    public static ArrayList<String> getMessageHashArray()    { return messageHashArray;    }
    public static ArrayList<String> getMessageIDArray()      { return messageIDArray;      }
}
