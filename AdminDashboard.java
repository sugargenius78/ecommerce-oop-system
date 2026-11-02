import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class AdminDashboard extends JFrame {
    private JButton openChatButton, addProductButton, manageProductButton;
    private JButton manageUsersButton, manageOrdersButton, logoutButton;

    public AdminDashboard() {
        setTitle("Gold Rim - Administrator Dashboard");
        setSize(500, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(7, 1));

        JLabel title = new JLabel("Admin Control Center", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 22));
        add(title);

        //BUTTONS
        openChatButton = new JButton("Open Support Chat");
        addProductButton = new JButton("Add New Product");
        manageProductButton = new JButton("Manage Products (Edit/Delete)"); 
        manageUsersButton = new JButton("Manage Users (Delete)");
        manageOrdersButton = new JButton("Manage Orders (Update/Delete)");
        logoutButton = new JButton("Logout");

        add(openChatButton);
        add(addProductButton);
        add(manageProductButton);
        add(manageUsersButton);
        add(manageOrdersButton);
        add(logoutButton);

        //LOGIC
        openChatButton.addActionListener(e -> new ChatWindow("Admin"));
        addProductButton.addActionListener(e -> showAddProductDialog());
        
        //Manage Products with Edit & Delete
        manageProductButton.addActionListener(e -> showProductManager());

        manageUsersButton.addActionListener(e -> showDeleteList("User")); 
        manageOrdersButton.addActionListener(e -> showOrderManager());
        
        logoutButton.addActionListener(e -> {
            dispose();
            new LoginForm();
        });

        setVisible(true);
    }

    // ADVANCED PRODUCT MANAGER (Edit & Delete) 
    private void showProductManager() {
        JDialog dialog = new JDialog(this, "Manage Products", true);
        dialog.setSize(500, 400);
        dialog.setLayout(new BorderLayout());

        DefaultListModel<String> model = new DefaultListModel<>();
        JList<String> list = new JList<>(model);
        
        loadProductsIntoModel(model); // Load products from DB

        JPanel btnPanel = new JPanel();
        JButton editBtn = new JButton("Edit Selected");
        JButton deleteBtn = new JButton("Delete Selected");
        
        btnPanel.add(editBtn);
        btnPanel.add(deleteBtn);
        
        dialog.add(new JScrollPane(list), BorderLayout.CENTER);
        dialog.add(btnPanel, BorderLayout.SOUTH);

        // 1. EDIT LOGIC
        editBtn.addActionListener(e -> {
            String selected = list.getSelectedValue();
            if (selected != null) {
                String id = selected.split(" - ")[0];
                showEditProductPopup(id); 
                loadProductsIntoModel(model); // Refresh list after edit
            } else {
                JOptionPane.showMessageDialog(dialog, "Please select a product to edit.");
            }
        });

        // 2. DELETE LOGIC
        deleteBtn.addActionListener(e -> {
            String selected = list.getSelectedValue();
            if (selected != null) {
                String id = selected.split(" - ")[0];
                // Cascade delete
                executeUpdate("DELETE FROM OrderDetails WHERE ProductID=" + id);
                if(executeUpdate("DELETE FROM Products WHERE ProductID=" + id)) {
                    loadProductsIntoModel(model); 
                    JOptionPane.showMessageDialog(dialog, "Product Deleted!");
                }
            }
        });

        dialog.setVisible(true);
    }

    // --- NEW: EDIT POPUP ---
    private void showEditProductPopup(String id) {
        String url = "jdbc:mysql://localhost:3306/OnlineShoppingDB";
        
        try (Connection conn = DriverManager.getConnection(url, "root", "");
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM Products WHERE ProductID=" + id)) {

            if (rs.next()) {
                // Pre-fill fields with current data
                JTextField nameField = new JTextField(rs.getString("Name"));
                JTextField descField = new JTextField(rs.getString("Description"));
                JTextField priceField = new JTextField(String.valueOf(rs.getDouble("UnitPrice")));
                JTextField stockField = new JTextField(String.valueOf(rs.getInt("StockQuantity")));

                Object[] message = {
                    "Name:", nameField,
                    "Description:", descField,
                    "Price ($):", priceField,
                    "Stock:", stockField
                };

                int option = JOptionPane.showConfirmDialog(null, message, "Edit Product Details", JOptionPane.OK_CANCEL_OPTION);
                
                if (option == JOptionPane.OK_OPTION) {
                    // Update Database
                    String sql = "UPDATE Products SET Name=?, Description=?, UnitPrice=?, StockQuantity=? WHERE ProductID=?";
                    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                        pstmt.setString(1, nameField.getText());
                        pstmt.setString(2, descField.getText());
                        pstmt.setDouble(3, Double.parseDouble(priceField.getText()));
                        pstmt.setInt(4, Integer.parseInt(stockField.getText()));
                        pstmt.setInt(5, Integer.parseInt(id));
                        
                        pstmt.executeUpdate();
                        JOptionPane.showMessageDialog(null, "Product Updated Successfully!");
                    }
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
        }
    }

    // Load Products List 
    private void loadProductsIntoModel(DefaultListModel<String> model) {
        model.clear();
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/OnlineShoppingDB", "root", "");
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM Products")) {
            
            while(rs.next()) {
                model.addElement(rs.getInt("ProductID") + " - " + rs.getString("Name") + " ($" + rs.getDouble("UnitPrice") + ")");
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void showAddProductDialog() {
        JTextField nameField = new JTextField();
        JTextField descField = new JTextField();
        JTextField priceField = new JTextField();
        JTextField stockField = new JTextField();

        Object[] message = { "Name:", nameField, "Description:", descField, "Price:", priceField, "Stock:", stockField };
        int option = JOptionPane.showConfirmDialog(null, message, "Add Product", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            executeUpdate("INSERT INTO Products (Name, Description, UnitPrice, StockQuantity) VALUES ('" 
                + nameField.getText() + "', '" + descField.getText() + "', " + priceField.getText() + ", " + stockField.getText() + ")");
        }
    }

    private void showDeleteList(String type) {
        // Simple delete for Users 
        JDialog dialog = new JDialog(this, "Manage Users", true);
        dialog.setSize(400, 300);
        DefaultListModel<String> model = new DefaultListModel<>();
        JList<String> list = new JList<>(model);
        
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/OnlineShoppingDB", "root", "");
             Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery("SELECT * FROM Users WHERE Role='Customer'");
            while(rs.next()) {
                model.addElement(rs.getInt("UserID") + " - " + rs.getString("FirstName") + " (" + rs.getString("Email") + ")");
            }
        } catch (Exception e) { e.printStackTrace(); }

        JButton deleteBtn = new JButton("Delete Selected");
        deleteBtn.addActionListener(e -> {
            String selected = list.getSelectedValue();
            if (selected != null) {
                String id = selected.split(" - ")[0];
                executeUpdate("DELETE FROM OrderDetails WHERE OrderID IN (SELECT OrderID FROM Orders WHERE UserID=" + id + ")");
                executeUpdate("DELETE FROM Orders WHERE UserID=" + id);
                if(executeUpdate("DELETE FROM Users WHERE UserID=" + id)) {
                     model.removeElement(selected);
                     JOptionPane.showMessageDialog(null, "User Deleted!");
                }
            }
        });
        dialog.add(new JScrollPane(list), BorderLayout.CENTER);
        dialog.add(deleteBtn, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void showOrderManager() {
        JDialog dialog = new JDialog(this, "Manage Orders", true);
        dialog.setSize(500, 400);
        DefaultListModel<String> model = new DefaultListModel<>();
        JList<String> list = new JList<>(model);
        
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/OnlineShoppingDB", "root", "");
             Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery("SELECT * FROM Orders");
            while(rs.next()) {
                model.addElement(rs.getInt("OrderID") + " - Total: $" + rs.getDouble("TotalAmount") + " - Status: [" + rs.getString("Status") + "]");
            }
        } catch (Exception e) { e.printStackTrace(); }

        JPanel btnPanel = new JPanel();
        JButton shipBtn = new JButton("Mark as Shipped");
        JButton deleteBtn = new JButton("Delete Order");
        btnPanel.add(shipBtn);
        btnPanel.add(deleteBtn);

        shipBtn.addActionListener(e -> {
            String selected = list.getSelectedValue();
            if (selected != null) {
                String id = selected.split(" - ")[0];
                executeUpdate("UPDATE Orders SET Status='Shipped' WHERE OrderID=" + id);
                dialog.dispose(); showOrderManager();
            }
        });

        deleteBtn.addActionListener(e -> {
            String selected = list.getSelectedValue();
            if (selected != null) {
                String id = selected.split(" - ")[0];
                executeUpdate("DELETE FROM OrderDetails WHERE OrderID=" + id); 
                executeUpdate("DELETE FROM Orders WHERE OrderID=" + id);
                model.removeElement(selected);
            }
        });

        dialog.add(new JScrollPane(list), BorderLayout.CENTER);
        dialog.add(btnPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private boolean executeUpdate(String sql) {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/OnlineShoppingDB", "root", "");
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
            return true;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
            return false;
        }
    }
}