package voluntrack.service;

import voluntrack.db.DatabaseManager;
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

/**

 Handles registration confirmation logic and validations.
 */
public class RegistrationService {

    private final RegistrationRepository regRepo = new RegistrationRepository();
    private final ProjectRepository projectRepo = new ProjectRepository();
    private final UserRepository userRepo = new UserRepository();

    private static final String CONFIRM_CODE = "123456";

    // Confirm all items in the cart for the given user
    public String confirm(String username, CartService cart, String code) {
        if (cart == null || cart.isEmpty()) return "Cart is empty.";
        if (!isValidConfirmationCode(code)) return "Enter a 6 digit code";
        if (!CONFIRM_CODE.equals(code.trim())) return "Invalid code";

        Integer userId = userRepo.findUserId(username);
        if (userId == null) return "User not found.";

        // Validate day and slots for every item before writing
        for (CartService.CartItem item : cart.getItems()) {
            ProjectCore p = fetchProjectCore(item.getProjectId());
            if (p == null || p.enabled == 0) return "Project not available: " + item.getTitle();
            if (!isDayAllowed(p.day)) return "Project not available this week: " + item.getTitle();
            int newRegistered = p.registeredSlots + item.getSlots();
            if (newRegistered > p.totalSlots) return "Not enough slots for: " + item.getTitle();
        }

        // All validations passed, write changes
        for (CartService.CartItem item : cart.getItems()) {
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

    // Week starts Monday. Allow current or later day in this week
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
// MONDAY=1..SUNDAY=7 -> 0..6
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
            this.id = id;
            this.day = day;
            this.totalSlots = totalSlots;
            this.registeredSlots = registeredSlots;
            this.enabled = enabled;
        }
    }

    private ProjectCore fetchProjectCore(int projectId) {
        String sql = "SELECT id, day, total_slots, registered_slots, enabled FROM projects WHERE id = ?";
        Connection c = DatabaseManager.getInstance().getConnection(); // อย่าใช้ try-with-resources กับ Connection
        try (PreparedStatement ps = c.prepareStatement(sql)) {
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