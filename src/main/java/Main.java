
import ui.InventoryManagementSystem;
import ui.LoginDialog;
import auth.AuthService;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // Set system look and feel
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            AuthService authService = new AuthService();
            JFrame splashFrame = new JFrame("Inventory Management System");
            splashFrame.setUndecorated(true);
            splashFrame.setSize(400, 300);
            splashFrame.setLocationRelativeTo(null);
            
            // Show login dialog
            LoginDialog loginDialog = new LoginDialog(splashFrame, authService);
            loginDialog.setVisible(true);
            
            if (loginDialog.isAuthenticated()) {
                new InventoryManagementSystem(authService);
            } else {
                JOptionPane.showMessageDialog(
                    null,
                    "Authentication required. Exiting application.",
                    "Login Required",
                    JOptionPane.WARNING_MESSAGE
                );
                System.exit(0);
            }
        });
    }
}
