import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;

public class OrderForm extends JFrame {
    private JTextField cardNameField, cardNumberField, cvvField;
    private JLabel totalAmountLabel;
    private JButton checkoutButton;

    public OrderForm() {
        setTitle("Gold Rim Shop - Secure Checkout");
        setSize(450, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new GridLayout(6, 1));

        double finalPrice = ShoppingCart.getTotalPrice();
        totalAmountLabel = new JLabel("Total to Pay: $" + finalPrice, SwingConstants.CENTER);
        totalAmountLabel.setFont(new Font("Arial", Font.BOLD, 18));
        add(totalAmountLabel);

        JPanel p1 = new JPanel(new FlowLayout());
        p1.add(new JLabel("Card Holder Name:"));
        cardNameField = new JTextField(20);
        p1.add(cardNameField);
        add(p1);

        JPanel p2 = new JPanel(new FlowLayout());
        p2.add(new JLabel("Card Number (16 digits):"));
        cardNumberField = new JTextField(16);
        p2.add(cardNumberField);
        add(p2);

        JPanel p3 = new JPanel(new FlowLayout());
        p3.add(new JLabel("CVV (3 digits):"));
        cvvField = new JTextField(3);
        p3.add(cvvField);
        add(p3);

        checkoutButton = new JButton("Confirm Payment & Order");
        add(checkoutButton);
        
        add(new JLabel("")); // Spacer

        checkoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(cardNumberField.getText().isEmpty() || cvvField.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Please enter all card details.");
                    return;
                }
                placeOrder(finalPrice);
            }
        });

        setVisible(true);
    }

    private void placeOrder(double amount) {
        String url = "jdbc:mysql://localhost:3306/OnlineShoppingDB";
        
        String insertOrderSQL = "INSERT INTO Orders (UserID, TotalAmount, Status) VALUES (?, ?, ?)";
        String insertDetailSQL = "INSERT INTO OrderDetails (OrderID, ProductID, Quantity, SubTotal) VALUES (?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(url, "root", "")) {
            conn.setAutoCommit(false);

            try {
                PreparedStatement stmt = conn.prepareStatement(insertOrderSQL, Statement.RETURN_GENERATED_KEYS);
                
                stmt.setInt(1, UserSession.userId); 
                stmt.setDouble(2, amount);
                stmt.setString(3, "Paid");
                stmt.executeUpdate();

                ResultSet generatedKeys = stmt.getGeneratedKeys();
                int newOrderId = 0;
                if (generatedKeys.next()) {
                    newOrderId = generatedKeys.getInt(1);
                }

                PreparedStatement detailStmt = conn.prepareStatement(insertDetailSQL);
                for (Product p : ShoppingCart.getItems()) {
                    detailStmt.setInt(1, newOrderId);
                    detailStmt.setInt(2, p.getId());
                    detailStmt.setInt(3, 1);
                    detailStmt.setDouble(4, p.getPrice());
                    detailStmt.addBatch();
                }
                detailStmt.executeBatch();
                
                conn.commit();
                
                String successMsg = "Payment Approved!\n\n" +
                                    "Your Order ID is: " + newOrderId + "\n\n" +
                                    "Please save this ID to track your order.";
                                    
                JOptionPane.showMessageDialog(null, successMsg, "Order Success", JOptionPane.INFORMATION_MESSAGE);

                ShoppingCart.clearCart(); 
                dispose();

            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}