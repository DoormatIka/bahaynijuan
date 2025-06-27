package com.example.nakakainis2;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

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

    @FXML
    private void initialize() {
        showSection("dashboard");
    }

    private void showSection(String section) {
        paneDashboard.setVisible("dashboard".equals(section));
        paneInventory.setVisible("inventory".equals(section));
        paneManagement.setVisible("management".equals(section));
        paneMap.setVisible("map".equals(section));
        paneReports.setVisible("reports".equals(section));
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