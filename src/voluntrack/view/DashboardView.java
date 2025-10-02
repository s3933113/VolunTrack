package voluntrack.view;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import voluntrack.model.Project;

public class DashboardView {
    public void show(Stage stage, String username, ObservableList<Project> projects) {
        Label welcome = new Label("Welcome, " + username + "!");

        // สร้าง TableView
        TableView<Project> table = new TableView<>();

        TableColumn<Project, String> cTitle = new TableColumn<>("Title");
        cTitle.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTitle()));
        cTitle.setPrefWidth(200);

        TableColumn<Project, String> cDesc = new TableColumn<>("Description");
        cDesc.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDescription()));
        cDesc.setPrefWidth(300);

        table.getColumns().addAll(cTitle, cDesc);
        table.setItems(projects);  // projects จาก controller

        VBox layout = new VBox(12, welcome, table);
        layout.setPadding(new Insets(20));

        Scene scene = new Scene(layout, 600, 400);
        stage.setTitle("Dashboard");
        stage.setScene(scene);
        stage.show();
    }
}
