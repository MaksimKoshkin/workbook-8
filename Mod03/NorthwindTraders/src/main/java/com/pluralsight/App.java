package com.pluralsight;

import java.sql.*;
import java.util.Scanner;

public class App {
    public static void main(String[] args) {
        String url = "jdbc:mysql://127.0.0.1:3306/northwind";
        String user = "root";
        String password = "yearup";

        Scanner scanner = new Scanner(System.in);
        int choice;

        do {
            System.out.println("What do you want to do?");
            System.out.println("1) Display all products");
            System.out.println("2) Display all customers");
            System.out.println("3) Display all categories");
            System.out.println("0) Exit");
            System.out.print("Select an option: ");
            choice = scanner.nextInt();
            scanner.nextLine(); // clear newline

            switch (choice) {
                case 1:
                    displayAllProducts(url, user, password);
                    break;
                case 2:
                    displayAllCustomers(url, user, password);
                    break;
                case 3:
                    displayAllCategoriesAndProductsById(url, user, password, scanner);
                    break;
                case 0:
                    System.out.println("Exiting application.");
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }

            System.out.println(); // spacing

        } while (choice != 0);

        scanner.close();
    }

    private static void displayAllProducts(String url, String user, String password) {
        String query = "SELECT * FROM Products";

        try (
                Connection connection = DriverManager.getConnection(url, user, password);
                PreparedStatement statement = connection.prepareStatement(query);
                ResultSet results = statement.executeQuery()
        ) {
            System.out.printf("%-10s %-35s %-12s %-15s%n", "ProductId", "ProductName", "UnitPrice", "UnitsInStock");
            System.out.println("------------------------------------------------------------------------");

            while (results.next()) {
                int productId = results.getInt("ProductID");
                String productName = results.getString("ProductName");
                double unitPrice = results.getDouble("UnitPrice");
                int unitsInStock = results.getInt("UnitsInStock");

                System.out.printf("%-10d %-35s %-12.2f %-15d%n", productId, productName, unitPrice, unitsInStock);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void displayAllCustomers(String url, String user, String password) {
        String query = "SELECT ContactName, CompanyName, City, Country, Phone FROM Customers ORDER BY Country";

        try (
                Connection connection = DriverManager.getConnection(url, user, password);
                PreparedStatement statement = connection.prepareStatement(query);
                ResultSet results = statement.executeQuery()
        ) {
            System.out.printf("%-30s %-35s %-20s %-20s %-20s%n", "ContactName", "CompanyName", "City", "Country", "Phone");
            System.out.println("---------------------------------------------------------------------------------------------------------------");

            while (results.next()) {
                String contactName = results.getString("ContactName");
                String companyName = results.getString("CompanyName");
                String city = results.getString("City");
                String country = results.getString("Country");
                String phone = results.getString("Phone");

                System.out.printf("%-30s %-35s %-20s %-20s %-20s%n", contactName, companyName, city, country, phone);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void displayAllCategoriesAndProductsById(String url, String user, String password, Scanner scanner) {
        String categoryQuery = "SELECT CategoryID, CategoryName FROM Categories ORDER BY CategoryID";

        try (
                Connection connection = DriverManager.getConnection(url, user, password);
                PreparedStatement categoryStatement = connection.prepareStatement(categoryQuery);
                ResultSet categoryResults = categoryStatement.executeQuery()
        ) {
            System.out.printf("%-12s %-35s%n", "CategoryID", "CategoryName");
            System.out.println("-----------------------------------------------");

            while (categoryResults.next()) {
                int categoryId = categoryResults.getInt("CategoryID");
                String categoryName = categoryResults.getString("CategoryName");
                System.out.printf("%-12d %-35s%n", categoryId, categoryName);
            }

            System.out.print("\nEnter Category ID to view its products: ");
            int selectedCategoryId = scanner.nextInt();
            scanner.nextLine(); // clear newline

            displayProductsByCategoryId(url, user, password, selectedCategoryId);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void displayProductsByCategoryId(String url, String user, String password, int categoryId) {
        String productQuery = "SELECT ProductID, ProductName, UnitPrice, UnitsInStock FROM Products WHERE CategoryID = ?";

        try (
                Connection connection = DriverManager.getConnection(url, user, password);
                PreparedStatement statement = connection.prepareStatement(productQuery)
        ) {
            statement.setInt(1, categoryId);

            try (ResultSet results = statement.executeQuery()) {
                System.out.printf("\n%-10s %-35s %-12s %-15s%n", "ProductId", "ProductName", "UnitPrice", "UnitsInStock");
                System.out.println("------------------------------------------------------------------------");

                boolean found = false;

                while (results.next()) {
                    found = true;
                    int productId = results.getInt("ProductID");
                    String productName = results.getString("ProductName");
                    double unitPrice = results.getDouble("UnitPrice");
                    int unitsInStock = results.getInt("UnitsInStock");

                    System.out.printf("%-10d %-35s %-12.2f %-15d%n", productId, productName, unitPrice, unitsInStock);
                }

                if (!found) {
                    System.out.println("No products found for the selected category.");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
