package voluntrack.view;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import voluntrack.service.AuthService;

public class ChangePasswordView {
    private final AuthService authService;

    public ChangePasswordView(AuthService authService) {
        this.authService = authService;
    }

    public void show(Stage stage, String username, Runnable onBack) {
        Label title = new Label("Change password for " + username);

        PasswordField oldPwd = new PasswordField();
        oldPwd.setPromptText("Current password");

        PasswordField newPwd = new PasswordField();
        newPwd.setPromptText("New password");

        PasswordField confirm = new PasswordField();
        confirm.setPromptText("Confirm new password");

        Label hint = new Label("Min 8 chars, include uppercase, digit, special");
        Label msg = new Label();

        Button btnSave = new Button("Save");
        Button btnBack = new Button("Back");

        btnSave.setOnAction(e -> {
            String res = authService.changePassword(username, oldPwd.getText(), newPwd.getText(), confirm.getText());
            if ("SUCCESS".equals(res)) {
                new Alert(Alert.AlertType.INFORMATION, "Password updated").showAndWait();
                onBack.run();
            } else {
                new Alert(Alert.AlertType.ERROR, res).showAndWait();
            }
        });

        btnBack.setOnAction(e -> onBack.run());

        VBox box = new VBox(10, title, oldPwd, newPwd, confirm, hint, btnSave, btnBack, msg);
        box.setPadding(new Insets(16));

        stage.setScene(new Scene(box, 420, 350));
        stage.setTitle("Change Password");
        stage.show();
    }


}