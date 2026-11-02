import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CartForm extends JFrame {
    private DefaultListModel<String> listModel;
    private JList<String> cartList;
    private JLabel totalLabel;
    private JButton removeButton, checkoutButton, backButton;

    public CartForm() {
        setTitle("Gold Rim Shop - Your Cart");
        setSize(500, 450);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JLabel title = new JLabel("Shopping Cart", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 22));
        add(title, BorderLayout.NORTH);

        listModel = new DefaultListModel<>();
        refreshCartList(); // Load items
        cartList = new JList<>(listModel);
        add(new JScrollPane(cartList), BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new GridLayout(3, 1));
        
        totalLabel = new JLabel("Total: $" + ShoppingCart.getTotalPrice(), SwingConstants.CENTER);
        totalLabel.setFont(new Font("Arial", Font.BOLD, 16));
        bottomPanel.add(totalLabel);

        JPanel btnPanel = new JPanel();
        removeButton = new JButton("Remove Selected Item");
        checkoutButton = new JButton("Proceed to Checkout");
        backButton = new JButton("Back to Catalog");
        
        btnPanel.add(backButton);
        btnPanel.add(removeButton);
        btnPanel.add(checkoutButton);
        bottomPanel.add(btnPanel);

        add(bottomPanel, BorderLayout.SOUTH);

        removeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedIndex = cartList.getSelectedIndex();
                if (selectedIndex != -1) {
                    ShoppingCart.removeItem(selectedIndex); // Remove from memory
                    refreshCartList(); // Refresh UI
                    totalLabel.setText("Total: $" + ShoppingCart.getTotalPrice()); // Update Total
                } else {
                    JOptionPane.showMessageDialog(null, "Please select an item to remove.");
                }
            }
        });

        checkoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (ShoppingCart.getItems().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Your cart is empty!");
                } else {
                    dispose();
                    new OrderForm(); // Go to Payment
                }
            }
        });

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose(); 
               
            }
        });

        setVisible(true);
    }

    private void refreshCartList() {
        listModel.clear();
        for (Product p : ShoppingCart.getItems()) {
            listModel.addElement(p.toString());
        }
    }
}