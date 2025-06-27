package com.example.nakakainis2;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AdminLogin_Controller {

    @FXML
    private Button btnLogin;

    @FXML
    private Button btnBack;

    @FXML
    private Label lblUsername;

    @FXML
    private Label lblPassword;

    @FXML
    private TextField tfUsername;

    @FXML
    private PasswordField tfPassword;

    @FXML
    private Label lblError;

    @FXML
    private void handleLogin() {
        String username = tfUsername.getText();
        String password = tfPassword.getText();
        if (username == null || password == null || username.isEmpty() || password.isEmpty()) {
            lblError.setText("Please enter username and password");
            return;
        }
        try (Connection conn = DatabaseHelper.getConnection()) {
            String sql = "SELECT password_hash FROM admin_accounts WHERE username = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, username);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        String dbHash = rs.getString("password_hash");
                        String inputHash = getSHA256(password);
                        if (dbHash != null && dbHash.equalsIgnoreCase(inputHash)) {
                            Parent root = FXMLLoader
                                    .load(getClass().getResource("/com/example/nakakainis2/adminDashboard.fxml"));
                            Stage stage = (Stage) btnLogin.getScene().getWindow();
                            stage.setScene(new Scene(root, 1500, 780));
                            return;
                        }
                    }
                }
            }
            lblError.setText("Invalid username or password");
        } catch (Exception e) {
            lblError.setText("Database error");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleBack() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/example/nakakainis2/landingPage.fxml"));
            Stage stage = (Stage) btnBack.getScene().getWindow();
            stage.setScene(new Scene(root, 1500, 780));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getSHA256(String input) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(input.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1)
                    hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            return null;
        }
    }
}