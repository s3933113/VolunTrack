package voluntrack.service;

import javafx.collections.ObservableList;
import voluntrack.model.Registration;
import voluntrack.repository.RegistrationRepository;
import voluntrack.repository.UserRepository;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * History service: fetch participation history and export to text file under data/exports.
 */
public class HistoryService {

    private final RegistrationRepository regRepo = new RegistrationRepository();
    private final UserRepository userRepo = new UserRepository();

    // Get history for a username (most recent first)
    public ObservableList<Registration> getUserHistory(String username) {
        Integer userId = userRepo.findUserId(username);
        if (userId == null) throw new IllegalArgumentException("User not found: " + username);
        return regRepo.findByUserId(userId);
    }

    // Export to data/exports/history_<username>.txt . Returns absolute path when success
    public String exportUserHistory(String username) {
        Integer userId = userRepo.findUserId(username);
        if (userId == null) return "User not found.";
        ObservableList<Registration> rows = regRepo.findByUserId(userId);

        // ensure folder exists
        File dir = new File("data/exports");
        if (!dir.exists()) dir.mkdirs();
        File out = new File(dir, "history_" + username + ".txt");

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(out, StandardCharsets.UTF_8))) {
            bw.write("VolunTrack Participation History\n");
            bw.write("User: " + username + "\n\n");
            bw.write(String.format("%-6s | %-19s | %-16s | %-3s | %-5s | %-5s | %-6s\n",
                    "ID", "Confirmed", "Project", "Day", "Slots", "Hr/Sl", "Value"));
            bw.write("----------------------------------------------------------------------\n");
            for (Registration r : rows) {
                bw.write(String.format("%-6s | %-19s | %-16s | %-3s | %-5d | %-5d | $%-5d\n",
                        r.getId(), r.getConfirmedAt(), r.getTitle(), r.getDay(), r.getSlots(), r.getHoursPerSlot(), r.getTotalValue()));
            }
        } catch (IOException e) {
            return "Failed to write file: " + e.getMessage();
        }
        return out.getAbsolutePath();
    }
}
