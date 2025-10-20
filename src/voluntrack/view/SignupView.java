package voluntrack.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import voluntrack.service.AuthService;

/**
 * Signup screen backed by AuthService.signup.
 * onBackToLogin: navigate back to login on success or when user clicks Back
 */
public class SignupView {
    private final AuthService authService;

    public SignupView(AuthService authService) {
        this.authService = authService;
    }

    public void show(Stage stage, Runnable onBackToLogin) {
        Label title = new Label("Sign up");

        TextField tfFullName = new TextField();
        tfFullName.setPromptText("Full name");

        TextField tfUsername = new TextField();
        tfUsername.setPromptText("Username");

        TextField tfEmail = new TextField();
        tfEmail.setPromptText("Email");

        PasswordField tfPassword = new PasswordField();
        tfPassword.setPromptText("Password");

        Button btnCreate = new Button("Create account");
        Button btnBack = new Button("Back");
        Label msg = new Label();

        btnCreate.setOnAction(e -> {
            String res = authService.signup(
                    tfFullName.getText(),
                    tfUsername.getText(),
                    tfEmail.getText(),
                    tfPassword.getText());
            if ("SUCCESS".equals(res)) {
                msg.setText("Account created. Please login.");
                onBackToLogin.run();
            } else {
                msg.setText(res);
            }
        });

        btnBack.setOnAction(e -> onBackToLogin.run());

        VBox layout = new VBox(10, title, tfFullName, tfUsername, tfEmail, tfPassword, btnCreate, btnBack, msg);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));
        layout.setPrefSize(460, 320);

        Scene scene = new Scene(layout);
        stage.setTitle("Sign up");
        stage.setScene(scene);
        stage.show();
    }
}