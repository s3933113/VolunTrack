package voluntrack;

import javafx.application.Application;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import voluntrack.db.DatabaseManager;
import voluntrack.db.SeedData;
import voluntrack.service.*;
import voluntrack.view.DashboardView;
import voluntrack.view.LoginView;
import voluntrack.view.SignupView;

import java.io.InputStream;

public class Main extends Application {
    private Stage stage;

    // Services
    private final AuthService authService = new AuthService();
    private final ProjectService projectService = new ProjectService();
    private final CartService cartService = new CartService();
    private final RegistrationService registrationService = new RegistrationService();

    // Views
    private LoginView loginView;
    private SignupView signupView;
    private DashboardView dashboardView;

    @Override
    public void start(Stage stage) {
        this.stage = stage;
        stage.setTitle("VolunTrack");

        // DB init
        try {
            DatabaseManager db = DatabaseManager.getInstance();
            db.connect("data/voluntrack.db");
            try (InputStream in = getClass().getResourceAsStream("resources/sql/schema.sql")) {
                db.initSchema(in);
            }
            SeedData.run("data/projects.csv");
        } catch (Exception ex) {
            new Alert(Alert.AlertType.ERROR, "Database initialisation failed: " + ex.getMessage()).showAndWait();
        }

        // Init views
        loginView = new LoginView(authService);
        signupView = new SignupView(authService);
        dashboardView = new DashboardView(projectService, cartService, registrationService);

        showLogin();
        stage.show();
    }

    private void showLogin() {
        loginView.show(
                stage,
                this::showSignup,
                (username, role) -> {
                    if ("admin".equals(role)) {
                        // Admin dashboard view not implemented yet
                        new Alert(Alert.AlertType.INFORMATION, "Admin dashboard is coming next.").showAndWait();
                        // TODO: showAdminDashboard(username);
                    } else {
                        showUserDashboard(username);
                    }
                }
        );
    }

    private void showSignup() {
        signupView.show(stage, this::showLogin);
    }

    private void showUserDashboard(String username) {
        dashboardView.show(
                stage,
                username,
                this::showLogin,
                () -> new Alert(Alert.AlertType.INFORMATION, "Change password view coming next.").showAndWait(),
                () -> new Alert(Alert.AlertType.INFORMATION, "History view coming next.").showAndWait()
        );
    }

    public static void main(String[] args) { launch(args); }
}
