import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;

public class SalesApplication {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        try {
            // Initialize the database and create the Customer and Order tables if not exists
            DatabaseManager.createCustomerTable();
            DatabaseManager.createOrderTable();

            // Creating a customer
            Customer customer = createCustomer(scanner);

            // Inserting the customer into the database
            insertCustomerIntoDatabase(customer);

            // Logging a warning message
            SalesLogger.logWarning("Order placed for customer: " + customer.getName());

            // Collecting product information from the user
            List<Product> products = getProductsFromUser(scanner);

            // Creating an order for the customer
            Order order = new Order(customer);

            // Adding products to the order
            for (Product product : products) {
                order.addProduct(product);
            }

            // Calculating and displaying the total order price
            double total = order.calculateTotal();
            System.out.println("Total Order Price: $" + total);

            // Logging the total order price
            SalesLogger.logWarning("Total Order Price: $" + total);

            // Adding the customer and order details to the database
            DatabaseManager.addOrder(order);

            // Retrieving and displaying customers from the database
            DatabaseManager.displayCustomers();
        } catch (Exception e) {
            // Logging an exception if one occurs
            SalesLogger.logException(Level.SEVERE, "An exception occurred", e);
        } finally {
            // Closing the scanners to avoid resource leak
            if (scanner != null) {
                scanner.close();
            }
        }
    }

    private static Customer createCustomer(Scanner scanner) {
        // Get customer information from the user
        System.out.print("Enter customer name: ");
        String name = scanner.nextLine();

        System.out.print("Enter customer email: ");
        String email = scanner.nextLine();

        return new Customer(name, email);
    }

    private static void insertCustomerIntoDatabase(Customer customer) {
        try {
            // Inserting the customer into the database
            Connection connection = DatabaseManager.getConnection();
            String insertCustomerSQL = "INSERT INTO Customer (name, email) VALUES (?, ?)";
            
            try (PreparedStatement preparedStatement = connection.prepareStatement(insertCustomerSQL, Statement.RETURN_GENERATED_KEYS)) {
                preparedStatement.setString(1, customer.getName());
                preparedStatement.setString(2, customer.getEmail());

                int affectedRows = preparedStatement.executeUpdate();

                if (affectedRows > 0) {
                    ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        int customerId = generatedKeys.getInt(1);
                        SalesLogger.logWarning("Customer added to the database with ID: " + customerId);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            SalesLogger.logWarning("Error adding customer to the database, please try again. Customer: " + customer.getName());
        }
    }

    private static List<Product> getProductsFromUser(Scanner scanner) {
        List<Product> products = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            // Get product information from the user
            System.out.println("Enter details for Product " + (i + 1) + ":");
            System.out.print("Product name: ");
            String productName = scanner.nextLine();

            // Validate and get product price
            double productPrice = getValidProductPrice(scanner);

            // Create and add the product to the list
            Product product = new Product(productName, productPrice);
            products.add(product);
        }

        return products;
    }

    private static double getValidProductPrice(Scanner scanner) {
        double productPrice = 0;
        boolean isValidInput = false;

        while (!isValidInput) {
            try {
                System.out.print("Product price: $");
                productPrice = Double.parseDouble(scanner.nextLine());

                // Validate that the price is non-negative
                if (productPrice < 0) {
                    throw new NumberFormatException();
                }

                isValidInput = true;
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid non-negative price.");
            }
        }

        return productPrice;
    }
}
