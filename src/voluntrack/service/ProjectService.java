package voluntrack.service;

import javafx.collections.ObservableList;
import voluntrack.model.Project;
import voluntrack.repository.ProjectRepository;
import voluntrack.util.TimeUtil;

import java.util.Set;

public class ProjectService {

    private final ProjectRepository repo = new ProjectRepository();
    private static final Set<String> DAYS = Set.of("Mon","Tue","Wed","Thu","Fri","Sat","Sun");

    // ผู้ใช้ทั่วไปเห็นเฉพาะ enabled
    public ObservableList<Project> getAllForUser() {
        return repo.findAllEnabled();
    }

    // แอดมินเห็นทั้งหมด
    public ObservableList<Project> getAllForAdmin() {
        return repo.findAllForAdmin();
    }

    // สร้างโปรเจกต์ใหม่
    public boolean create(String title, String location, String day, int hourly, int totalSlots) {
        String error = validate(title, location, day, hourly, totalSlots);
        if (error != null) return false;

        Project p = new Project(title.trim(), location.trim(), day, hourly, totalSlots);
        return repo.insert(p);
    }

    // อัปเดตโปรเจกต์ที่มีอยู่
    public boolean update(Project p) {
        if (p == null) return false;
        String error = validate(p.getTitle(), p.getLocation(), p.getDay(), p.getHourlyValue(), p.getTotalSlots());
        if (error != null) return false;
        // ป้องกัน registeredSlots เกิน totalSlots
        if (p.getRegisteredSlots() > p.getTotalSlots()) return false;
        return repo.update(p);
    }

    // ลบโปรเจกต์ ต้องไม่มีผู้ลงทะเบียนแล้ว
    public boolean delete(int id) {
        return repo.delete(id);
    }

    // เปิด ปิด โปรเจกต์
    public boolean toggleEnabled(int id, boolean on) {
        return repo.setEnabled(id, on);
    }

    // ปรับจำนวน registered slots บวกหรือลบ
    public boolean updateRegisteredSlots(int projectId, int delta) {
        return repo.updateRegisteredSlots(projectId, delta);
    }

    private String validate(String title, String location, String day, int hourly, int totalSlots) {
        if (title == null || title.isBlank()) return "title";
        if (location == null || location.isBlank()) return "location";
        if (day == null || !DAYS.contains(day)) return "day";
        if (hourly < 1 || hourly > 100) return "hourly";
        if (totalSlots < 1 || totalSlots > 100) return "slots";
        return null;
    }


}