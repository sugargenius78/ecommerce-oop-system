import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class LoginForm extends JFrame {
    private JTextField emailField;
    private JPasswordField passField;
    private JButton loginButton, goToRegisterButton;

    public LoginForm() {
        setTitle("Gold Rim Shop - Login");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(4, 2));

        add(new JLabel("  Email:"));
        emailField = new JTextField();
        add(emailField);

        add(new JLabel("  Password:"));
        passField = new JPasswordField();
        add(passField);

        loginButton = new JButton("Login");
        add(loginButton);

        goToRegisterButton = new JButton("No Account? Register");
        add(goToRegisterButton);

        // Login Logic
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                authenticateUser();
            }
        });

        // Register Button Logic
        goToRegisterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new RegisterForm();
            }
        });

        setVisible(true);
    }

    private void authenticateUser() {
        String url = "jdbc:mysql://localhost:3306/OnlineShoppingDB";
        String user = "root";
        String password = "";
        String sql = "SELECT * FROM Users WHERE Email = ? AND Password = ?";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, emailField.getText());
            stmt.setString(2, new String(passField.getPassword()));

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                UserSession.userId = rs.getInt("UserID");
                UserSession.userName = rs.getString("FirstName");
                UserSession.email = rs.getString("Email");
                String role = rs.getString("Role");
                JOptionPane.showMessageDialog(null, "Login Successful! Welcome " + UserSession.userName);
                dispose(); 

                if ("Admin".equalsIgnoreCase(role)) {
                    new AdminDashboard(); 
                } else {
                    new CatalogForm(); 
                }

            } else {
                JOptionPane.showMessageDialog(null, "Invalid Email or Password");
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
        }
    }
}