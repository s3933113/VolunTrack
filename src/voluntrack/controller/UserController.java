package voluntrack.controller;

import javafx.collections.ObservableList;
import voluntrack.model.Project;
import voluntrack.service.AuthService;
import voluntrack.service.ProjectService;

/**
 * Thin facade kept for backward compatibility with earlier MVC.
 * Now delegates to services and no longer keeps in-memory users or reads CSV.
 */
public class UserController {
    private final AuthService authService = new AuthService();
    private final ProjectService projectService = new ProjectService();

    // Signup via AuthService
    public String addUser(String fullName, String username, String email, String password) {
        return authService.signup(fullName, username, email, password);
    }

    // Login returns role: "user" or "admin"; or error message
    public String loginRole(String username, String password) {
        return authService.login(username, password);
    }

    // For old code paths expecting boolean, keep a helper
    public boolean login(String username, String password) {
        String res = authService.login(username, password);
        return "user".equals(res) || "admin".equals(res);
    }

    // Projects for user view
    public ObservableList<Project> getProjects() {
        return projectService.getAllForUser();
    }
}
