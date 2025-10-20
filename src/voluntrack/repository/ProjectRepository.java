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

    // ดึงโครงการทั้งหมดที่เปิดให้ผู้ใช้เห็น (enabled = 1)
    public ObservableList<Project> findAllEnabled() {
        ObservableList<Project> list = FXCollections.observableArrayList();
        String sql = "SELECT id, title, location, day, hourly_value, total_slots, registered_slots, enabled, created_at FROM projects WHERE enabled = 1 ORDER BY title";
        try (PreparedStatement ps = conn().prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("findAllEnabled failed", e);
        }
        return list;
    }

    // ดึงทุกโปรเจกต์สำหรับ admin
    public ObservableList<Project> findAllForAdmin() {
        ObservableList<Project> list = FXCollections.observableArrayList();
        String sql = "SELECT id, title, location, day, hourly_value, total_slots, registered_slots, enabled, created_at FROM projects ORDER BY title";
        try (PreparedStatement ps = conn().prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("findAllForAdmin failed", e);
        }
        return list;
    }

    // เพิ่มโปรเจกต์ใหม่
    public boolean insert(Project p) {
        String sql = "INSERT INTO projects(title, location, day, hourly_value, total_slots, registered_slots, enabled, created_at) VALUES(?,?,?,?,?,?,?,?)";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, p.getTitle());
            ps.setString(2, p.getLocation());
            ps.setString(3, p.getDay());
            ps.setInt(4, p.getHourlyValue());
            ps.setInt(5, p.getTotalSlots());
            ps.setInt(6, p.getRegisteredSlots());
            ps.setInt(7, p.isEnabled() ? 1 : 0);
            ps.setString(8, TimeUtil.nowIso());
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new RuntimeException("insert project failed", e);
        }
    }

    // ปรับสถานะเปิด/ปิดโปรเจกต์
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

    // อัปเดตจำนวนผู้เข้าร่วม (เพิ่มหรือลด slot ที่ถูกใช้)
    public boolean updateRegisteredSlots(int id, int delta) {
        String sql = "UPDATE projects SET registered_slots = registered_slots + ? WHERE id = ? AND registered_slots + ? BETWEEN 0 AND total_slots";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, delta);
            ps.setInt(2, id);
            ps.setInt(3, delta);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new RuntimeException("updateRegisteredSlots failed", e);
        }
    }

    // อัปเดตข้อมูลโปรเจกต์
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