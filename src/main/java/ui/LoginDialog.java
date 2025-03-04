
package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import auth.AuthService;

public class LoginDialog extends JDialog {
    private static final long serialVersionUID = 1L;
    
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton cancelButton;
    private AuthService authService;
    private boolean authenticated = false;
    
    public LoginDialog(JFrame parent, AuthService authService) {
        super(parent, "Login", true);
        this.authService = authService;
        
        initComponents();
        setSize(300, 150);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }
    
    private void initComponents() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(new JLabel("Username:"), gbc);
        
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        usernameField = new JTextField(15);
        panel.add(usernameField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        panel.add(new JLabel("Password:"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        passwordField = new JPasswordField(15);
        panel.add(passwordField, gbc);
        
        JPanel buttonPanel = new JPanel();
        loginButton = new JButton("Login");
        cancelButton = new JButton("Cancel");
        
        loginButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            
            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Username and password cannot be empty",
                    "Login Failed",
                    JOptionPane.ERROR_MESSAGE);
            } else if (authService.login(username, password)) {
                authenticated = true;
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Invalid username or password. Default is admin/admin", 
                    "Login Failed", 
                    JOptionPane.ERROR_MESSAGE);
                // Reset password field for retry
                passwordField.setText("");
            }
        });
        
        cancelButton.addActionListener(e -> dispose());
        
        buttonPanel.add(loginButton);
        buttonPanel.add(cancelButton);
        
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(buttonPanel, gbc);
        
        add(panel);
        
        // Handle Enter key
        getRootPane().setDefaultButton(loginButton);
    }
    
    public boolean isAuthenticated() {
        return authenticated;
    }
}
