package voluntrack.repository;

import voluntrack.db.DatabaseManager;
import voluntrack.model.Project;
import voluntrack.util.TimeUtil;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;

public class ProjectRepository {

    private Connection conn() {
        return DatabaseManager.getInstance().getConnection();
    }

    // ผู้ใช้ทั่วไป เห็นเฉพาะ enabled
    public ObservableList<Project> findAllEnabled() {
        ObservableList<Project> list = FXCollections.observableArrayList();
        String sql = "SELECT id, title, location, day, hourly_value, total_slots, registered_slots, enabled, created_at " +
                "FROM projects WHERE enabled = 1 ORDER BY title";
        try (PreparedStatement ps = conn().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            throw new RuntimeException("findAllEnabled failed", e);
        }
        return list;
    }

    // แอดมิน เห็นทั้งหมด
    public ObservableList<Project> findAllForAdmin() {
        ObservableList<Project> list = FXCollections.observableArrayList();
        String sql = "SELECT id, title, location, day, hourly_value, total_slots, registered_slots, enabled, created_at " +
                "FROM projects ORDER BY title";
        try (PreparedStatement ps = conn().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            throw new RuntimeException("findAllForAdmin failed", e);
        }
        return list;
    }

    // สร้างโปรเจกต์ใหม่
    public boolean insert(Project p) {
        String sql = "INSERT INTO projects(title, location, day, hourly_value, total_slots, registered_slots, enabled, created_at) " +
                "VALUES(?,?,?,?,?,?,?,?)";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, p.getTitle());
            ps.setString(2, p.getLocation());
            ps.setString(3, p.getDay());
            ps.setInt(4, p.getHourlyValue());
            ps.setInt(5, p.getTotalSlots());
            ps.setInt(6, p.getRegisteredSlots());
            ps.setInt(7, p.isEnabled() ? 1 : 0);
            ps.setString(8, p.getCreatedAt() == null ? TimeUtil.nowIso() : p.getCreatedAt());
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new RuntimeException("insert project failed", e);
        }
    }

    // ปรับสถานะเปิดปิด
    public boolean setEnabled(int id, boolean enabled) {
        String sql = "UPDATE projects SET enabled = ? WHERE id = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, enabled ? 1 : 0);
            ps.setInt(2, id);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new RuntimeException("setEnabled failed", e);
        }
    }

    // ปรับจำนวนที่ลงทะเบียนแล้วแบบปลอดภัย
    public boolean updateRegisteredSlots(int id, int delta) {
        String sql = "UPDATE projects " +
                "SET registered_slots = registered_slots + ? " +
                "WHERE id = ? AND (registered_slots + ?) BETWEEN 0 AND total_slots";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, delta);
            ps.setInt(2, id);
            ps.setInt(3, delta);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new RuntimeException("updateRegisteredSlots failed", e);
        }
    }

    // แก้ไขข้อมูลหลัก
    public boolean update(Project p) {
        String sql = "UPDATE projects SET title=?, location=?, day=?, hourly_value=?, total_slots=?, enabled=? WHERE id=?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, p.getTitle());
            ps.setString(2, p.getLocation());
            ps.setString(3, p.getDay());
            ps.setInt(4, p.getHourlyValue());
            ps.setInt(5, p.getTotalSlots());
            ps.setInt(6, p.isEnabled() ? 1 : 0);
            ps.setInt(7, p.getId());
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new RuntimeException("update project failed", e);
        }
    }

    // ลบโปรเจกต์, ป้องกันลบถ้ามีคนลงทะเบียนแล้ว
    public boolean delete(int id) {
        // ปฏิเสธถ้ามี registered_slots > 0
        String guard = "SELECT registered_slots FROM projects WHERE id = ?";
        try (PreparedStatement g = conn().prepareStatement(guard)) {
            g.setInt(1, id);
            try (ResultSet rs = g.executeQuery()) {
                if (!rs.next()) return false;
                if (rs.getInt(1) > 0) return false;
            }
        } catch (SQLException e) {
            throw new RuntimeException("delete guard failed", e);
        }

        String sql = "DELETE FROM projects WHERE id = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new RuntimeException("delete project failed", e);
        }
    }

    private Project mapRow(ResultSet rs) throws SQLException {
        return new Project(
                rs.getInt("id"),
                rs.getString("title"),
                rs.getString("location"),
                rs.getString("day"),
                rs.getInt("hourly_value"),
                rs.getInt("total_slots"),
                rs.getInt("registered_slots"),
                rs.getInt("enabled") == 1,
                rs.getString("created_at")
        );
    }


}