package voluntrack.service;

import javafx.collections.ObservableList;
import voluntrack.model.Project;
import voluntrack.repository.ProjectRepository;

/**
 * Handles project-related logic for both user and admin.
 */
public class ProjectService {

    private final ProjectRepository repo = new ProjectRepository();

    // ดึงโครงการทั้งหมดที่เปิดให้ผู้ใช้ทั่วไปเห็น
    public ObservableList<Project> getAllForUser() {
        return repo.findAllEnabled();
    }

    // ดึงโครงการทั้งหมดสำหรับ admin
    public ObservableList<Project> getAllForAdmin() {
        return repo.findAllForAdmin();
    }

    // เพิ่มโครงการใหม่ (admin)
    public String addProject(String title, String location, String day, int hourly, int totalSlots) {
        if (title == null || title.isBlank() || location == null || location.isBlank())
            return "Title and location required.";
        if (hourly < 1 || hourly > 100)
            return "Hourly value must be between 1 and 100.";
        if (totalSlots < 1 || totalSlots > 100)
            return "Slots must be between 1 and 100.";

        Project p = new Project(0, title, location, day, hourly, totalSlots, 0, true, null);
        boolean ok = repo.insert(p);
        return ok ? "SUCCESS" : "Failed to insert project.";
    }

    // ปิดหรือเปิดโครงการ (admin)
    public boolean setEnabled(int projectId, boolean enabled) {
        return repo.setEnabled(projectId, enabled);
    }

    // อัปเดตข้อมูลโครงการ (admin)
    public String updateProject(Project p) {
        if (p.getTitle().isBlank() || p.getLocation().isBlank())
            return "Title and location cannot be empty.";
        boolean ok = repo.update(p);
        return ok ? "SUCCESS" : "Failed to update project.";
    }

    // ปรับจำนวน slot ที่ลงทะเบียนแล้ว
    public boolean adjustSlots(int projectId, int delta) {
        return repo.updateRegisteredSlots(projectId, delta);
    }
}
