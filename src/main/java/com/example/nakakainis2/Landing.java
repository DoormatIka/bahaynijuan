package com.example.nakakainis2;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Landing extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Test database connection on startup
        testDatabaseConnection();

        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/example/nakakainis2/landingPage.fxml"));
            Scene scene = new Scene(root, 1500, 780);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Bahaynijuan - Landing Page");
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void testDatabaseConnection() {
        System.out.println("🔄 Testing database connection...");
        try {
            var connection = DatabaseHelper.getConnection();
            System.out.println("✅ Database connected successfully!");
            connection.close();
        } catch (Exception e) {
            System.err.println("❌ Database connection failed: " + e.getMessage());
            System.err.println("   Application will continue but admin login may not work.");
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}