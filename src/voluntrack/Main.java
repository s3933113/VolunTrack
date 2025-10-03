package voluntrack;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import voluntrack.controller.UserController;
import voluntrack.model.Project;
import voluntrack.model.User;

public class Main extends Application {
    private Stage stage;
    private final UserController controller = new UserController();

    @Override
    public void start(Stage stage) {
        this.stage = stage;
        stage.setTitle("VolunTrack");

        // โหลด projects.csv (วางไว้ที่รากโปรเจ็กต์ ข้างๆ src/)
        controller.loadProjectsFromCsv("projects.csv");

        showLogin();    // Lunch Login
        stage.show();
    } //

    /* =================== LOGIN =================== */
    private void showLogin() {
        Label title = new Label("Login");

        TextField username = new TextField();
        username.setPromptText("Username");

        PasswordField password = new PasswordField();
        password.setPromptText("Password");

        Button btnLogin  = new Button("Login");
        Button btnSignup = new Button("Sign up");

        btnLogin.setOnAction(e -> {
            if (controller.login(username.getText(), password.getText())) {
                showDashboard(username.getText());  //  go dashboard if login done
            } else {
                new Alert(Alert.AlertType.ERROR, "Invalid username/password").showAndWait();
            }
        });

        btnSignup.setOnAction(e -> showSignup());   // go to signup page

        VBox box = new VBox(12, title, username, password, btnLogin, btnSignup);
        box.setPadding(new Insets(16));
        stage.setScene(new Scene(box, 480, 320));
    }

    /* =================== SIGNUP =================== */
    private void showSignup() {
        Label title = new Label("Sign up");

        TextField fullName = new TextField(); fullName.setPromptText("Full name");
        TextField username = new TextField(); username.setPromptText("Username");
        TextField email    = new TextField(); email.setPromptText("Email");
        PasswordField password = new PasswordField(); password.setPromptText("Password");

        Button btnCreate = new Button("Create account");
        Button btnBack   = new Button("Back");

        btnCreate.setOnAction(e -> {

            controller.addUser(new User(
                    fullName.getText(), username.getText(), email.getText(), password.getText()));
            new Alert(Alert.AlertType.INFORMATION, "Account created! Please login.").showAndWait();
            showLogin();
        });

        btnBack.setOnAction(e -> showLogin());

        VBox box = new VBox(12, title, fullName, username, email, password, btnCreate, btnBack);
        box.setPadding(new Insets(16));
        stage.setScene(new Scene(box, 520, 360));
    }

    /* =================== DASHBOARD =================== */
    private void showDashboard(String username) {
        Label welcome = new Label("Welcome, " + (username == null ? "user" : username) + "!");

        TableView<Project> table = new TableView<>();

        TableColumn<Project, String> cTitle = new TableColumn<>("Title");
        cTitle.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTitle()));
        cTitle.setPrefWidth(220);

        TableColumn<Project, String> cDesc = new TableColumn<>("Description");
        cDesc.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDescription()));
        cDesc.setPrefWidth(360);

        table.getColumns().addAll(cTitle, cDesc);
        table.setItems(controller.getProjects());   //  CSV

        Button logout = new Button("Logout");
        logout.setOnAction(e -> showLogin());

        VBox box = new VBox(12, welcome, table, logout);
        box.setPadding(new Insets(16));
        stage.setScene(new Scene(box, 640, 420));
    }

    public static void main(String[] args) { launch(args); }
}
