public class Product {
    private int id;
    private String name;
    private String description;
    private double price;
    private int stock;

    public Product(int id, String name, String description, double price, int stock) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.stock = stock;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }
    
    public int getId() {
        return id;
    }

    // controls what shows up in the list (Name - Price - Description)
    @Override
    public String toString() {
        return name + " - $" + price + " : " + description;
    }
}