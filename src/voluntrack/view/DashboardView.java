package voluntrack.view;

import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class DashboardView {
    public void show(Stage stage, String username) {
        Label welcome = new Label("Welcome, " + username + "!");
        VBox layout = new VBox(10, welcome);
        Scene scene = new Scene(layout, 400, 200);

        stage.setTitle("Dashboard");
        stage.setScene(scene);
        stage.show();
    }
}
