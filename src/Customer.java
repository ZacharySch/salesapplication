// This class represents a Customer in the sales application.

public class Customer {
    private String name;
    private String email;

    // Constructor to initialize the customer with a name and email.
    public Customer(String name, String email) {
        this.name = name;
        this.email = email;
    }

    // Getter method to retrieve the customer's name.
    public String getName() {
        return name;
    }

    // Getter method to retrieve the customer's email.
    public String getEmail() {
        return email;
    }
}
