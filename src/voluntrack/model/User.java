package voluntrack.model;

public class User {
    private String fullName;
    private String username;
    private String email;
    private String password;

    public User(String fullName, String username, String email, String password) {
        this.fullName = fullName;
        this.username = username;
        this.email = email;
        this.password = password;
    }

    // Getters
    public String getFullName() { return fullName; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }

    // Setters
    public void setPassword(String password) { this.password = password; }
}
