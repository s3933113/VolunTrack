package voluntrack.service;

import voluntrack.repository.UserRepository;
import voluntrack.util.PasswordHasher;

/**
 * Handles signup, login, and password updates.
 */
public class AuthService {

    private final UserRepository repo = new UserRepository();

    // สมัครผู้ใช้ใหม่
    public String signup(String fullName, String username, String email, String password) {
        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            return "Username and password cannot be empty.";
        }
        if (repo.usernameExists(username)) {
            return "Username already exists.";
        }
        if (!isPasswordStrong(password)) {
            return "Password must be at least 8 characters and contain uppercase, number, and special character.";
        }
        String hash = PasswordHasher.hash(password);
        boolean ok = repo.insertUser(fullName, username, email, hash, "user");
        return ok ? "SUCCESS" : "Failed to register user.";
    }

    // ล็อกอิน
    public String login(String username, String password) {
        String hash = repo.findPasswordHash(username);
        if (hash == null) return "User not found.";
        if (!PasswordHasher.verify(password, hash)) return "Incorrect password.";
        return repo.findRole(username); // return role (user/admin)
    }

    // เปลี่ยนรหัสผ่าน
    public String changePassword(String username, String oldPassword, String newPassword, String confirm) {
        if (username == null || username.isBlank()) return "Invalid user";
        if (oldPassword == null || newPassword == null || confirm == null) return "Fill all fields";
        if (!newPassword.equals(confirm)) return "Passwords do not match";

        var userRepo = new voluntrack.repository.UserRepository();
        String currentHash = userRepo.findPasswordHash(username);
        if (currentHash == null) return "User not found";

        if (!voluntrack.util.PasswordHasher.verify(oldPassword, currentHash)) {
            return "Old password is incorrect";
        }

// ใช้ policy ที่คุณมีอยู่แล้ว
        if (!isPasswordStrong(newPassword)) return "Password too weak";

        String newHash = voluntrack.util.PasswordHasher.hash(newPassword);
        boolean ok = userRepo.updatePasswordHash(username, newHash);
        return ok ? "SUCCESS" : "Update failed";


    }

    // ตรวจความแข็งแรงของรหัสผ่าน
    private boolean isPasswordStrong(String password) {
        if (password.length() < 8) return false;
        boolean hasUpper = password.chars().anyMatch(Character::isUpperCase);
        boolean hasDigit = password.chars().anyMatch(Character::isDigit);
        boolean hasSpecial = password.matches(".*[^a-zA-Z0-9].*");
        return hasUpper && hasDigit && hasSpecial;
    }
}