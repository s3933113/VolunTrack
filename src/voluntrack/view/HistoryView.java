package voluntrack.view;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import voluntrack.model.Registration;
import voluntrack.service.HistoryService;

public class HistoryView {
    private final HistoryService historyService = new HistoryService();

    public void show(Stage stage, String username, Runnable onBack) {
        Label title = new Label("History for " + username);
        TableView<Registration> table = new TableView<>();

        TableColumn<Registration, String> cTitle = new TableColumn<>("Project");
        cTitle.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getTitle()));

        TableColumn<Registration, String> cDay = new TableColumn<>("Day");
        cDay.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getDay()));

        TableColumn<Registration, Number> cSlots = new TableColumn<>("Slots");
        cSlots.setCellValueFactory(d -> new javafx.beans.property.SimpleIntegerProperty(d.getValue().getSlots()));

        TableColumn<Registration, String> cConfirm = new TableColumn<>("Confirmed");
        cConfirm.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getConfirmedAt()));

        table.getColumns().addAll(cTitle, cDay, cSlots, cConfirm);

        ObservableList<Registration> rows = historyService.getUserHistory(username);
        table.setItems(rows);

        Button btnExport = new Button("Export to text file");
        btnExport.setOnAction(e -> {
            String res = historyService.exportUserHistory(username);
            new Alert(Alert.AlertType.INFORMATION, "Export done:\n" + res).showAndWait();
        });

        Button btnBack = new Button("Back");
        btnBack.setOnAction(e -> onBack.run());

        VBox bottom = new VBox(8, btnExport, btnBack);
        bottom.setPadding(new Insets(8));

        BorderPane root = new BorderPane();
        root.setTop(title);
        root.setCenter(table);
        root.setBottom(bottom);

        stage.setScene(new Scene(root, 700, 500));
        stage.setTitle("History");
        stage.show();
    }
}
