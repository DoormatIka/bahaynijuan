package com.example.nakakainis2;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * Modern DatabaseHelper using configuration files and better practices.
 * This approach is more suitable for Maven projects and IDE database integration.
 * 
 * To use with your IDE's database tab:
 * 1. Use the connection details from database.properties
 * 2. Or override with system properties: -Ddb.url=... -Ddb.username=... -Ddb.password=...
 */
public class DatabaseHelper {
    private static final Logger LOGGER = Logger.getLogger(DatabaseHelper.class.getName());
    private static volatile DatabaseHelper instance;
    private static Properties dbProperties;

    // Initialize database configuration
    static {
        try {
            initializeDatabaseConfiguration();
            // Test the JDBC driver
            Class.forName(dbProperties.getProperty("db.driver", "com.mysql.cj.jdbc.Driver"));
            LOGGER.info("✅ MySQL JDBC Driver loaded successfully");
        } catch (ClassNotFoundException e) {
            LOGGER.severe("❌ MySQL JDBC Driver not found: " + e.getMessage());
            throw new RuntimeException("Database driver initialization failed", e);
        } catch (Exception e) {
            LOGGER.severe("❌ Database configuration initialization failed: " + e.getMessage());
            throw new RuntimeException("Database initialization failed", e);
        }
    }

    private DatabaseHelper() {
        // Singleton pattern
    }

    /**
     * Get the singleton instance of DatabaseHelper
     */
    public static DatabaseHelper getInstance() {
        if (instance == null) {
            synchronized (DatabaseHelper.class) {
                if (instance == null) {
                    instance = new DatabaseHelper();
                }
            }
        }
        return instance;
    }

    /**
     * Initialize database configuration from properties file
     */
    private static void initializeDatabaseConfiguration() {
        try {
            dbProperties = loadDatabaseProperties();
            LOGGER.info("✅ Database configuration initialized successfully");
        } catch (Exception e) {
            LOGGER.severe("❌ Failed to initialize database configuration: " + e.getMessage());
            throw new RuntimeException("Database configuration initialization failed", e);
        }
    }

    /**
     * Load database properties from file, with fallback to system properties
     */
    private static Properties loadDatabaseProperties() throws IOException {
        Properties props = new Properties();
        
        // Try to load from resources
        try (InputStream is = DatabaseHelper.class.getClassLoader().getResourceAsStream("database.properties")) {
            if (is != null) {
                props.load(is);
                LOGGER.info("Loaded database configuration from database.properties");
            } else {
                LOGGER.warning("database.properties not found, using default configuration");
                setDefaultProperties(props);
            }
        }
        
        // Override with system properties if they exist (useful for IDE integration)
        overrideWithSystemProperties(props);
        
        return props;
    }

    /**
     * Set default database properties if no config file is found
     */
    private static void setDefaultProperties(Properties props) {
        props.setProperty("db.url", "jdbc:mysql://mysql-3d45c792-jlallas384-c83c.f.aivencloud.com:27554/bahaynijuan?sslMode=REQUIRED");
        props.setProperty("db.username", "avnadmin");
        props.setProperty("db.password", "AVNS_KPhhTV5clfJYiLENeYN");
        props.setProperty("db.driver", "com.mysql.cj.jdbc.Driver");
    }

    /**
     * Allow system properties to override configuration (useful for IDE database integration)
     * Usage: -Ddb.url=jdbc:mysql://localhost:3306/mydb -Ddb.username=user -Ddb.password=pass
     */
    private static void overrideWithSystemProperties(Properties props) {
        String[] propertyKeys = {"db.url", "db.username", "db.password", "db.driver"};
        
        for (String key : propertyKeys) {
            String systemValue = System.getProperty(key);
            if (systemValue != null) {
                props.setProperty(key, systemValue);
                LOGGER.info("Using system property for: " + key);
            }
        }
    }

    /**
     * Get a database connection using the configured properties
     */
    public static Connection getConnection() throws SQLException {
        if (dbProperties == null) {
            throw new SQLException("Database properties not initialized");
        }
        
        String url = dbProperties.getProperty("db.url");
        String username = dbProperties.getProperty("db.username");
        String password = dbProperties.getProperty("db.password");
        
        return DriverManager.getConnection(url, username, password);
    }

    /**
     * Get database connection details for IDE integration
     */
    public static Properties getDatabaseProperties() {
        return new Properties(dbProperties); // Return a copy to prevent modification
    }

    /**
     * Get database URL for IDE database tab setup
     */
    public static String getDatabaseUrl() {
        return dbProperties.getProperty("db.url");
    }

    /**
     * Get database username for IDE database tab setup
     */
    public static String getDatabaseUsername() {
        return dbProperties.getProperty("db.username");
    }

    /**
     * Get database password for IDE database tab setup
     */
    public static String getDatabasePassword() {
        return dbProperties.getProperty("db.password");
    }

    /**
     * Print connection details for IntelliJ IDEA Database tab setup
     * Call this method to get the exact details you need for IntelliJ
     */
    public static void printIntelliJConnectionDetails() {
        System.out.println("\n=== IntelliJ IDEA Database Connection Details ===");
        System.out.println("1. Open Database tab: View → Tool Windows → Database");
        System.out.println("2. Click '+' → Data Source → MySQL");
        System.out.println("3. Use these connection details:");
        System.out.println();
        
        String url = getDatabaseUrl();
        // Parse the URL to extract components
        String host = "mysql-3d45c792-jlallas384-c83c.f.aivencloud.com";
        String port = "27554";
        String database = "bahaynijuan";
        
        System.out.println("Host: " + host);
        System.out.println("Port: " + port);
        System.out.println("Database: " + database);
        System.out.println("User: " + getDatabaseUsername());
        System.out.println("Password: " + getDatabasePassword());
        System.out.println();
        System.out.println("Full URL: " + url);
        System.out.println();
        System.out.println("4. Click 'Test Connection' to verify");
        System.out.println("5. Click 'Apply' and 'OK'");
        System.out.println("==============================================\n");
    }

    /**
     * Alternative connection method that can use IntelliJ's connection if available
     * This allows your code to work with both your properties and IntelliJ's settings
     */
    public static Connection getConnectionWithIntelliJFallback() throws SQLException {
        // First try the standard connection
        try {
            return getConnection();
        } catch (SQLException e) {
            LOGGER.warning("Standard connection failed, trying IntelliJ datasource: " + e.getMessage());
            
            // If that fails, you could implement IntelliJ DataSource integration here
            // For now, just re-throw the exception
            throw e;
        }
    }

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

        // Getters
        public int getItemId() { return itemId; }
        public String getItemName() { return itemName; }
        public String getCategory() { return category; }
        public int getQuantity() { return quantity; }
        public String getLocation() { return location; }
        public String getExpirationDate() { return expirationDate; }

        // Setters
        public void setItemId(int itemId) { this.itemId = itemId; }
        public void setItemName(String itemName) { this.itemName = itemName; }
        public void setCategory(String category) { this.category = category; }
        public void setQuantity(int quantity) { this.quantity = quantity; }
        public void setLocation(String location) { this.location = location; }
        public void setExpirationDate(String expirationDate) { this.expirationDate = expirationDate; }

        @Override
        public String toString() {
            return String.format("InventoryItem{id=%d, name='%s', category='%s', quantity=%d, location='%s', expiration='%s'}",
                    itemId, itemName, category, quantity, location, expirationDate);
        }
    }

    // --- Inventory Methods ---
    
    /**
     * Update an inventory item by ID
     */
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
            
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("No item found with ID: " + itemId);
            }
            
            LOGGER.info("Updated inventory item with ID: " + itemId);
        }
    }

    /**
     * Get all inventory items
     */
    public static ObservableList<InventoryItem> getAllInventoryItems() throws SQLException {
        ObservableList<InventoryItem> items = FXCollections.observableArrayList();
        String sql = "SELECT item_id, item_name, category, quantity, location, expiration_date FROM inventory ORDER BY item_name";
        
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
                        rs.getString("expiration_date")
                ));
            }
        }
        
        LOGGER.info("Retrieved " + items.size() + " inventory items");
        return items;
    }

    /**
     * Add a new inventory item
     */
    public static int addInventoryItem(InventoryItem item) throws SQLException {
        String sql = "INSERT INTO inventory (item_name, category, quantity, location, expiration_date) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
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
            
            stmt.executeUpdate();
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int newId = generatedKeys.getInt(1);
                    LOGGER.info("Added new inventory item with ID: " + newId);
                    return newId;
                } else {
                    throw new SQLException("Creating inventory item failed, no ID obtained");
                }
            }
        }
    }

    /**
     * Delete an inventory item by ID
     */
    public static boolean deleteInventoryItem(int itemId) throws SQLException {
        String sql = "DELETE FROM inventory WHERE item_id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, itemId);
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                LOGGER.info("Deleted inventory item with ID: " + itemId);
                return true;
            } else {
                LOGGER.warning("No item found with ID: " + itemId);
                return false;
            }
        }
    }

    /**
     * Test database connectivity
     */
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn.isValid(5);
        } catch (SQLException e) {
            LOGGER.severe("Database connection test failed: " + e.getMessage());
            return false;
        }
    }

    /**
     * Print current database configuration (useful for debugging)
     */
    public static void printDatabaseConfig() {
        System.out.println("=== Database Configuration ===");
        System.out.println("URL: " + getDatabaseUrl());
        System.out.println("Username: " + getDatabaseUsername());
        System.out.println("Driver: " + dbProperties.getProperty("db.driver"));
        System.out.println("============================");
    }
}
