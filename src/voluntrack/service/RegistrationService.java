package voluntrack.service;

import voluntrack.repository.ProjectRepository;
import voluntrack.repository.RegistrationRepository;
import voluntrack.repository.UserRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import voluntrack.db.DatabaseManager;

/**
 * Handles registration confirmation logic and validations.
 */
public class RegistrationService {

    private final RegistrationRepository regRepo = new RegistrationRepository();
    private final ProjectRepository projectRepo = new ProjectRepository();
    private final UserRepository userRepo = new UserRepository();

    // Confirm all items in the cart for the given user
    public String confirm(String username, CartService cart, String confirmationCode) {
        if (cart == null || cart.isEmpty()) return "Cart is empty.";
        if (!isValidConfirmationCode(confirmationCode)) return "Invalid 6-digit confirmation code.";

        Integer userId = userRepo.findUserId(username);
        if (userId == null) return "User not found.";

        // Validate day-of-week and slot availability for each item before any write
        for (CartService.CartItem item : cart.getItems()) {
            ProjectCore p = fetchProjectCore(item.getProjectId());
            if (p == null || p.enabled == 0) return "Project not available: " + item.getTitle();
            if (!isDayAllowed(p.day)) return "This project's day is no longer available this week.";
            int newRegistered = p.registeredSlots + item.getSlots();
            if (newRegistered > p.totalSlots) return "Not enough slots for: " + item.getTitle();
        }

        // All validations passed, perform writes
        for (CartService.CartItem item : cart.getItems()) {
            ProjectCore p = fetchProjectCore(item.getProjectId());
            int totalValue = item.getHourlyValue() * item.getHoursPerSlot() * item.getSlots();
            boolean ok1 = regRepo.insert(userId, item.getProjectId(), item.getSlots(), item.getHoursPerSlot(), totalValue);
            boolean ok2 = projectRepo.updateRegisteredSlots(item.getProjectId(), item.getSlots());
            if (!ok1 || !ok2) return "Failed to save registration for: " + item.getTitle();
        }

        cart.clear();
        return "SUCCESS";
    }

    // 6-digit numeric only
    public boolean isValidConfirmationCode(String code) {
        return code != null && code.matches("\\d{6}");
    }

    // Day-of-week restriction: can only register for current or later days in the same week
    // Week starts on Monday
    public boolean isDayAllowed(String projectDay) {
        String[] days = {"Mon","Tue","Wed","Thu","Fri","Sat","Sun"};
        Map<String,Integer> idx = new HashMap<>();
        for (int i = 0; i < days.length; i++) idx.put(days[i], i);

        int todayIdx = dayIndexOfToday();
        Integer projIdx = idx.get(projectDay);
        if (projIdx == null) return false;
        return projIdx >= todayIdx;
    }

    private int dayIndexOfToday() {
        DayOfWeek dow = LocalDate.now().getDayOfWeek();
        // Java: MONDAY=1 .. SUNDAY=7; we want 0..6 with Monday=0
        return (dow.getValue() + 6) % 7;
    }

    // Minimal project info used for validations
    private static final class ProjectCore {
        final int id;
        final String day;
        final int totalSlots;
        final int registeredSlots;
        final int enabled;
        ProjectCore(int id, String day, int totalSlots, int registeredSlots, int enabled) {
            this.id = id; this.day = day; this.totalSlots = totalSlots; this.registeredSlots = registeredSlots; this.enabled = enabled;
        }
    }

    private ProjectCore fetchProjectCore(int projectId) {
        String sql = "SELECT id, day, total_slots, registered_slots, enabled FROM projects WHERE id = ?";
        try (Connection c = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, projectId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new ProjectCore(
                            rs.getInt(1),
                            rs.getString(2),
                            rs.getInt(3),
                            rs.getInt(4),
                            rs.getInt(5)
                    );
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("fetchProjectCore failed", e);
        }
        return null;
    }
}
