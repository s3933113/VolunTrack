package voluntrack.view;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import voluntrack.model.Project;
import voluntrack.service.ProjectService;
import voluntrack.view.AdminRegistrationsView;
import voluntrack.service.RegistrationService;

public class AdminDashboardView {

    private final ProjectService projectService;

    public AdminDashboardView(ProjectService projectService) {
        this.projectService = projectService;
    }

    public void show(Stage stage, String adminUsername, Runnable onBack) {
        Label title = new Label("Admin: " + adminUsername);

        TableView<Project> table = new TableView<>();
        table.setPlaceholder(new Label("No projects"));

        TableColumn<Project, Number> cId = new TableColumn<>("ID");
        cId.setCellValueFactory(d -> new SimpleIntegerProperty(d.getValue().getId()));

        TableColumn<Project, String> cTitle = new TableColumn<>("Title");
        cTitle.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getTitle()));

        TableColumn<Project, String> cLocation = new TableColumn<>("Location");
        cLocation.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getLocation()));

        TableColumn<Project, String> cDay = new TableColumn<>("Day");
        cDay.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getDay()));

        TableColumn<Project, Number> cHourly = new TableColumn<>("$/h");
        cHourly.setCellValueFactory(d -> new SimpleIntegerProperty(d.getValue().getHourlyValue()));

        TableColumn<Project, Number> cTotal = new TableColumn<>("Total");
        cTotal.setCellValueFactory(d -> new SimpleIntegerProperty(d.getValue().getTotalSlots()));

        TableColumn<Project, Number> cReg = new TableColumn<>("Registered");
        cReg.setCellValueFactory(d -> new SimpleIntegerProperty(d.getValue().getRegisteredSlots()));

        TableColumn<Project, String> cEnabled = new TableColumn<>("Enabled");
        cEnabled.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().isEnabled() ? "Yes" : "No"));

        table.getColumns().addAll(cId, cTitle, cLocation, cDay, cHourly, cTotal, cReg, cEnabled);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        table.setItems(projectService.getAllForAdmin());

        Button btnRefresh = new Button("Refresh");
        btnRefresh.setOnAction(e -> table.setItems(projectService.getAllForAdmin()));

        Button btnAdd = new Button("Add");
        btnAdd.setOnAction(e -> {
            Project edited = showProjectEditor(null);
            if (edited != null) {
                boolean ok = projectService.create(
                        edited.getTitle(),
                        edited.getLocation(),
                        edited.getDay(),
                        edited.getHourlyValue(),
                        edited.getTotalSlots()
                );
                if (ok) {
                    table.setItems(projectService.getAllForAdmin());
                    new Alert(Alert.AlertType.INFORMATION, "Created").showAndWait();
                } else {
                    new Alert(Alert.AlertType.ERROR, "Create failed").showAndWait();
                }
            }
        });

        Button btnEdit = new Button("Edit");
        btnEdit.setOnAction(e -> {
            Project sel = table.getSelectionModel().getSelectedItem();
            if (sel == null) { new Alert(Alert.AlertType.WARNING, "Select a row").showAndWait(); return; }
            Project edited = showProjectEditor(sel);
            if (edited != null) {
                Project toUpdate = new Project(
                        sel.getId(),
                        edited.getTitle(),
                        edited.getLocation(),
                        edited.getDay(),
                        edited.getHourlyValue(),
                        edited.getTotalSlots(),
                        sel.getRegisteredSlots(),
                        sel.isEnabled(),
                        sel.getCreatedAt()
                );
                boolean ok = projectService.update(toUpdate);
                if (ok) {
                    table.setItems(projectService.getAllForAdmin());
                    new Alert(Alert.AlertType.INFORMATION, "Updated").showAndWait();
                } else {
                    new Alert(Alert.AlertType.ERROR, "Update failed").showAndWait();
                }
            }
        });

        Button btnDelete = new Button("Delete");
        btnDelete.setOnAction(e -> {
            Project sel = table.getSelectionModel().getSelectedItem();
            if (sel == null) { new Alert(Alert.AlertType.WARNING, "Select a row").showAndWait(); return; }
            if (sel.getRegisteredSlots() > 0) { new Alert(Alert.AlertType.WARNING, "Has registrations").showAndWait(); return; }
            if (new Alert(Alert.AlertType.CONFIRMATION, "Delete project " + sel.getTitle() + "?")
                    .showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
                boolean ok = projectService.delete(sel.getId());
                if (ok) {
                    table.getItems().remove(sel);
                    new Alert(Alert.AlertType.INFORMATION, "Deleted").showAndWait();
                } else {
                    new Alert(Alert.AlertType.ERROR, "Delete failed").showAndWait();
                }
            }
        });

        Button btnToggle = new Button("Enable/Disable");
        btnToggle.setOnAction(e -> {
            Project sel = table.getSelectionModel().getSelectedItem();
            if (sel == null) { new Alert(Alert.AlertType.WARNING, "Select a row").showAndWait(); return; }
            boolean ok = projectService.toggleEnabled(sel.getId(), !sel.isEnabled());
            if (ok) {
                table.setItems(projectService.getAllForAdmin());
            } else {
                new Alert(Alert.AlertType.ERROR, "Toggle failed").showAndWait();
            }
        });

        // ปุ่มดูประวัติทั้งหมด
        Button btnAllRegs = new Button("All Registrations");
        btnAllRegs.setOnAction(e -> {
            AdminRegistrationsView view = new AdminRegistrationsView(new RegistrationService());
            view.show(stage, () -> this.show(stage, adminUsername, onBack));
        });

        Button btnBack = new Button("Back");
        btnBack.setOnAction(e -> onBack.run());

        HBox actions = new HBox(10, btnRefresh, btnAdd, btnEdit, btnDelete, btnToggle, btnAllRegs, new Separator(), btnBack);
        actions.setAlignment(Pos.CENTER_LEFT);
        actions.setPadding(new Insets(8));

        VBox top = new VBox(8, title, actions);
        top.setPadding(new Insets(10));

        BorderPane root = new BorderPane();
        root.setTop(top);
        root.setCenter(table);

        stage.setScene(new Scene(root, 1100, 640));
        stage.setTitle("Admin Dashboard");
        stage.show();
    }

    private Project showProjectEditor(Project original) {
        Dialog<Project> dialog = new Dialog<>();
        dialog.setTitle(original == null ? "Add Project" : "Edit Project");

        TextField tfTitle = new TextField();
        TextField tfLocation = new TextField();
        ChoiceBox<String> cbDay = new ChoiceBox<>();
        cbDay.getItems().addAll("Mon","Tue","Wed","Thu","Fri","Sat","Sun");
        Spinner<Integer> spHourly = new Spinner<>(1, 100, 20);
        Spinner<Integer> spTotal  = new Spinner<>(1, 100, 10);

        if (original != null) {
            tfTitle.setText(original.getTitle());
            tfLocation.setText(original.getLocation());
            cbDay.setValue(original.getDay());
            spHourly.getValueFactory().setValue(original.getHourlyValue());
            spTotal.getValueFactory().setValue(original.getTotalSlots());
        } else {
            cbDay.setValue("Mon");
        }

        GridPane form = new GridPane();
        form.setHgap(8);
        form.setVgap(8);
        form.setPadding(new Insets(12));
        form.addRow(0, new Label("Title"), tfTitle);
        form.addRow(1, new Label("Location"), tfLocation);
        form.addRow(2, new Label("Day"), cbDay);
        form.addRow(3, new Label("Hourly Value"), spHourly);
        form.addRow(4, new Label("Total Slots"), spTotal);

        dialog.getDialogPane().setContent(form);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(btn -> {
            if (btn == ButtonType.OK) {
                String title = tfTitle.getText().trim();
                String location = tfLocation.getText().trim();
                String day = cbDay.getValue();
                int hourly = spHourly.getValue();
                int total = spTotal.getValue();
                if (title.isEmpty() || location.isEmpty() || day == null) return null;

                return new Project(
                        0, title, location, day, hourly, total,
                        0, true, null
                );
            }
            return null;
        });

        return dialog.showAndWait().orElse(null);
    }


}