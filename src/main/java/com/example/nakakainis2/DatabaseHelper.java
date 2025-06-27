package com.example.nakakainis2;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class DatabaseHelper {
    // --- MySQL Table Designs ---
    //
    // inventory
    // CREATE TABLE inventory (
    // item_id INT AUTO_INCREMENT PRIMARY KEY,
    // item_name VARCHAR(100) NOT NULL,
    // category VARCHAR(45),
    // location VARCHAR(45),
    // quantity INT NOT NULL DEFAULT 0,
    // expiration_date DATE,
    // date_added DATETIME DEFAULT CURRENT_TIMESTAMP
    // );
    //
    // inventory_transactions
    // CREATE TABLE inventory_transactions (
    // transaction_id INT AUTO_INCREMENT PRIMARY KEY,
    // item_id INT NOT NULL,
    // transaction_type ENUM('add', 'remove', 'edit', 'donation', 'distribution',
    // 'correction') NOT NULL,
    // quantity_change INT NOT NULL,
    // transaction_timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    // FOREIGN KEY (item_id) REFERENCES inventory(item_id) ON DELETE CASCADE
    // );
    //
    // admin_accounts
    // CREATE TABLE admin_accounts (
    // admin_id INT AUTO_INCREMENT PRIMARY KEY,
    // username VARCHAR(50) NOT NULL UNIQUE,
    // password_hash VARCHAR(255) NOT NULL,
    // full_name VARCHAR(100),
    // email VARCHAR(100),
    // created_at DATETIME DEFAULT CURRENT_TIMESTAMP
    // );

    // --- InventoryItem inner class ---
    public static class InventoryItem {
        private int itemId;
        private String itemName;
        private String category;
        private int quantity;
        private String location;
        private String expirationDate;

        public InventoryItem(int itemId, String itemName, String category, int quantity, String location,
                String expirationDate) {
            this.itemId = itemId;
            this.itemName = itemName;
            this.category = category;
            this.quantity = quantity;
            this.location = location;
            this.expirationDate = expirationDate;
        }

        public InventoryItem(String itemName, String category, int quantity, String location, String expirationDate) {
            this(-1, itemName, category, quantity, location, expirationDate);
        }

        public int getItemId() {
            return itemId;
        }

        public void setItemId(int itemId) {
            this.itemId = itemId;
        }

        public String getItemName() {
            return itemName;
        }

        public String getCategory() {
            return category;
        }

        public int getQuantity() {
            return quantity;
        }

        public String getLocation() {
            return location;
        }

        public String getExpirationDate() {
            return expirationDate;
        }
    }

    private static final String DB_URL = "jdbc:mysql://mysql-3d45c792-jlallas384-c83c.f.aivencloud.com:27554/bahaynijuan?sslMode=REQUIRED";
    private static final String USER = "avnadmin";
    private static final String PASS = "AVNS_KPhhTV5clfJYiLENeYN";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, USER, PASS);
    }

    // --- Inventory Methods ---
    public static void updateInventoryItemById(String itemId, InventoryItem item) throws SQLException {
        String sql = "UPDATE inventory SET item_name=?, category=?, quantity=?, location=?, expiration_date=? WHERE item_id=?";
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, item.getItemName());
            stmt.setString(2, item.getCategory());
            stmt.setInt(3, item.getQuantity());
            stmt.setString(4, item.getLocation());
            String expirationDate = item.getExpirationDate();
            if (expirationDate == null || expirationDate.trim().isEmpty()) {
                stmt.setNull(5, java.sql.Types.DATE);
            } else {
                stmt.setString(5, expirationDate);
            }
            stmt.setString(6, itemId);
            stmt.executeUpdate();
        }
    }

    public static ObservableList<InventoryItem> getAllInventoryItems() throws SQLException {
        ObservableList<InventoryItem> items = FXCollections.observableArrayList();
        String sql = "SELECT item_id, item_name, category, quantity, location, expiration_date FROM inventory";
        try (Connection conn = getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                items.add(new InventoryItem(
                        rs.getInt("item_id"),
                        rs.getString("item_name"),
                        rs.getString("category"),
                        rs.getInt("quantity"),
                        rs.getString("location"),
                        rs.getString("expiration_date")));
            }
        }
        return items;
    }
    // ...add more methods for transactions and admin as needed...
}
