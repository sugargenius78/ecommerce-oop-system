import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class RegisterForm extends JFrame {
    private JTextField emailField, fNameField, lNameField, addressField, shipAddressField, paymentField;
    private JPasswordField passField;
    private JComboBox<String> roleBox;
    private JButton registerButton, goToLoginButton;

    public RegisterForm() {
        setTitle("Gold Rim Shop - Register");
        setSize(450, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(10, 2));

        // Fields
        add(new JLabel("  Email:"));
        emailField = new JTextField();
        add(emailField);

        add(new JLabel("  First Name:"));
        fNameField = new JTextField();
        add(fNameField);

        add(new JLabel("  Last Name:"));
        lNameField = new JTextField();
        add(lNameField);

        add(new JLabel("  Address:"));
        addressField = new JTextField();
        add(addressField);

        add(new JLabel("  Shipping Address:"));
        shipAddressField = new JTextField();
        add(shipAddressField);

        add(new JLabel("  Preferred Payment:"));
        paymentField = new JTextField();
        add(paymentField);

        add(new JLabel("  Account Type:"));
        String[] roles = {"Customer", "Admin"};
        roleBox = new JComboBox<>(roles);
        add(roleBox);

        add(new JLabel("  Password:"));
        passField = new JPasswordField();
        add(passField);

        // 2. Buttons
        registerButton = new JButton("Register Now");
        add(registerButton);

        goToLoginButton = new JButton("Already have an account? Login");
        add(goToLoginButton);

        // 3. Logic
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                registerUser();
            }
        });

        goToLoginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new LoginForm();
            }
        });

        setVisible(true);
    }

    private void registerUser() {
        String url = "jdbc:mysql://localhost:3306/OnlineShoppingDB";
        String user = "root";
        String password = "";
        
        // Added 'Role' to the query
        String sql = "INSERT INTO Users (Email, FirstName, LastName, Address, ShippingAddress, PreferredPayment, Password, Role) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, emailField.getText());
            stmt.setString(2, fNameField.getText());
            stmt.setString(3, lNameField.getText());
            stmt.setString(4, addressField.getText());
            stmt.setString(5, shipAddressField.getText());
            stmt.setString(6, paymentField.getText());
            stmt.setString(7, new String(passField.getPassword()));
            
            // Get the selected Role from the dropdown
            String selectedRole = (String) roleBox.getSelectedItem();
            stmt.setString(8, selectedRole);

            stmt.executeUpdate();
            
            JOptionPane.showMessageDialog(null, "Registration Successful as " + selectedRole + "! Please Login.");
            dispose(); 
            new LoginForm(); 

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
        }
    }
}