import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class ChatWindow extends JFrame {
    private JTextArea chatArea;
    private JTextField inputField;
    private JButton sendButton;
    private String currentUser;
    private Timer refreshTimer;

    public ChatWindow(String userType) {
        this.currentUser = userType;
        setTitle("Gold Rim Support - " + userType);
        setSize(400, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Chat History Area
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        add(new JScrollPane(chatArea), BorderLayout.CENTER);

        // Input Area
        JPanel bottomPanel = new JPanel(new BorderLayout());
        inputField = new JTextField();
        sendButton = new JButton("Send");
        bottomPanel.add(inputField, BorderLayout.CENTER);
        bottomPanel.add(sendButton, BorderLayout.EAST);
        add(bottomPanel, BorderLayout.SOUTH);

        // Logic
        loadMessages(); 

        sendButton.addActionListener(e -> sendMessage());
        
        // Auto-refresh every 2 seconds
        refreshTimer = new Timer(2000, e -> loadMessages());
        refreshTimer.start();

        setVisible(true);
    }

    private void sendMessage() {
        if (inputField.getText().trim().isEmpty()) return;

        String url = "jdbc:mysql://localhost:3306/OnlineShoppingDB";
        try (Connection conn = DriverManager.getConnection(url, "root", "");
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO SupportMessages (Sender, Content) VALUES (?, ?)")) {

            stmt.setString(1, currentUser);
            stmt.setString(2, inputField.getText());
            stmt.executeUpdate();
            
            inputField.setText("");
            loadMessages();

        } catch (Exception ex) { ex.printStackTrace(); }
    }

    private void loadMessages() {
        String url = "jdbc:mysql://localhost:3306/OnlineShoppingDB";
        try (Connection conn = DriverManager.getConnection(url, "root", "");
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM SupportMessages ORDER BY Timestamp ASC")) {

            StringBuilder history = new StringBuilder();
            while (rs.next()) {
                history.append("[" + rs.getString("Sender") + "]: " + rs.getString("Content") + "\n");
            }
            chatArea.setText(history.toString());

        } catch (Exception ex) { /* Silent fail */ }
    }
}