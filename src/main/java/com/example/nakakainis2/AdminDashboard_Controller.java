package com.example.nakakainis2;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Callback;

public class AdminDashboard_Controller {

    @FXML
    private ImageView imgLogo;

    @FXML
    private Label lblDashboard;

    @FXML
    private AnchorPane lblExpiringSoon;

    @FXML
    private AnchorPane lblRecentDonation;

    @FXML
    private AnchorPane lblTotalGoods;

    @FXML
    private AnchorPane mainContent;

    @FXML
    private Label navDashboard;
    @FXML
    private Label navInventory;
    @FXML
    private Label navManagement;
    @FXML
    private Label navMap;
    @FXML
    private Label navReports;

    @FXML
    private AnchorPane paneDashboard;
    @FXML
    private AnchorPane paneInventory;
    @FXML
    private AnchorPane paneManagement;
    @FXML
    private AnchorPane paneMap;
    @FXML
    private AnchorPane paneReports;

    // Add a TextArea to display inventory data for testing
    @FXML
    private TextArea inventoryTextArea;

    // CRUD UI Components for Inventory Management
    @FXML
    private TableView<DatabaseHelper.InventoryItem> inventoryTable;

    @FXML
    private TableColumn<DatabaseHelper.InventoryItem, Integer> colItemId;
    @FXML
    private TableColumn<DatabaseHelper.InventoryItem, String> colItemName;
    @FXML
    private TableColumn<DatabaseHelper.InventoryItem, String> colCategory;
    @FXML
    private TableColumn<DatabaseHelper.InventoryItem, Integer> colQuantity;
    @FXML
    private TableColumn<DatabaseHelper.InventoryItem, String> colLocation;
    @FXML
    private TableColumn<DatabaseHelper.InventoryItem, String> colExpirationDate;
    @FXML
    private TableColumn<DatabaseHelper.InventoryItem, Void> colActions;

    // Form fields for CRUD operations
    @FXML
    private TextField txtItemName;
    @FXML
    private TextField txtCategory;
    @FXML
    private TextField txtQuantity;
    @FXML
    private TextField txtLocation;
    @FXML
    private DatePicker dateExpiration;

    @FXML
    private Button btnAdd;
    @FXML
    private Button btnUpdate;
    @FXML
    private Button btnDelete;
    @FXML
    private Button btnClear;
    @FXML
    private Button btnRefresh;

    // Dashboard summary labels
    @FXML
    private Label lblTotalItems;
    @FXML
    private Label lblTotalQuantity;
    @FXML
    private Label lblLowStock;
    @FXML
    private Label lblExpiringSoonCount;

    private DatabaseHelper.InventoryItem selectedItem = null;

    @FXML
    private void initialize() {
        showSection("dashboard");
        initializeInventoryTable();
        setupFormValidation();
        loadDashboardData();
        loadInventoryData();
    }

    private void initializeInventoryTable() {
        if (inventoryTable == null)
            return;

        // Set up table columns
        colItemId.setCellValueFactory(new PropertyValueFactory<>("itemId"));
        colItemName.setCellValueFactory(new PropertyValueFactory<>("itemName"));
        colCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
        colQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colLocation.setCellValueFactory(new PropertyValueFactory<>("location"));
        colExpirationDate.setCellValueFactory(new PropertyValueFactory<>("expirationDate"));

        // Add action buttons column
        colActions.setCellFactory(
                new Callback<TableColumn<DatabaseHelper.InventoryItem, Void>, TableCell<DatabaseHelper.InventoryItem, Void>>() {
                    @Override
                    public TableCell<DatabaseHelper.InventoryItem, Void> call(
                            TableColumn<DatabaseHelper.InventoryItem, Void> param) {
                        return new TableCell<DatabaseHelper.InventoryItem, Void>() {
                            private final Button editBtn = new Button("Edit");
                            private final Button deleteBtn = new Button("Delete");

                            {
                                editBtn.setStyle(
                                        "-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 10px;");
                                deleteBtn.setStyle(
                                        "-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-size: 10px;");

                                editBtn.setOnAction(e -> {
                                    DatabaseHelper.InventoryItem item = getTableView().getItems().get(getIndex());
                                    editItem(item);
                                });

                                deleteBtn.setOnAction(e -> {
                                    DatabaseHelper.InventoryItem item = getTableView().getItems().get(getIndex());
                                    deleteItem(item);
                                });
                            }

                            @Override
                            protected void updateItem(Void item, boolean empty) {
                                super.updateItem(item, empty);
                                if (empty) {
                                    setGraphic(null);
                                } else {
                                    setGraphic(new javafx.scene.layout.HBox(5, editBtn, deleteBtn));
                                }
                            }
                        };
                    }
                });

        // Table selection listener
        inventoryTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedItem = newSelection;
                populateForm(newSelection);
            }
        });
    }

    private void setupFormValidation() {
        // Add listeners for form validation
        if (txtItemName != null) {
            txtItemName.textProperty().addListener((obs, oldText, newText) -> validateForm());
        }
        if (txtQuantity != null) {
            txtQuantity.textProperty().addListener((obs, oldText, newText) -> {
                // Only allow numbers
                if (!newText.matches("\\d*")) {
                    txtQuantity.setText(newText.replaceAll("[^\\d]", ""));
                }
                validateForm();
            });
        }
    }

    private void validateForm() {
        boolean isValid = txtItemName != null && !txtItemName.getText().trim().isEmpty() &&
                txtQuantity != null && !txtQuantity.getText().trim().isEmpty();

        if (btnAdd != null)
            btnAdd.setDisable(!isValid);
        if (btnUpdate != null)
            btnUpdate.setDisable(!isValid || selectedItem == null);
    }

    // CRUD Operations

    @FXML
    private void handleAddItem() {
        try {
            String name = txtItemName.getText().trim();
            String category = txtCategory.getText().trim();
            String quantityStr = txtQuantity.getText().trim();
            String location = txtLocation.getText().trim();
            String expirationDate = dateExpiration.getValue() != null ? dateExpiration.getValue().toString() : null;

            if (name.isEmpty() || quantityStr.isEmpty()) {
                showAlert("Error", "Item name and quantity are required!", Alert.AlertType.ERROR);
                return;
            }

            int quantity = Integer.parseInt(quantityStr);
            DatabaseHelper.InventoryItem newItem = new DatabaseHelper.InventoryItem(name, category, quantity, location,
                    expirationDate);

            int newId = DatabaseHelper.addInventoryItem(newItem);
            showAlert("Success", "Item added successfully with ID: " + newId, Alert.AlertType.INFORMATION);

            clearForm();
            loadInventoryData();
            loadDashboardData();

        } catch (NumberFormatException e) {
            showAlert("Error", "Quantity must be a valid number!", Alert.AlertType.ERROR);
        } catch (Exception e) {
            showAlert("Error", "Failed to add item: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleUpdateItem() {
        if (selectedItem == null) {
            showAlert("Error", "Please select an item to update!", Alert.AlertType.ERROR);
            return;
        }

        try {
            String name = txtItemName.getText().trim();
            String category = txtCategory.getText().trim();
            String quantityStr = txtQuantity.getText().trim();
            String location = txtLocation.getText().trim();
            String expirationDate = dateExpiration.getValue() != null ? dateExpiration.getValue().toString() : null;

            if (name.isEmpty() || quantityStr.isEmpty()) {
                showAlert("Error", "Item name and quantity are required!", Alert.AlertType.ERROR);
                return;
            }

            int quantity = Integer.parseInt(quantityStr);
            DatabaseHelper.InventoryItem updatedItem = new DatabaseHelper.InventoryItem(
                    selectedItem.getItemId(), name, category, quantity, location, expirationDate);

            DatabaseHelper.updateInventoryItemById(String.valueOf(selectedItem.getItemId()), updatedItem);
            showAlert("Success", "Item updated successfully!", Alert.AlertType.INFORMATION);

            clearForm();
            loadInventoryData();
            loadDashboardData();

        } catch (NumberFormatException e) {
            showAlert("Error", "Quantity must be a valid number!", Alert.AlertType.ERROR);
        } catch (Exception e) {
            showAlert("Error", "Failed to update item: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleDeleteItem() {
        if (selectedItem == null) {
            showAlert("Error", "Please select an item to delete!", Alert.AlertType.ERROR);
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Delete");
        confirmAlert.setHeaderText("Delete Item");
        confirmAlert.setContentText("Are you sure you want to delete '" + selectedItem.getItemName() + "'?");

        if (confirmAlert.showAndWait().get() == ButtonType.OK) {
            try {
                boolean deleted = DatabaseHelper.deleteInventoryItem(selectedItem.getItemId());
                if (deleted) {
                    showAlert("Success", "Item deleted successfully!", Alert.AlertType.INFORMATION);
                    clearForm();
                    loadInventoryData();
                    loadDashboardData();
                } else {
                    showAlert("Error", "Item not found or could not be deleted!", Alert.AlertType.ERROR);
                }
            } catch (Exception e) {
                showAlert("Error", "Failed to delete item: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    @FXML
    private void handleClearForm() {
        clearForm();
    }

    @FXML
    private void handleRefreshData() {
        loadInventoryData();
        loadDashboardData();
        showAlert("Success", "Data refreshed successfully!", Alert.AlertType.INFORMATION);
    }

    // Helper methods for CRUD

    private void editItem(DatabaseHelper.InventoryItem item) {
        selectedItem = item;
        populateForm(item);
        inventoryTable.getSelectionModel().select(item);
    }

    private void deleteItem(DatabaseHelper.InventoryItem item) {
        selectedItem = item;
        handleDeleteItem();
    }

    private void populateForm(DatabaseHelper.InventoryItem item) {
        if (item == null)
            return;

        if (txtItemName != null)
            txtItemName.setText(item.getItemName());
        if (txtCategory != null)
            txtCategory.setText(item.getCategory());
        if (txtQuantity != null)
            txtQuantity.setText(String.valueOf(item.getQuantity()));
        if (txtLocation != null)
            txtLocation.setText(item.getLocation());

        if (dateExpiration != null && item.getExpirationDate() != null && !item.getExpirationDate().isEmpty()) {
            try {
                dateExpiration.setValue(java.time.LocalDate.parse(item.getExpirationDate()));
            } catch (Exception e) {
                // If date parsing fails, leave it empty
                dateExpiration.setValue(null);
            }
        }

        validateForm();
    }

    private void clearForm() {
        selectedItem = null;

        if (txtItemName != null)
            txtItemName.clear();
        if (txtCategory != null)
            txtCategory.clear();
        if (txtQuantity != null)
            txtQuantity.clear();
        if (txtLocation != null)
            txtLocation.clear();
        if (dateExpiration != null)
            dateExpiration.setValue(null);

        if (inventoryTable != null)
            inventoryTable.getSelectionModel().clearSelection();
        validateForm();
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showSection(String section) {
        paneDashboard.setVisible("dashboard".equals(section));
        paneInventory.setVisible("inventory".equals(section));
        paneManagement.setVisible("management".equals(section));
        paneMap.setVisible("map".equals(section));
        paneReports.setVisible("reports".equals(section));

        // Load data based on the section
        if ("inventory".equals(section)) {
            loadInventoryData();
        } else if ("dashboard".equals(section)) {
            loadDashboardData();
        }
    }

    private void loadDashboardData() {
        try {
            ObservableList<DatabaseHelper.InventoryItem> items = DatabaseHelper.getAllInventoryItems();
            int totalItems = items.size();
            int totalQuantity = items.stream().mapToInt(DatabaseHelper.InventoryItem::getQuantity).sum();

            // Count low stock items (quantity < 10)
            long lowStockCount = items.stream().filter(item -> item.getQuantity() < 10).count();

            // Count items expiring within 30 days (if we had proper date handling)
            long expiringSoonCount = 0; // Placeholder for expiring items logic

            // Update dashboard labels if they exist
            if (lblTotalItems != null) {
                lblTotalItems.setText(String.valueOf(totalItems));
            }
            if (lblTotalQuantity != null) {
                lblTotalQuantity.setText(String.valueOf(totalQuantity));
            }
            if (lblLowStock != null) {
                lblLowStock.setText(String.valueOf(lowStockCount));
            }
            if (lblExpiringSoonCount != null) {
                lblExpiringSoonCount.setText(String.valueOf(expiringSoonCount));
            }

            System.out.println("📊 Dashboard Updated - Items: " + totalItems +
                    ", Total Qty: " + totalQuantity +
                    ", Low Stock: " + lowStockCount);

        } catch (Exception e) {
            System.err.println("❌ Error loading dashboard data: " + e.getMessage());
            if (lblTotalItems != null)
                lblTotalItems.setText("Error");
        }
    }

    private void loadInventoryData() {
        try {
            ObservableList<DatabaseHelper.InventoryItem> items = DatabaseHelper.getAllInventoryItems();

            // Update the table if it exists
            if (inventoryTable != null) {
                inventoryTable.setItems(items);
                System.out.println("📋 Inventory table updated with " + items.size() + " items");
            }

            // Also update the text area for fallback display
            if (inventoryTextArea != null) {
                StringBuilder inventoryText = new StringBuilder();
                inventoryText.append("📦 INVENTORY DATA FROM DATABASE:\n");
                inventoryText.append("=================================\n\n");

                if (items.isEmpty()) {
                    inventoryText.append("No items found in inventory.\n");
                } else {
                    for (DatabaseHelper.InventoryItem item : items) {
                        inventoryText.append(String.format(
                                "ID: %d | Name: %s | Category: %s | Qty: %d | Location: %s | Expiry: %s\n",
                                item.getItemId(),
                                item.getItemName(),
                                item.getCategory(),
                                item.getQuantity(),
                                item.getLocation(),
                                item.getExpirationDate() != null ? item.getExpirationDate() : "No expiry"));
                    }
                }
                inventoryTextArea.setText(inventoryText.toString());
            }

        } catch (Exception e) {
            String errorMsg = "❌ Error loading inventory data: " + e.getMessage();
            System.err.println(errorMsg);

            if (inventoryTextArea != null) {
                inventoryTextArea.setText(errorMsg);
            }
            if (inventoryTable != null) {
                inventoryTable.getItems().clear();
            }
        }
    }

    @FXML
    private void handleDashboard() {
        showSection("dashboard");
    }

    @FXML
    private void handleInventory() {
        showSection("inventory");
    }

    @FXML
    private void handleManagement() {
        showSection("management");
    }

    @FXML
    private void handleMap() {
        showSection("map");
    }

    @FXML
    private void handleReports() {
        showSection("reports");
    }

    @FXML
    private void handleDashboardLabel() {
        showSection("dashboard");
    }

    @FXML
    private void handleInventoryLabel() {
        showSection("inventory");
    }

    @FXML
    private void handleManagementLabel() {
        showSection("management");
    }

    @FXML
    private void handleMapLabel() {
        showSection("map");
    }

    @FXML
    private void handleReportsLabel() {
        showSection("reports");
    }

    @FXML
    private void handleLogout() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/example/nakakainis2/landingPage.fxml"));
            Stage stage = (Stage) imgLogo.getScene().getWindow();
            stage.setScene(new Scene(root, 1500, 780));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}