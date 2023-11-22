// This class represents an Order in the sales application.

import java.util.ArrayList;
import java.util.List;

public class Order {
    private Customer customer;
    private List<Product> products;

    // Constructor to initialize an order for a specific customer.
    public Order(Customer customer) {
        this.customer = customer;
        this.products = new ArrayList<>();
    }

    // Method to add a product to the order.
    public void addProduct(Product product) {
        products.add(product);
    }

    // Method to calculate the total price of the order.
    public double calculateTotal() {
        double total = 0;
        for (Product product : products) {
            total += product.getPrice();
        }
        return total;
    }
}
