package com.example.nakakainis2;

import javafx.collections.FXCollections;
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

public class UserDashboard_Controller {

    @FXML
    private Label navDashboard;

    @FXML
    private Label navInventory;

    @FXML
    private Label navDonate;

    @FXML
    private Label navMap;

    @FXML
    private Button btnLogout;

    @FXML
    private ImageView imgLogo;

    @FXML
    private Label lblDashboard;

    @FXML
    private AnchorPane cardTotalGoods;

    @FXML
    private AnchorPane cardExpiringSoon;

    @FXML
    private AnchorPane cardRecentDonations;

    @FXML
    private AnchorPane paneDashboard;

    @FXML
    private AnchorPane paneInventory;

    @FXML
    private AnchorPane paneDonate;

    @FXML
    private AnchorPane paneMap;

    // Read-only UI components for users
    @FXML
    private TableView<DatabaseHelper.InventoryItem> tableInventory;

    @FXML
    private TableColumn<DatabaseHelper.InventoryItem, Integer> colId;
    @FXML
    private TableColumn<DatabaseHelper.InventoryItem, String> colName;
    @FXML
    private TableColumn<DatabaseHelper.InventoryItem, String> colCategory;
    @FXML
    private TableColumn<DatabaseHelper.InventoryItem, Integer> colQuantity;
    @FXML
    private TableColumn<DatabaseHelper.InventoryItem, String> colUnit;
    @FXML
    private TableColumn<DatabaseHelper.InventoryItem, String> colExpiryDate;
    @FXML
    private TableColumn<DatabaseHelper.InventoryItem, String> colLocation;
    @FXML
    private TableColumn<DatabaseHelper.InventoryItem, String> colStatus;

    // Dashboard summary labels for users
    @FXML
    private Label lblTotalGoods;
    @FXML
    private Label lblExpiringSoon;
    @FXML
    private Label lblRecentDonations;

    // Fallback text area for inventory display
    @FXML
    private TextArea userInventoryTextArea;

    // Search/Filter functionality
    @FXML
    private TextField txtSearch;
    @FXML
    private Button btnRefresh;

    @FXML
    private void initialize() {
        showSection("dashboard");
        initializeUserInventoryTable();
        setupUserSearch();
        loadUserDashboardData();
        
        navDashboard.setOnMouseClicked(event -> showSection("dashboard"));
        navInventory.setOnMouseClicked(event -> showSection("inventory"));
        navDonate.setOnMouseClicked(event -> showSection("donate"));
        navMap.setOnMouseClicked(event -> showSection("map"));
        btnLogout.setOnAction(event -> handleLogout());
        
        // Set up search and refresh handlers
        if (btnRefresh != null) {
            btnRefresh.setOnAction(event -> handleUserRefresh());
        }
        if (txtSearch != null) {
            txtSearch.setOnKeyReleased(event -> handleUserSearch());
        }
    }

    private void initializeUserInventoryTable() {
        if (tableInventory == null) return;

        // Set up table columns for read-only view
        colId.setCellValueFactory(new PropertyValueFactory<>("itemId"));
        colName.setCellValueFactory(new PropertyValueFactory<>("itemName"));
        colCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
        colQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colUnit.setCellValueFactory(new PropertyValueFactory<>("unit"));
        colExpiryDate.setCellValueFactory(new PropertyValueFactory<>("expirationDate"));
        colLocation.setCellValueFactory(new PropertyValueFactory<>("location"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Make table read-only
        tableInventory.setEditable(false);
        tableInventory.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    }

    private void setupUserSearch() {
        if (txtSearch != null) {
            txtSearch.setPromptText("Search by item name or category...");
        }
    }

    @FXML
    private void handleUserSearch() {
        String searchTerm = txtSearch != null ? txtSearch.getText().trim().toLowerCase() : "";
        loadUserInventoryData(searchTerm);
    }

    @FXML
    private void handleUserRefresh() {
        if (txtSearch != null) txtSearch.clear();
        loadUserInventoryData("");
        loadUserDashboardData();
    }

    private void loadUserDashboardData() {
        try {
            ObservableList<DatabaseHelper.InventoryItem> items = DatabaseHelper.getAllInventoryItems();
            int totalItems = items.size();
            int totalQuantity = items.stream().mapToInt(DatabaseHelper.InventoryItem::getQuantity).sum();
            
            // Count low stock items (quantity < 10)
            long lowStockCount = items.stream().filter(item -> item.getQuantity() < 10).count();
            
            // Count items expiring within 30 days (placeholder)
            long expiringSoonCount = 0;

            // Update dashboard labels if they exist
            if (lblTotalGoods != null) {
                lblTotalGoods.setText(String.valueOf(totalItems));
            }
            if (lblExpiringSoon != null) {
                lblExpiringSoon.setText(String.valueOf(expiringSoonCount));
            }
            if (lblRecentDonations != null) {
                lblRecentDonations.setText(String.valueOf(totalQuantity));
            }

            System.out.println("📊 User Dashboard Updated - Items: " + totalItems + 
                             ", Total Qty: " + totalQuantity + 
                             ", Low Stock: " + lowStockCount);
                             
        } catch (Exception e) {
            System.err.println("❌ Error loading user dashboard data: " + e.getMessage());
        }
    }

    private void loadUserInventoryData(String searchFilter) {
        try {
            ObservableList<DatabaseHelper.InventoryItem> allItems = DatabaseHelper.getAllInventoryItems();
            ObservableList<DatabaseHelper.InventoryItem> filteredItems = FXCollections.observableArrayList();

            // Apply search filter if provided
            for (DatabaseHelper.InventoryItem item : allItems) {
                if (searchFilter.isEmpty() || 
                    item.getItemName().toLowerCase().contains(searchFilter) ||
                    item.getCategory().toLowerCase().contains(searchFilter)) {
                    filteredItems.add(item);
                }
            }
            
            // Update the table if it exists
            if (tableInventory != null) {
                tableInventory.setItems(filteredItems);
                System.out.println("📋 User inventory table updated with " + filteredItems.size() + " items");
            }
            
            // Also update the text area for fallback display
            if (userInventoryTextArea != null) {
                StringBuilder inventoryText = new StringBuilder();
                inventoryText.append("📦 AVAILABLE INVENTORY ITEMS:\n");
                inventoryText.append("===============================\n\n");

                if (filteredItems.isEmpty()) {
                    inventoryText.append("No items found matching your search.\n");
                } else {
                    for (DatabaseHelper.InventoryItem item : filteredItems) {
                        inventoryText.append(String.format(
                                "📦 %s | Category: %s | Available: %d | Location: %s | Expires: %s\n",
                                item.getItemName(),
                                item.getCategory(),
                                item.getQuantity(),
                                item.getLocation(),
                                item.getExpirationDate() != null ? item.getExpirationDate() : "No expiry"));
                    }
                }
                userInventoryTextArea.setText(inventoryText.toString());
            }

        } catch (Exception e) {
            String errorMsg = "❌ Error loading inventory data: " + e.getMessage();
            System.err.println(errorMsg);

            if (userInventoryTextArea != null) {
                userInventoryTextArea.setText(errorMsg);
            }
            if (tableInventory != null) {
                tableInventory.getItems().clear();
            }
        }
    }

    private void showSection(String section) {
        // Hide/show panes
        if (paneDashboard != null)
            paneDashboard.setVisible("dashboard".equals(section));
        if (paneInventory != null)
            paneInventory.setVisible("inventory".equals(section));
        if (paneDonate != null)
            paneDonate.setVisible("donate".equals(section));
        if (paneMap != null)
            paneMap.setVisible("map".equals(section));

        // Update navigation label colors (active = white, inactive = green)
        updateNavColors(section);
    }

    private void updateNavColors(String activeSection) {
        // Reset all to green first
        navDashboard.setStyle("-fx-text-fill: #059212; -fx-cursor: hand;");
        navInventory.setStyle("-fx-text-fill: #059212; -fx-cursor: hand;");
        navDonate.setStyle("-fx-text-fill: #059212; -fx-cursor: hand;");
        navMap.setStyle("-fx-text-fill: #059212; -fx-cursor: hand;");

        // Set active section to white
        switch (activeSection) {
            case "dashboard" -> navDashboard.setStyle("-fx-text-fill: WHITE; -fx-cursor: hand;");
            case "inventory" -> navInventory.setStyle("-fx-text-fill: WHITE; -fx-cursor: hand;");
            case "donate" -> navDonate.setStyle("-fx-text-fill: WHITE; -fx-cursor: hand;");
            case "map" -> navMap.setStyle("-fx-text-fill: WHITE; -fx-cursor: hand;");
        }
    }

    private void handleLogout() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/example/nakakainis2/landingPage.fxml"));
            Stage stage = (Stage) btnLogout.getScene().getWindow();
            stage.setScene(new Scene(root, 1500, 780));
        } catch (java.io.IOException e) {
            System.err.println("Error loading landing page: " + e.getMessage());
        }
    }
}