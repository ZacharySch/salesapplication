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

            // Display the main menu
            int choice;
            do {
                displayMainMenu();
                choice = getChoice(scanner);

                switch (choice) {
                    case 1:
                        // Add a new customer
                        Customer newCustomer = createCustomer(scanner);
                        insertCustomerIntoDatabase(newCustomer);
                        break;
                    case 2:
                        // Add a new sale
                        addSale(scanner);
                        break;
                    case 3:
                        // Add both a new customer and a new sale
                        Customer customerForSale = createCustomer(scanner);
                        insertCustomerIntoDatabase(customerForSale);
                        addSale(scanner, customerForSale);
                        break;
                    case 4:
                        // Display customers from the database
                        DatabaseManager.displayCustomers();
                        break;
                    case 5:
                        // Print recent sales
                        printRecentSales();
                        break;
                    case 0:
                        System.out.println("Exiting the application. Thank you!");
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            } while (choice != 0);
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

    private static void displayMainMenu() {
        System.out.println("===== Main Menu =====");
        System.out.println("1. Add a new customer");
        System.out.println("2. Add a new sale");
        System.out.println("3. Add both a new customer and a new sale");
        System.out.println("4. Display customers");
        System.out.println("5. Print recent sales");
        System.out.println("0. Exit");
        System.out.println("==================");
        System.out.print("Enter your choice: ");
    }

    private static int getChoice(Scanner scanner) {
        int choice = -1;
        while (!scanner.hasNextInt()) {
            System.out.println("Invalid input. Please enter a number.");
            scanner.next();
        }
        choice = scanner.nextInt();
        scanner.nextLine();
        return choice;
    }

    private static void addSale(Scanner scanner) {
        // Collecting product information from the user
        List<Product> products = getProductsFromUser(scanner);

        // Creating an order for the customer (a new customer will be created for the sale)
        Order order = new Order(new Customer("", ""));

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
    }

    private static void addSale(Scanner scanner, Customer existingCustomer) {
        // Collecting product information from the user
        List<Product> products = getProductsFromUser(scanner);

        // Creating an order for the existing customer
        Order order = new Order(existingCustomer);

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
    }

    private static void printRecentSales() {
        try (Connection connection = DatabaseManager.getConnection();
             Statement statement = connection.createStatement()) {

            // Retrieve the last 3 orders from the OrderProduct table
            String recentSalesQuery = "SELECT * FROM OrderProduct ORDER BY id DESC LIMIT 3";
            try (ResultSet resultSet = statement.executeQuery(recentSalesQuery)) {
                System.out.println("Recent Sales:");
                while (resultSet.next()) {
                    int orderId = resultSet.getInt("order_id");
                    String productName = resultSet.getString("product_name");
                    double productPrice = resultSet.getDouble("product_price");

                    System.out.println("Order ID: " + orderId + ", Product: " + productName + ", Price: $" + productPrice);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
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
