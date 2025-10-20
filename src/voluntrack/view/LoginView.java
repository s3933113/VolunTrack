package voluntrack.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import voluntrack.service.AuthService;

import java.util.function.BiConsumer;

/**
 * Login screen backed by AuthService.
 * onGoSignup: navigate to signup view
 * onLoginSuccess: callback with (username, role) where role is "user" or "admin"
 */
public class LoginView {
    private final AuthService authService;

    public LoginView(AuthService authService) {
        this.authService = authService;
    }

    public void show(Stage stage, Runnable onGoSignup, BiConsumer<String, String> onLoginSuccess) {
        Label title = new Label("Login");

        TextField tfUser = new TextField();
        tfUser.setPromptText("Username");

        PasswordField tfPass = new PasswordField();
        tfPass.setPromptText("Password");

        Button btnLogin = new Button("Login");
        Button btnSignup = new Button("Sign up");
        Label msg = new Label();

        btnLogin.setOnAction(e -> {
            String u = tfUser.getText();
            String p = tfPass.getText();
            String res = authService.login(u, p);
            if ("user".equals(res) || "admin".equals(res)) {
                msg.setText("");
                onLoginSuccess.accept(u, res);
            } else {
                msg.setText(res);
            }
        });

        btnSignup.setOnAction(e -> onGoSignup.run());

        VBox layout = new VBox(10, title, tfUser, tfPass, btnLogin, btnSignup, msg);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));
        layout.setPrefSize(400, 260);

        Scene scene = new Scene(layout);
        stage.setTitle("Login");
        stage.setScene(scene);
        stage.show();
    }
}
