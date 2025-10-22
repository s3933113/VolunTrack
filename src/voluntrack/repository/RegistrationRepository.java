package voluntrack.repository;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import voluntrack.db.DatabaseManager;
import voluntrack.model.Registration;
import voluntrack.util.IdUtil;
import voluntrack.util.TimeUtil;

import java.sql.*;

public class RegistrationRepository {

    private Connection conn() {
        return DatabaseManager.getInstance().getConnection();
    }

    // ดึงประวัติของผู้ใช้ตาม id เรียงจากล่าสุด
    public ObservableList<Registration> findByUserId(int userId) {
        ObservableList<Registration> list = FXCollections.observableArrayList();
        String sql =
                "SELECT r.id, r.user_id, r.project_id, r.slots, r.hours_per_slot, r.confirmed_at, r.total_value, " +
                        "       p.title, p.location, p.day, p.hourly_value " +
                        "FROM registrations r " +
                        "JOIN projects p ON r.project_id = p.id " +
                        "WHERE r.user_id = ? " +
                        "ORDER BY r.confirmed_at DESC, r.id DESC";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("findByUserId failed", e);
        }
        return list;
    }

    // ดึงประวัติทั้งหมดแบบ Registration เดิม ใช้ตอนฝั่ง user ต้องการรวม
    public ObservableList<Registration> findAll() {
        ObservableList<Registration> list = FXCollections.observableArrayList();
        String sql =
                "SELECT r.id, r.user_id, r.project_id, r.slots, r.hours_per_slot, r.confirmed_at, r.total_value, " +
                        "       p.title, p.location, p.day, p.hourly_value " +
                        "FROM registrations r " +
                        "JOIN projects p ON r.project_id = p.id " +
                        "ORDER BY r.confirmed_at DESC, r.id DESC";
        try (PreparedStatement ps = conn().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("findAll failed", e);
        }
        return list;
    }

    // โครงข้อมูลสำหรับหน้าแอดมิน
    public static class RegDetail {
        private final String regId;
        private final String confirmedAt;
        private final String username;
        private final String fullName;
        private final String projectTitle;
        private final String location;
        private final String day;
        private final int slots;
        private final int hoursPerSlot;
        private final int totalValue;

        public RegDetail(String regId, String confirmedAt, String username, String fullName,
                         String projectTitle, String location, String day,
                         int slots, int hoursPerSlot, int totalValue) {
            this.regId = regId;
            this.confirmedAt = confirmedAt;
            this.username = username;
            this.fullName = fullName;
            this.projectTitle = projectTitle;
            this.location = location;
            this.day = day;
            this.slots = slots;
            this.hoursPerSlot = hoursPerSlot;
            this.totalValue = totalValue;
        }
        public String getRegId() { return regId; }
        public String getConfirmedAt() { return confirmedAt; }
        public String getUsername() { return username; }
        public String getFullName() { return fullName; }
        public String getProjectTitle() { return projectTitle; }
        public String getLocation() { return location; }
        public String getDay() { return day; }
        public int getSlots() { return slots; }
        public int getHoursPerSlot() { return hoursPerSlot; }
        public int getTotalValue() { return totalValue; }
    }

    // ดึงประวัติทั้งหมดสำหรับแอดมิน join users และ projects
    public ObservableList<RegDetail> findAllDetails() {
        ObservableList<RegDetail> list = FXCollections.observableArrayList();
        String sql =
                "SELECT r.id, r.confirmed_at, " +
                        "       u.username, u.full_name, " +
                        "       p.title, p.location, p.day, " +
                        "       r.slots, r.hours_per_slot, r.total_value " +
                        "FROM registrations r " +
                        "JOIN users u ON r.user_id = u.id " +
                        "JOIN projects p ON r.project_id = p.id " +
                        "ORDER BY r.confirmed_at DESC, r.id DESC";
        try (PreparedStatement ps = conn().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new RegDetail(
                        rs.getString(1),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getString(4),
                        rs.getString(5),
                        rs.getString(6),
                        rs.getString(7),
                        rs.getInt(8),
                        rs.getInt(9),
                        rs.getInt(10)
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException("findAllDetails failed", e);
        }
        return list;
    }

    // เพิ่มการลงทะเบียนใหม่
    public boolean insert(int userId, int projectId, int slots, int hoursPerSlot, int totalValue) {
        String nextId = nextId();
        String sql =
                "INSERT INTO registrations(id, user_id, project_id, slots, hours_per_slot, confirmed_at, total_value) " +
                        "VALUES(?,?,?,?,?,?,?)";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, nextId);
            ps.setInt(2, userId);
            ps.setInt(3, projectId);
            ps.setInt(4, slots);
            ps.setInt(5, hoursPerSlot);
            ps.setString(6, TimeUtil.nowIso());
            ps.setInt(7, totalValue);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new RuntimeException("insert registration failed", e);
        }
    }

    // หา id ล่าสุดแล้วบวก 1
    private String nextId() {
        String sql = "SELECT id FROM registrations ORDER BY id DESC LIMIT 1";
        try (Statement st = conn().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            int last = 0;
            if (rs.next()) {
                try {
                    last = Integer.parseInt(rs.getString(1));
                } catch (Exception ignored) {}
            }
            return IdUtil.zeroPad4(last + 1);
        } catch (SQLException e) {
            throw new RuntimeException("nextId failed", e);
        }
    }

    private Registration mapRow(ResultSet rs) throws SQLException {
        return new Registration(
                rs.getString("id"),
                rs.getInt("user_id"),
                rs.getInt("project_id"),
                rs.getString("title"),
                rs.getString("location"),
                rs.getString("day"),
                rs.getInt("hourly_value"),
                rs.getInt("slots"),
                rs.getInt("hours_per_slot"),
                rs.getInt("total_value"),
                rs.getString("confirmed_at")
        );
    }


}