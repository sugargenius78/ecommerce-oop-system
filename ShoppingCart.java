import java.util.ArrayList;

public class ShoppingCart {
    private static ArrayList<Product> items = new ArrayList<>();

    public static void addItem(Product p) {
        items.add(p);
    }

    public static void removeItem(int index) {
        if (index >= 0 && index < items.size()) {
            items.remove(index);
        }
    }

    public static ArrayList<Product> getItems() {
        return items;
    }

    public static double getTotalPrice() {
        double total = 0;
        for (Product p : items) {
            total += p.getPrice();
        }
        return total;
    }

    public static void clearCart() {
        items.clear();
    }
}