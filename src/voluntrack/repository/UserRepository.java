package voluntrack.repository;

import voluntrack.db.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserRepository {

    private Connection conn() {
        return DatabaseManager.getInstance().getConnection();
    }

    public boolean usernameExists(String username) {
        String sql = "SELECT 1 FROM users WHERE username = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("usernameExists failed", e);
        }
    }

    public boolean insertUser(String fullName, String username, String email, String passwordHash, String role) {
        String sql = "INSERT INTO users(full_name, username, email, password_hash, role, created_at) VALUES(?,?,?,?,?,?)";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, fullName);
            ps.setString(2, username);
            ps.setString(3, email);
            ps.setString(4, passwordHash);
            ps.setString(5, role);
            ps.setString(6, voluntrack.util.TimeUtil.nowIso());
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            if (e.getMessage() != null && e.getMessage().toLowerCase().contains("unique")) {
                return false; // username already exists
            }
            throw new RuntimeException("insertUser failed", e);
        }
    }

    public boolean updatePasswordByUsername(String username, String newHash) {
        String sql = "UPDATE users SET password_hash = ? WHERE username = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, newHash);
            ps.setString(2, username);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new RuntimeException("updatePasswordByUsername failed", e);
        }
    }

    public String findPasswordHash(String username) {
        String sql = "SELECT password_hash FROM users WHERE username = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getString(1) : null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("findPasswordHash failed", e);
        }
    }

    public String findRole(String username) {
        String sql = "SELECT role FROM users WHERE username = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getString(1) : null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("findRole failed", e);
        }
    }

    public Integer findUserId(String username) {
        String sql = "SELECT id FROM users WHERE username = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("findUserId failed", e);
        }
    }
}
