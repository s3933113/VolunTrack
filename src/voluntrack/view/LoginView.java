package voluntrack.view;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LoginView {
    public void show(Stage stage) {
        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        Button loginButton = new Button("Login");

        VBox layout = new VBox(10, usernameField, passwordField, loginButton);
        Scene scene = new Scene(layout, 300, 200);

        stage.setTitle("Login");
        stage.setScene(scene);
        stage.show();
    }
}
