package voluntrack;

import javafx.application.Application;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import voluntrack.db.DatabaseManager;
import voluntrack.db.SeedData;
import voluntrack.service.AuthService;
import voluntrack.service.CartService;
import voluntrack.service.ProjectService;
import voluntrack.service.RegistrationService;
import voluntrack.view.AdminDashboardView;
import voluntrack.view.ChangePasswordView;
import voluntrack.view.DashboardView;
import voluntrack.view.HistoryView;
import voluntrack.view.LoginView;
import voluntrack.view.SignupView;

import java.io.InputStream;

public class Main extends Application {
    private Stage stage;

    // Services
    private AuthService authService;
    private ProjectService projectService;
    private CartService cartService;
    private RegistrationService registrationService;

    // Views
    private LoginView loginView;
    private SignupView signupView;
    private DashboardView dashboardView;
    private AdminDashboardView adminDashboardView;

    @Override
    public void start(Stage stage) {
        this.stage = stage;
        stage.setTitle("VolunTrack");

        try {
            DatabaseManager db = DatabaseManager.getInstance();
            db.connect("data/voluntrack.db");

            try (InputStream in = getClass().getResourceAsStream("/sql/schema.sql")) {
                if (in == null) throw new IllegalStateException("schema.sql not found at /sql/schema.sql");
                db.initSchema(in);
            }

            SeedData.run("data/projects.csv");
        } catch (Exception ex) {
            new Alert(Alert.AlertType.ERROR, "Database initialisation failed: " + ex.getMessage()).showAndWait();
            return;
        }

        // create services after DB is ready
        authService = new AuthService();
        projectService = new ProjectService();
        cartService = new CartService();
        registrationService = new RegistrationService();

        // create views after services
        loginView = new LoginView(authService);
        signupView = new SignupView(authService);
        dashboardView = new DashboardView(projectService, cartService, registrationService);
        adminDashboardView = new AdminDashboardView(projectService); // IMPORTANT

        showLogin();
        stage.show();
    }

    private void showLogin() {
        loginView.show(
                stage,
                this::showSignup,
                (username, role) -> {
                    if ("admin".equals(role)) {
                        adminDashboardView.show(stage, username, this::showLogin);
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
                () -> {
                    ChangePasswordView view = new ChangePasswordView(authService);
                    view.show(stage, username, () -> showUserDashboard(username));
                },
                () -> {
                    HistoryView hv = new HistoryView();
                    hv.show(stage, username, () -> showUserDashboard(username));
                }
        );
    }

    public static void main(String[] args) { launch(args); }


}