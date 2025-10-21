package voluntrack.view;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
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
import voluntrack.service.CartService;
import voluntrack.service.ProjectService;
import voluntrack.service.RegistrationService;

/**
 * User dashboard: list projects, manage cart, confirm registration, export history, change password, logout.
 */
public class DashboardView {

    private final ProjectService projectService;
    private final CartService cartService;
    private final RegistrationService registrationService;

    public DashboardView(ProjectService projectService,
                         CartService cartService,
                         RegistrationService registrationService) {
        this.projectService = projectService;
        this.cartService = cartService;
        this.registrationService = registrationService;
    }

    public void show(Stage stage, String username,
                     Runnable onLogout,
                     Runnable onChangePassword,
                     Runnable onViewHistory) {
        // Top bar
        Label welcome = new Label("Welcome, " + username + "!");
        Button btnHistory = new Button("History");
        Button btnChangePwd = new Button("Change Password");
        Button btnLogout = new Button("Logout");
        btnHistory.setOnAction(e -> onViewHistory.run());
        btnChangePwd.setOnAction(e -> onChangePassword.run());
        btnLogout.setOnAction(e -> onLogout.run());
        HBox top = new HBox(10, welcome, new Separator(), btnHistory, btnChangePwd, btnLogout);
        top.setAlignment(Pos.CENTER_LEFT);
        top.setPadding(new Insets(10));

        // Projects table
        TableView<Project> tblProjects = new TableView<>();
        TableColumn<Project, String> cTitle = new TableColumn<>("Title");
        cTitle.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getTitle()));
        TableColumn<Project, String> cLocation = new TableColumn<>("Location");
        cLocation.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getLocation()));
        TableColumn<Project, String> cDay = new TableColumn<>("Day");
        cDay.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getDay()));
        TableColumn<Project, Number> cHourly = new TableColumn<>("$/h");
        cHourly.setCellValueFactory(d -> new SimpleIntegerProperty(d.getValue().getHourlyValue()));
        TableColumn<Project, Number> cAvail = new TableColumn<>("Avail");
        cAvail.setCellValueFactory(d -> new SimpleIntegerProperty(d.getValue().getAvailableSlots()));
        TableColumn<Project, Number> cTotal = new TableColumn<>("Total");
        cTotal.setCellValueFactory(d -> new SimpleIntegerProperty(d.getValue().getTotalSlots()));
        tblProjects.getColumns().addAll(cTitle, cLocation, cDay, cHourly, cAvail, cTotal);
        tblProjects.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);

        // Load projects
        ObservableList<Project> data = projectService.getAllForUser();
        tblProjects.setItems(data);

        // Cart table (declare BEFORE handlers that reference it)
        TableView<CartService.CartItem> tblCart = new TableView<>(cartService.getItems());
        TableColumn<CartService.CartItem, String> ciTitle = new TableColumn<>("Project");
        ciTitle.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getTitle()));
        TableColumn<CartService.CartItem, Number> ciSlots = new TableColumn<>("Slots");
        ciSlots.setCellValueFactory(d -> new SimpleIntegerProperty(d.getValue().getSlots()));
        TableColumn<CartService.CartItem, Number> ciHours = new TableColumn<>("Hours/slot");
        ciHours.setCellValueFactory(d -> new SimpleIntegerProperty(d.getValue().getHoursPerSlot()));
        TableColumn<CartService.CartItem, Number> ciHourly = new TableColumn<>("$/h");
        ciHourly.setCellValueFactory(d -> new SimpleIntegerProperty(d.getValue().getHourlyValue()));
        TableColumn<CartService.CartItem, Number> ciTotal = new TableColumn<>("Value");
        ciTotal.setCellValueFactory(d -> new SimpleIntegerProperty(d.getValue().itemContribution()));
        tblCart.getColumns().addAll(ciTitle, ciSlots, ciHours, ciHourly, ciTotal);
        tblCart.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);

        // Totals label (declare BEFORE handlers that reference it)
        Label lblTotal = new Label("Total: $0");

        // Add to cart controls
        Spinner<Integer> spSlots = new Spinner<>(1, CartService.MAX_SLOTS_PER_PROJECT, 1);
        Spinner<Integer> spHours = new Spinner<>(CartService.MIN_HOURS, CartService.MAX_HOURS, 1);
        Button btnAdd = new Button("Add to cart");
        Label addMsg = new Label();
        btnAdd.setOnAction(e -> {
            Project p = tblProjects.getSelectionModel().getSelectedItem();
            if (p == null) { addMsg.setText("Select a project first."); return; }
            int slots = spSlots.getValue();
            int hours = spHours.getValue();
            if (slots > p.getAvailableSlots()) {
                addMsg.setText("Not enough slots.");
                return;
            }
            String res = cartService.addOrUpdate(p.getId(), p.getTitle(), p.getHourlyValue(), slots, hours);
            if ("SUCCESS".equals(res)) {
                addMsg.setText("Added.");
                tblCart.refresh();
                updateTotals(lblTotal);
            } else {
                addMsg.setText(res);
            }
        });

        GridPane addPane = new GridPane();
        addPane.setHgap(8); addPane.setVgap(6);
        addPane.addRow(0, new Label("Slots"), spSlots, new Label("Hours/slot"), spHours, btnAdd, addMsg);

        Button btnRemove = new Button("Remove selected");
        btnRemove.setOnAction(e -> {
            CartService.CartItem sel = tblCart.getSelectionModel().getSelectedItem();
            if (sel != null) {
                cartService.remove(sel.getProjectId());
                updateTotals(lblTotal);
                tblCart.refresh();
            }
        });

        // Confirm section
        TextField tfCode = new TextField();
        tfCode.setPromptText("6-digit code");
        Button btnConfirm = new Button("Confirm registration");
        Label confirmMsg = new Label();
        btnConfirm.setOnAction(e -> {
            String code = tfCode.getText();
            String res = registrationService.confirm(username, cartService, code);
            if ("SUCCESS".equals(res)) {
                confirmMsg.setText("Registered successfully.");
                tfCode.clear();
                updateTotals(lblTotal);
                // refresh project list since slots changed
                tblProjects.setItems(projectService.getAllForUser());
            } else {
                confirmMsg.setText(res);
            }
        });

        HBox confirmBox = new HBox(10, lblTotal, new Label("Code"), tfCode, btnConfirm, confirmMsg);
        confirmBox.setAlignment(Pos.CENTER_LEFT);

        VBox center = new VBox(10,
                new Label("Projects"), tblProjects, addPane,
                new Separator(), new Label("Cart"), tblCart, btnRemove,
                confirmBox);
        center.setPadding(new Insets(10));

        BorderPane root = new BorderPane();
        root.setTop(top);
        root.setCenter(center);

        Scene scene = new Scene(root, 900, 950);
        stage.setTitle("Dashboard");
        stage.setScene(scene);
        stage.show();

        updateTotals(lblTotal);
    }

    private void updateTotals(Label lblTotal) {
        lblTotal.setText("Total: $" + cartService.totalContribution());
    }
}
