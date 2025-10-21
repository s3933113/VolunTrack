package voluntrack.view;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import voluntrack.model.Registration;
import voluntrack.service.HistoryService;

public class HistoryView {
    private final HistoryService historyService = new HistoryService();

    public void show(Stage stage, String username, Runnable onBack) {
        Label title = new Label("History for " + username);

        TableView<Registration> table = new TableView<>();
        table.setPlaceholder(new Label("No history"));

        TableColumn<Registration, String> cProject = new TableColumn<>("Project");
        cProject.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getTitle()));

        TableColumn<Registration, String> cDay = new TableColumn<>("Day");
        cDay.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getDay()));

        TableColumn<Registration, Number> cSlots = new TableColumn<>("Slots");
        cSlots.setCellValueFactory(d -> new SimpleIntegerProperty(d.getValue().getSlots()));

        TableColumn<Registration, String> cWhen = new TableColumn<>("Confirmed At");
        cWhen.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getConfirmedAt()));

        table.getColumns().addAll(cProject, cDay, cSlots, cWhen);
        table.setItems(historyService.getUserHistory(username));
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);

        Button btnExport = new Button("Export");
        btnExport.setOnAction(e -> {
            String out = historyService.exportUserHistory(username);
            new Alert(Alert.AlertType.INFORMATION, "Exported to:\n" + out).showAndWait();
        });

        Button btnBack = new Button("Back");
        btnBack.setOnAction(e -> onBack.run());

        HBox actions = new HBox(10, btnExport, btnBack);
        actions.setPadding(new Insets(8));

        BorderPane root = new BorderPane();
        root.setTop(title);
        BorderPane.setMargin(title, new Insets(10, 10, 0, 10));
        root.setCenter(table);
        root.setBottom(actions);

        stage.setScene(new Scene(root, 700, 500));
        stage.setTitle("History");
        stage.show();
    }


}