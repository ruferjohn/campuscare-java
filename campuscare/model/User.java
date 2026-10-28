
package campuscare.model;

public abstract class User {

    private int userId;
    private String fullName;
    private String username;
    private String email;

    public User(int userId, String fullName,
                String username, String email) {

        if (userId < 0) {
            throw new IllegalArgumentException("User ID cannot be negative.");
        }

        this.userId = userId;
        setFullName(fullName);
        setUsername(username);
        setEmail(email);
    }

    public int getUserId() {
        return userId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        if (fullName == null || fullName.isBlank()) {
            throw new IllegalArgumentException("Name is required.");
        }

        this.fullName = fullName.trim();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username is required.");
        }

        this.username = username.trim();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email is required.");
        }

        String cleanedEmail = email.trim();

        if (!cleanedEmail.matches(
                "^[\\w.-]+@[\\w.-]+\\.[A-Za-z]{2,}$")) {
            throw new IllegalArgumentException("Invalid email.");
        }

        this.email = cleanedEmail;
    }

    public abstract String getRole();

    public abstract String getDashboardMessage();
}

