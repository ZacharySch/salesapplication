// This class represents a Product in the sales application.

public class Product {
    private String name;
    private double price;

    // Constructor to initialize the product with a name and price.
    public Product(String name, double price) {
        this.name = name;
        this.price = price;
    }

    // Getter method to retrieve the product's name.
    public String getName() {
        return name;
    }

    // Getter method to retrieve the product's price.
    public double getPrice() {
        return price;
    }
}
