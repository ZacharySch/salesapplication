import java.sql.*;
import java.util.List;

public class DatabaseManager {
    private static final String URL = "jdbc:mysql://localhost:3306/salesapp_db";
    private static final String USER = "root";
    private static final String PASSWORD = "Quadmobe13";

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void createCustomerTable() {
        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS Customer (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "name VARCHAR(255) NOT NULL," +
                    "email VARCHAR(255) NOT NULL)");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void createOrderTable() {
        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {
    
            // Create Customer table first (if not exists)
            createCustomerTable();
    
            // Create SalesOrder table with a foreign key reference to Customer table
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS SalesOrder (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "customer_id INT NOT NULL," +
                    "total_price DOUBLE NOT NULL," +
                    "FOREIGN KEY (customer_id) REFERENCES Customer(id))");
    
            // Create OrderProduct table (if not exists)
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS OrderProduct (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "order_id INT NOT NULL," +
                    "product_name VARCHAR(255) NOT NULL," +
                    "product_price DOUBLE NOT NULL," +
                    "FOREIGN KEY (order_id) REFERENCES SalesOrder(id))");
    
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void addOrder(Order order) {
        try (Connection connection = getConnection()) {
            // Insert Order information
            String insertOrderSQL = "INSERT INTO SalesOrder (customer_id, total_price) VALUES (?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(insertOrderSQL, Statement.RETURN_GENERATED_KEYS)) {
                int customerId = getCustomerId(order.getCustomer());
    
                if (customerId == -1) {
                    System.out.println("Error adding order. Customer not found. Customer: " + order.getCustomer() +
                            ", Total: " + order.calculateTotal());
                    return;
                }
    
                preparedStatement.setInt(1, customerId);
                preparedStatement.setDouble(2, order.calculateTotal());
    
                int affectedRows = preparedStatement.executeUpdate();
    
                if (affectedRows > 0) {
                    ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        int orderId = generatedKeys.getInt(1);
    
                        // Insert OrderProduct information
                        addProductsToOrder(orderId, order.getProducts());
    
                        SalesLogger.logOrderAdded(orderId);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error adding order. Customer: " + order.getCustomer() + ", Total: " + order.calculateTotal());
        }
    }
    
    

    private static void addProductsToOrder(int orderId, List<Product> products) {
        try (Connection connection = getConnection()) {
            String insertProductSQL = "INSERT INTO OrderProduct (order_id, product_name, product_price) VALUES (?, ?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(insertProductSQL)) {
                for (Product product : products) {
                    preparedStatement.setInt(1, orderId);
                    preparedStatement.setString(2, product.getName());
                    preparedStatement.setDouble(3, product.getPrice());
                    preparedStatement.addBatch();
                }
                preparedStatement.executeBatch();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static int getCustomerId(Customer customer) {
        try (Connection connection = getConnection()) {
            String selectCustomerSQL = "SELECT id FROM Customer WHERE LOWER(name) = LOWER(?) AND LOWER(email) = LOWER(?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(selectCustomerSQL)) {
                preparedStatement.setString(1, customer.getName());
                preparedStatement.setString(2, customer.getEmail());
    
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    return resultSet.getInt("id");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
    

    public static void displayCustomers() {
        try (Connection connection = getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM Customer")) {
            System.out.println("Customers in the database:");
            while (resultSet.next()) {
                int customerId = resultSet.getInt("id");
                String name = resultSet.getString("name");
                String email = resultSet.getString("email");

                System.out.println("ID: " + customerId + ", Name: " + name + ", Email: " + email);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
