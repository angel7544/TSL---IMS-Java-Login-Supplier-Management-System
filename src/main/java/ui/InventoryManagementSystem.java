
package ui;

import dao.ProductDAO;
import models.Product;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class InventoryManagementSystem extends JFrame {
    private static final long serialVersionUID = 1L;
    
    private final ProductDAO productDAO;
    private JTable productTable;
    private ProductTableModel tableModel;
    private JButton addButton, editButton, deleteButton, sellButton, restockButton;
    private JButton backupButton, restoreButton, reportButton;
    private JLabel statusLabel;
    
    public InventoryManagementSystem() {
        productDAO = new ProductDAO();
        
        // Initialize UI components
        setTitle("Inventory Management System");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        createMenuBar();
        createMainPanel();
        
        refreshProductList();
        
        setVisible(true);
    }
    
    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        
        JMenu fileMenu = new JMenu("File");
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> System.exit(0));
        fileMenu.add(exitItem);
        
        JMenu reportMenu = new JMenu("Reports");
        JMenuItem pdfReportItem = new JMenuItem("Generate PDF Report");
        pdfReportItem.addActionListener(e -> generateReport("pdf"));
        
        JMenuItem excelReportItem = new JMenuItem("Generate Excel Report");
        excelReportItem.addActionListener(e -> generateReport("excel"));
        
        reportMenu.add(pdfReportItem);
        reportMenu.add(excelReportItem);
        
        JMenu analyticsMenu = new JMenu("Analytics");
        JMenuItem topSellingItem = new JMenuItem("Top Selling Product");
        topSellingItem.addActionListener(e -> showTopSellingProduct());
        
        JMenuItem lowStockItem = new JMenuItem("Low Stock Alert");
        lowStockItem.addActionListener(e -> showLowStockProducts());
        
        analyticsMenu.add(topSellingItem);
        analyticsMenu.add(lowStockItem);
        
        menuBar.add(fileMenu);
        menuBar.add(reportMenu);
        menuBar.add(analyticsMenu);
        
        setJMenuBar(menuBar);
    }
    
    private void createMainPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        // Create table
        tableModel = new ProductTableModel(productDAO.getAllProducts());
        productTable = new JTable(tableModel);
        productTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        productTable.setAutoCreateRowSorter(true);
        JScrollPane scrollPane = new JScrollPane(productTable);
        
        // Control buttons panel
        JPanel buttonPanel = new JPanel();
        addButton = new JButton("Add Product");
        editButton = new JButton("Edit Product");
        deleteButton = new JButton("Delete Product");
        sellButton = new JButton("Sell");
        restockButton = new JButton("Restock");
        backupButton = new JButton("Backup");
        restoreButton = new JButton("Restore");
        reportButton = new JButton("Generate Report");
        
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(sellButton);
        buttonPanel.add(restockButton);
        buttonPanel.add(backupButton);
        buttonPanel.add(restoreButton);
        buttonPanel.add(reportButton);
        
        // Status bar
        statusLabel = new JLabel("Ready");
        statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        
        // Add components to main panel
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.NORTH);
        mainPanel.add(statusLabel, BorderLayout.SOUTH);
        
        // Add action listeners
        addButton.addActionListener(e -> addProduct());
        editButton.addActionListener(e -> editProduct());
        deleteButton.addActionListener(e -> deleteProduct());
        sellButton.addActionListener(e -> sellProduct());
        restockButton.addActionListener(e -> restockProduct());
        backupButton.addActionListener(e -> backupData());
        restoreButton.addActionListener(e -> restoreData());
        reportButton.addActionListener(e -> generateReport("pdf"));
        
        // Add main panel to frame
        add(mainPanel);
    }
    
    private void refreshProductList() {
        tableModel.refreshData(productDAO.getAllProducts());
        statusLabel.setText("Product list updated");
    }
    
    private void addProduct() {
        JDialog dialog = new JDialog(this, "Add New Product", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JTextField nameField = new JTextField();
        JTextField descField = new JTextField();
        JTextField priceField = new JTextField();
        JTextField quantityField = new JTextField();
        
        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Description:"));
        panel.add(descField);
        panel.add(new JLabel("Price:"));
        panel.add(priceField);
        panel.add(new JLabel("Quantity:"));
        panel.add(quantityField);
        
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        saveButton.addActionListener(e -> {
            try {
                String name = nameField.getText().trim();
                String description = descField.getText().trim();
                double price = Double.parseDouble(priceField.getText().trim());
                int quantity = Integer.parseInt(quantityField.getText().trim());
                
                if (name.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Name cannot be empty", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                Product product = new Product(0, name, description, price, quantity);
                productDAO.addProduct(product);
                refreshProductList();
                dialog.dispose();
                statusLabel.setText("Product added successfully");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Invalid price or quantity", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        cancelButton.addActionListener(e -> dialog.dispose());
        
        dialog.setLayout(new BorderLayout());
        dialog.add(panel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
    
    private void editProduct() {
        int selectedRow = productTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a product to edit", "No Selection", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        int modelRow = productTable.convertRowIndexToModel(selectedRow);
        Product product = tableModel.getProductAt(modelRow);
        
        JDialog dialog = new JDialog(this, "Edit Product", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JTextField nameField = new JTextField(product.getName());
        JTextField descField = new JTextField(product.getDescription());
        JTextField priceField = new JTextField(String.valueOf(product.getPrice()));
        JTextField quantityField = new JTextField(String.valueOf(product.getQuantity()));
        
        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Description:"));
        panel.add(descField);
        panel.add(new JLabel("Price:"));
        panel.add(priceField);
        panel.add(new JLabel("Quantity:"));
        panel.add(quantityField);
        
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        saveButton.addActionListener(e -> {
            try {
                String name = nameField.getText().trim();
                String description = descField.getText().trim();
                double price = Double.parseDouble(priceField.getText().trim());
                int quantity = Integer.parseInt(quantityField.getText().trim());
                
                if (name.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Name cannot be empty", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                product.setName(name);
                product.setDescription(description);
                product.setPrice(price);
                product.setQuantity(quantity);
                
                productDAO.updateProduct(product);
                refreshProductList();
                dialog.dispose();
                statusLabel.setText("Product updated successfully");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Invalid price or quantity", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        cancelButton.addActionListener(e -> dialog.dispose());
        
        dialog.setLayout(new BorderLayout());
        dialog.add(panel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
    
    private void deleteProduct() {
        int selectedRow = productTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a product to delete", "No Selection", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        int modelRow = productTable.convertRowIndexToModel(selectedRow);
        Product product = tableModel.getProductAt(modelRow);
        
        int confirm = JOptionPane.showConfirmDialog(this, 
                "Are you sure you want to delete " + product.getName() + "?", 
                "Confirm Delete", 
                JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            productDAO.deleteProduct(product.getId());
            refreshProductList();
            statusLabel.setText("Product deleted successfully");
        }
    }
    
    private void sellProduct() {
        int selectedRow = productTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a product to sell", "No Selection", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        int modelRow = productTable.convertRowIndexToModel(selectedRow);
        Product product = tableModel.getProductAt(modelRow);
        
        String input = JOptionPane.showInputDialog(this, 
                "Current stock: " + product.getQuantity() + "\nEnter quantity to sell:", 
                "Sell Product", 
                JOptionPane.QUESTION_MESSAGE);
        
        if (input != null && !input.trim().isEmpty()) {
            try {
                int quantity = Integer.parseInt(input.trim());
                if (quantity <= 0) {
                    JOptionPane.showMessageDialog(this, "Quantity must be positive", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                if (product.removeStock(quantity)) {
                    productDAO.updateProduct(product);
                    refreshProductList();
                    statusLabel.setText("Sold " + quantity + " units of " + product.getName());
                } else {
                    JOptionPane.showMessageDialog(this, 
                            "Insufficient stock. Current stock: " + product.getQuantity(), 
                            "Stock Error", 
                            JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Please enter a valid number", "Invalid Input", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void restockProduct() {
        int selectedRow = productTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a product to restock", "No Selection", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        int modelRow = productTable.convertRowIndexToModel(selectedRow);
        Product product = tableModel.getProductAt(modelRow);
        
        String input = JOptionPane.showInputDialog(this, 
                "Current stock: " + product.getQuantity() + "\nEnter quantity to add:", 
                "Restock Product", 
                JOptionPane.QUESTION_MESSAGE);
        
        if (input != null && !input.trim().isEmpty()) {
            try {
                int quantity = Integer.parseInt(input.trim());
                if (quantity <= 0) {
                    JOptionPane.showMessageDialog(this, "Quantity must be positive", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                product.addStock(quantity);
                productDAO.updateProduct(product);
                refreshProductList();
                statusLabel.setText("Added " + quantity + " units to " + product.getName() + " stock");
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Please enter a valid number", "Invalid Input", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void backupData() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Backup Data");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Data Files (*.dat)", "dat"));
        
        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            String filePath = selectedFile.getAbsolutePath();
            if (!filePath.endsWith(".dat")) {
                filePath += ".dat";
            }
            
            try {
                productDAO.backup(filePath);
                statusLabel.setText("Data backed up successfully to " + filePath);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, 
                        "Error backing up data: " + e.getMessage(), 
                        "Backup Error", 
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void restoreData() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Restore Data");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Data Files (*.dat)", "dat"));
        
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            
            try {
                productDAO.restore(selectedFile.getAbsolutePath());
                refreshProductList();
                statusLabel.setText("Data restored successfully from " + selectedFile.getAbsolutePath());
            } catch (IOException | ClassNotFoundException e) {
                JOptionPane.showMessageDialog(this, 
                        "Error restoring data: " + e.getMessage(), 
                        "Restore Error", 
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void generateReport(String type) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Report");
        
        String extension = type.equals("pdf") ? "pdf" : "xlsx";
        String description = type.equals("pdf") ? "PDF Files (*.pdf)" : "Excel Files (*.xlsx)";
        fileChooser.setFileFilter(new FileNameExtensionFilter(description, extension));
        
        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            String filePath = selectedFile.getAbsolutePath();
            if (!filePath.endsWith("." + extension)) {
                filePath += "." + extension;
            }
            
            try {
                ReportGenerator.generateReport(productDAO.getAllProducts(), filePath, type);
                statusLabel.setText("Report generated successfully: " + filePath);
                
                // Open the file
                Desktop.getDesktop().open(new File(filePath));
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, 
                        "Error generating report: " + e.getMessage(), 
                        "Report Error", 
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void showTopSellingProduct() {
        Product topProduct = productDAO.getTopSellingProduct();
        if (topProduct != null && topProduct.getSold() > 0) {
            JOptionPane.showMessageDialog(this, 
                    "Top Selling Product:\n\n" +
                    "Name: " + topProduct.getName() + "\n" +
                    "Units Sold: " + topProduct.getSold() + "\n" +
                    "Revenue: $" + String.format("%.2f", topProduct.getPrice() * topProduct.getSold()),
                    "Top Selling Product",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, 
                    "No sales data available yet.",
                    "Top Selling Product",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void showLowStockProducts() {
        List<Product> lowStockProducts = productDAO.getLowStockProducts(5); // Threshold of 5
        
        if (lowStockProducts.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                    "No products with low stock (below 5 units).",
                    "Low Stock Alert",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        StringBuilder message = new StringBuilder("Products with low stock (below 5 units):\n\n");
        for (Product product : lowStockProducts) {
            message.append(product.getName())
                   .append(" - ")
                   .append(product.getQuantity())
                   .append(" units left\n");
        }
        
        JOptionPane.showMessageDialog(this, 
                message.toString(),
                "Low Stock Alert",
                JOptionPane.WARNING_MESSAGE);
    }
}
