import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.concurrent.TimeUnit;

public class OrderTracker extends JFrame {
    private JTextField emailField, orderIdField;
    private JLabel statusLabel, dateLabel, totalLabel;
    private JButton trackButton;

    public OrderTracker() {
        setTitle("Gold Rim - Track Order");
        setSize(400, 350);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new GridLayout(6, 1));

        JLabel title = new JLabel("Track Your Order", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        add(title);

        // Inputs
        JPanel inputPanel = new JPanel(new GridLayout(2, 2));
        inputPanel.add(new JLabel("  Your Email:"));
        emailField = new JTextField();
        inputPanel.add(emailField);
        
        inputPanel.add(new JLabel("  Order ID:"));
        orderIdField = new JTextField();
        inputPanel.add(orderIdField);
        add(inputPanel);

        trackButton = new JButton("Track Now");
        add(trackButton);

        // Results
        statusLabel = new JLabel("Status: Waiting for input...", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Arial", Font.BOLD, 16));
        statusLabel.setForeground(Color.BLUE);
        add(statusLabel);

        dateLabel = new JLabel("", SwingConstants.CENTER);
        add(dateLabel);

        totalLabel = new JLabel("", SwingConstants.CENTER);
        add(totalLabel);

        // Logic
        trackButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                trackOrder();
            }
        });

        setVisible(true);
    }

    private void trackOrder() {
        String email = emailField.getText();
        String orderId = orderIdField.getText();

        String url = "jdbc:mysql://localhost:3306/OnlineShoppingDB";
        // Query checks BOTH Email and ID as required
        String sql = "SELECT o.OrderDate, o.TotalAmount " +
                     "FROM Orders o " +
                     "JOIN Users u ON o.UserID = u.UserID " +
                     "WHERE o.OrderID = ? AND u.Email = ?";

        try (Connection conn = DriverManager.getConnection(url, "root", "");
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, Integer.parseInt(orderId));
            stmt.setString(2, email);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Timestamp orderDate = rs.getTimestamp("OrderDate");
                double total = rs.getDouble("TotalAmount");
                long diffInMillis = System.currentTimeMillis() - orderDate.getTime();
                long diffInDays = TimeUnit.MILLISECONDS.toDays(diffInMillis);

                String status;
                if (diffInDays < 1) {
                    status = "Prepared";
                } else if (diffInDays <= 2) {
                    status = "In Preparation";
                } else {
                    status = "Shipped";
                }

                statusLabel.setText("Status: " + status);
                dateLabel.setText("Ordered on: " + orderDate.toString());
                totalLabel.setText("Total: $" + total);

            } else {
                statusLabel.setText("Order Not Found");
                JOptionPane.showMessageDialog(null, "No order matches that Email and ID.");
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
        }
    }
}