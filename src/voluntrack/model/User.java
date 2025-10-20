package voluntrack.model;

public class User {
    private final int id;
    private final String fullName;
    private final String username;
    private final String email;
    private final String passwordHash;
    private final String role; // user or admin

    public User(int id, String fullName, String username, String email, String passwordHash, String role) {
        this.id = id;
        this.fullName = fullName;
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
    }

    public int getId() { return id; }
    public String getFullName() { return fullName; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
    public String getRole() { return role; }
}