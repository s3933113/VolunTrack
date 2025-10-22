package voluntrack.view;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import voluntrack.repository.RegistrationRepository;
import voluntrack.service.RegistrationService;

public class AdminRegistrationsView {

    private final RegistrationService registrationService;

    public AdminRegistrationsView(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    public void show(Stage stage, Runnable onBack) {
        Label title = new Label("All Participation Histories");

        Button btnRefresh = new Button("Refresh");
        Button btnBack = new Button("Back");
        btnBack.setOnAction(e -> onBack.run());

        TableView<RegistrationRepository.RegDetail> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);

        TableColumn<RegistrationRepository.RegDetail, String> cRegId = new TableColumn<>("Reg ID");
        cRegId.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getRegId()));

        TableColumn<RegistrationRepository.RegDetail, String> cTime = new TableColumn<>("Confirmed At");
        cTime.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getConfirmedAt()));

        TableColumn<RegistrationRepository.RegDetail, String> cUser = new TableColumn<>("Username");
        cUser.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getUsername()));

        TableColumn<RegistrationRepository.RegDetail, String> cName = new TableColumn<>("Full Name");
        cName.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getFullName()));

        TableColumn<RegistrationRepository.RegDetail, String> cProj = new TableColumn<>("Project");
        cProj.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getProjectTitle()));

        TableColumn<RegistrationRepository.RegDetail, String> cLoc = new TableColumn<>("Location");
        cLoc.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getLocation()));

        TableColumn<RegistrationRepository.RegDetail, String> cDay = new TableColumn<>("Day");
        cDay.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getDay()));

        TableColumn<RegistrationRepository.RegDetail, Number> cSlots = new TableColumn<>("Slots");
        cSlots.setCellValueFactory(d -> new SimpleIntegerProperty(d.getValue().getSlots()));

        TableColumn<RegistrationRepository.RegDetail, Number> cHours = new TableColumn<>("Hours/slot");
        cHours.setCellValueFactory(d -> new SimpleIntegerProperty(d.getValue().getHoursPerSlot()));

        TableColumn<RegistrationRepository.RegDetail, Number> cValue = new TableColumn<>("Total Value");
        cValue.setCellValueFactory(d -> new SimpleIntegerProperty(d.getValue().getTotalValue()));

        table.getColumns().addAll(
                cRegId, cTime, cUser, cName, cProj, cLoc, cDay, cSlots, cHours, cValue
        );

        btnRefresh.setOnAction(e -> table.setItems(registrationService.listAllDetailsForAdmin()));

        HBox top = new HBox(10, title, new Separator(), btnRefresh, btnBack);
        top.setAlignment(Pos.CENTER_LEFT);
        top.setPadding(new Insets(10));

        BorderPane root = new BorderPane();
        root.setTop(top);
        root.setCenter(table);
        root.setPadding(new Insets(10));

        Scene scene = new Scene(root, 1000, 600);
        stage.setTitle("Admin - All Registrations");
        stage.setScene(scene);
        stage.show();

        // Initial load
        ObservableList<RegistrationRepository.RegDetail> rows = registrationService.listAllDetailsForAdmin();
        table.setItems(rows);
    }


}