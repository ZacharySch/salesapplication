import java.util.logging.Level;

public class SalesApplication {
    public static void main(String[] args) {
        try {
            // Initialize the database and create the Customer table if not exists
            DatabaseManager.createCustomerTable();

            // Creating a customer
            Customer customer = new Customer("John Doe", "john.doe@example.com");

            // Logging a warning message
            SalesLogger.logWarning("Order placed for customer: " + customer.getName());

            // Calculating and displaying the total order price
            double total = calculateOrderTotal(customer);
            System.out.println("Total Order Price: $" + total);

            // Logging the total order price
            SalesLogger.logWarning("Total Order Price: $" + total);

            // Adding the customer to the database
            DatabaseManager.addCustomer(customer);

            // Retrieving and displaying customers from the database
            DatabaseManager.displayCustomers();
        } catch (Exception e) {
            // Logging an exception if one occurs
            SalesLogger.logException(Level.SEVERE, "An exception occurred", e);
        }
    }

    private static double calculateOrderTotal(Customer customer) {
        // Your existing logic for calculating the order total
        // You can add database interactions related to order calculations here if needed
        return 49.98; // Placeholder value, replace it with your actual calculation
    }
}
