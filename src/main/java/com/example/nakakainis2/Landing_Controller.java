package com.example.nakakainis2;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class Landing_Controller {
    @FXML
    private AnchorPane btnAdminLogin;

    @FXML
    private Button btnDashboard2;

    @FXML
    private Button btnDashboard;

    @FXML
    private void handleDashboard(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/example/nakakainis2/userDashboard.fxml"));
            Stage stage = (Stage) btnDashboard.getScene().getWindow();
            stage.setScene(new Scene(root, 1500, 780));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAdminLogin(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/example/nakakainis2/adminLogin.fxml"));
            Stage stage = (Stage) btnDashboard2.getScene().getWindow();
            stage.setScene(new Scene(root, 1500, 780));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
