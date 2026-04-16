/**
 * ================================================
 * PROG5121 - Part 1
 * Author    : Your Full Name
 * Student No: Your Student Number
 * Date      : April 2026
 * Purpose   : Handles user registration and login
 * ================================================
 */
public class Login {

    // ── User Details ──────────────────────────────
    private String firstName;
    private String lastName;
    private String username;
    private String password;
    private String cellNumber;

    // ── Constructor ───────────────────────────────
    public Login(String firstName, String lastName,
                 String username, String password,
                 String cellNumber) {
        this.firstName  = firstName;
        this.lastName   = lastName;
        this.username   = username;
        this.password   = password;
        this.cellNumber = cellNumber;
    }

    // ═════════════════════════════════════════════
    // VALIDATION METHODS
    // ═════════════════════════════════════════════

    /*
     * Validates the username:
     * - Must contain an underscore
     * - Must be 5 characters or less
     */
    public boolean checkUserName() {
        boolean hasUnderscore = username.contains("_");
        boolean correctLength = username.length() <= 5;
        return hasUnderscore && correctLength;
    }

    /*
     * Validates the password:
     * - Minimum 8 characters
     * - Must have a capital letter
     * - Must have a number
     * - Must have a special character
     */
    public boolean checkPasswordComplexity() {
        if (password.length() < 8) {
            return false;
        }

        boolean hasCapital = false;
        boolean hasNumber  = false;
        boolean hasSpecial = false;

        for (char character : password.toCharArray()) {
            if (Character.isUpperCase(character)) {
                hasCapital = true;
            } else if (Character.isDigit(character)) {
                hasNumber = true;
            } else if (!Character.isLetterOrDigit(character)) {
                hasSpecial = true;
            }
        }
        return hasCapital && hasNumber && hasSpecial;
    }

    /*
     * Validates the cell number using regex:
     * - Must start with + and country code
     * - Must be correct length
     * Reference: docs.oracle.com/javase/8/docs/api/java/util/regex/Pattern.html
     */
    public boolean checkCellPhoneNumber() {
        String pattern = "^\\+\\d{1,3}\\d{7,10}$";
        return cellNumber.matches(pattern);
    }

    // ═════════════════════════════════════════════
    // REGISTRATION METHOD
    // ═════════════════════════════════════════════

    /*
     * Registers the user if all validations pass.
     * Checks username first, then password, then cell.
     * Returns appropriate message for each outcome.
     */
    public String registerUser() {
        boolean validUsername = checkUserName();
        boolean validPassword = checkPasswordComplexity();
        boolean validCell     = checkCellPhoneNumber();

        if (!validUsername) {
            return "Username is not correctly formatted; please ensure that your "
                 + "username contains an underscore and is no more than "
                 + "five characters in length.";
        }

        if (!validPassword) {
            return "Password is not correctly formatted; please ensure that the "
                 + "password contains at least eight characters, a capital "
                 + "letter, a number, and a special character.";
        }

        if (!validCell) {
            return "Cell phone number incorrectly formatted or does not "
                 + "contain international code.";
        }

        return "Username successfully captured.\n"
             + "Password successfully captured.\n"
             + "Cell number successfully captured.";
    }

    // ═════════════════════════════════════════════
    // LOGIN METHODS
    // ═════════════════════════════════════════════

    /*
     * Checks if the entered credentials match
     * what was saved during registration.
     */
    public boolean loginUser(String inputUsername,
                             String inputPassword) {
        boolean usernameMatch = this.username.equals(inputUsername);
        boolean passwordMatch = this.password.equals(inputPassword);
        return usernameMatch && passwordMatch;
    }

    /*
     * Returns a welcome message if login succeeds,
     * or an error message if login fails.
     */
    public String returnLoginStatus(String inputUsername,
                                    String inputPassword) {
        boolean loginSuccess = loginUser(inputUsername, inputPassword);

        if (loginSuccess) {
            return "Welcome " + firstName + " " + lastName
                 + " it is great to see you.";
        }
        return "Username or password incorrect, please try again.";
    }

    // ═════════════════════════════════════════════
    // GETTERS
    // ═════════════════════════════════════════════
    public String fetchUsername()   { return username; }
    public String fetchPassword()   { return password; }
    public String fetchFirstName()  { return firstName; }
    public String fetchLastName()   { return lastName; }
}