package com.example.nakakainis2;

import java.sql.Connection;

/**
 * Test class to help set up IntelliJ Database connection
 * Run this class to get the exact connection details for IntelliJ
 */
public class DatabaseConnectionTest {
    
    public static void main(String[] args) {
        System.out.println("=== Database Connection Setup Helper ===\n");
        
        // Print IntelliJ connection details
        DatabaseHelper.printIntelliJConnectionDetails();
        
        // Test the connection
        System.out.println("Testing database connection...");
        if (DatabaseHelper.testConnection()) {
            System.out.println("✅ Database connection successful!");
            
            // Try to get some sample data
            try (Connection conn = DatabaseHelper.getConnection()) {
                System.out.println("✅ Connection established successfully");
                System.out.println("Database URL: " + conn.getMetaData().getURL());
                System.out.println("Database Product: " + conn.getMetaData().getDatabaseProductName());
                System.out.println("Database Version: " + conn.getMetaData().getDatabaseProductVersion());
            } catch (Exception e) {
                System.err.println("❌ Error getting connection details: " + e.getMessage());
            }
        } else {
            System.err.println("❌ Database connection failed!");
            System.err.println("Check your database.properties file or network connection");
        }
        
        System.out.println("\n=== Next Steps ===");
        System.out.println("1. Copy the connection details above");
        System.out.println("2. Set them up in IntelliJ Database tab");
        System.out.println("3. You can then run SQL queries directly in IntelliJ");
        System.out.println("4. Your Java code will continue to work the same way");
    }
}
