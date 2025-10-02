package voluntrack.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import voluntrack.model.Project;
import voluntrack.model.User;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class UserController {
    // Users เก็บชั่วคราว (milestone ยังไม่ใช้ DB)
    private final ObservableList<User> users = FXCollections.observableArrayList();

    // Projects สำหรับโชว์บน Dashboard (ผูกกับ TableView)
    private final ObservableList<Project> projects = FXCollections.observableArrayList();

    // ---------- User ops ----------
    public void addUser(User user) {
        users.add(user);
    }

    public boolean login(String username, String password) {
        return users.stream().anyMatch(u ->
                u.getUsername().equals(username) && u.getPassword().equals(password));
    }

    // ---------- Project ops ----------
    public ObservableList<Project> getProjects() {
        return projects;
    }

    /**
     * โหลดไฟล์ CSV รูปแบบ: title,description
     * วางไฟล์ไว้ที่รากโปรเจกต์ เช่น "projects.csv"
     */
    public void loadProjectsFromCsv(String csvPath) {
        projects.clear();
        try (BufferedReader br = new BufferedReader(
                new FileReader(csvPath, StandardCharsets.UTF_8))) {

            String line;
            boolean first = true;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                // ข้าม header ถ้ามี
                if (first && line.toLowerCase().startsWith("title,")) {
                    first = false;
                    continue;
                }
                first = false;

                // split แค่ 2 ช่อง (กัน description มี comma ภายใน)
                String[] parts = line.split(",", 2);
                String title = parts.length > 0 ? parts[0].trim() : "";
                String desc  = parts.length > 1 ? parts[1].trim() : "";

                projects.add(new Project(title, desc));
            }
        } catch (IOException e) {
            e.printStackTrace(); // milestone: แสดง stacktrace พอ
        }
    }
}
