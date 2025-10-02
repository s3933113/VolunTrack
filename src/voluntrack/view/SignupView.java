package voluntrack.view;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class SignupView {
    public void show(Stage stage) {
        TextField fullNameField = new TextField();
        fullNameField.setPromptText("Full Name");

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");

        TextField emailField = new TextField();
        emailField.setPromptText("Email");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        Button signupButton = new Button("Sign Up");

        VBox layout = new VBox(10, fullNameField, usernameField, emailField, passwordField, signupButton);
        Scene scene = new Scene(layout, 350, 250);

        stage.setTitle("Signup");
        stage.setScene(scene);
        stage.show();
    }
}
