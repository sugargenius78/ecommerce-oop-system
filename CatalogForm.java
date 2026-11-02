import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class CatalogForm extends JFrame {
    private DefaultListModel<Product> listModel;
    private JList<Product> productList;
    private JButton addToCartButton, viewCartButton, trackButton, chatButton, aboutButton;

    public CatalogForm() {
        setTitle("Gold Rim Shop - Catalog");
        setSize(750, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JLabel title = new JLabel("Product Catalog", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 22));
        add(title, BorderLayout.NORTH);

        listModel = new DefaultListModel<>();
        loadProducts();
        productList = new JList<>(listModel);
        add(new JScrollPane(productList), BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel();
        
        addToCartButton = new JButton("Add to Cart");
        viewCartButton = new JButton("View Cart");
        trackButton = new JButton("Track Order");
        chatButton = new JButton("Support Chat");
        aboutButton = new JButton("About Us");

        bottomPanel.add(addToCartButton);
        bottomPanel.add(viewCartButton);
        bottomPanel.add(trackButton);
        bottomPanel.add(chatButton);
        bottomPanel.add(aboutButton);

        add(bottomPanel, BorderLayout.SOUTH);

       //Buttons

        // Add to Cart
        addToCartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Product selected = productList.getSelectedValue();
                if (selected != null) {
                    ShoppingCart.addItem(selected);
                    JOptionPane.showMessageDialog(null, selected.getName() + " added to Cart!");
                } else {
                    JOptionPane.showMessageDialog(null, "Please select a product first.");
                }
            }
        });

        // View Cart
        viewCartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new CartForm();
            }
        });

        // Track Order
        trackButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new OrderTracker();
            }
        });

        //  Chat Support
        chatButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new ChatWindow("Customer");
            }
        });

        // About Us 
        aboutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String aboutText = "GOLD RIM SHOP - Premium Tech Store\n\n" +
                                   "Location: Kenitra, Morocco\n" +
                                   "Services: \n" +
                                   "• High-quality Gaming Gear\n" +
                                   "• Fast Shipping (2 Days)\n" +
                                   "• 24/7 Customer Support\n\n" +
                                   "Contact: support@goldrim.com";
                
                JOptionPane.showMessageDialog(null, aboutText, "About Us", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        setVisible(true);
    }

    private void loadProducts() {
        String url = "jdbc:mysql://localhost:3306/OnlineShoppingDB";
        String user = "root";
        String password = "";

        try {
            Connection conn = DriverManager.getConnection(url, user, password);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM Products");

            while (rs.next()) {
                Product p = new Product(
                    rs.getInt("ProductID"),
                    rs.getString("Name"),
                    rs.getString("Description"),
                    rs.getDouble("UnitPrice"),
                    rs.getInt("StockQuantity")
                );
                listModel.addElement(p);
            }
            conn.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }
}