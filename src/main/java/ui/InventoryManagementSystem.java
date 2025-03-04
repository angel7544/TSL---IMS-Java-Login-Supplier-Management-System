
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
    private JTextField nameField, descField, priceField, quantityField;
    private JButton addButton, editButton, deleteButton, sellButton, restockButton;
    private JLabel statusLabel;
    
    public InventoryManagementSystem() {
        productDAO = new ProductDAO();
        
        setTitle("Inventory Management System");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        initComponents();
        loadProductData();
        
        setVisible(true);
    }
    
    private void initComponents() {
        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Create table
        tableModel = new ProductTableModel(productDAO.getAllProducts());
        productTable = new JTable(tableModel);
        productTable.getSelectionModel().addListSelectionListener(e -> {
            boolean rowSelected = productTable.getSelectedRow() >= 0;
            editButton.setEnabled(rowSelected);
            deleteButton.setEnabled(rowSelected);
            sellButton.setEnabled(rowSelected);
            restockButton.setEnabled(rowSelected);
        });
        
        JScrollPane scrollPane = new JScrollPane(productTable);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Form panel for adding/editing products
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Product Details"));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Name
        formPanel.add(new JLabel("Name:"), gbc);
        gbc.gridx++;
        nameField = new JTextField(20);
        formPanel.add(nameField, gbc);
        
        // Description
        gbc.gridx = 0;
        gbc.gridy++;
        formPanel.add(new JLabel("Description:"), gbc);
        gbc.gridx++;
        descField = new JTextField(20);
        formPanel.add(descField, gbc);
        
        // Price
        gbc.gridx = 0;
        gbc.gridy++;
        formPanel.add(new JLabel("Price:"), gbc);
        gbc.gridx++;
        priceField = new JTextField(20);
        formPanel.add(priceField, gbc);
        
        // Quantity
        gbc.gridx = 0;
        gbc.gridy++;
        formPanel.add(new JLabel("Quantity:"), gbc);
        gbc.gridx++;
        quantityField = new JTextField(20);
        formPanel.add(quantityField, gbc);
        
        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        
        addButton = new JButton("Add Product");
        addButton.addActionListener(e -> addProduct());
        buttonPanel.add(addButton);
        
        editButton = new JButton("Update");
        editButton.addActionListener(e -> editProduct());
        editButton.setEnabled(false);
        buttonPanel.add(editButton);
        
        deleteButton = new JButton("Delete");
        deleteButton.addActionListener(e -> deleteProduct());
        deleteButton.setEnabled(false);
        buttonPanel.add(deleteButton);
        
        sellButton = new JButton("Sell");
        sellButton.addActionListener(e -> sellProduct());
        sellButton.setEnabled(false);
        buttonPanel.add(sellButton);
        
        restockButton = new JButton("Restock");
        restockButton.addActionListener(e -> restockProduct());
        restockButton.setEnabled(false);
        buttonPanel.add(restockButton);
        
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        formPanel.add(buttonPanel, gbc);
        
        // Status label
        statusLabel = new JLabel(" ");
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        formPanel.add(statusLabel, gbc);
        
        mainPanel.add(formPanel, BorderLayout.SOUTH);
        
        // Analytics and reports panel
        JPanel reportsPanel = new JPanel(new GridLayout(6, 1, 10, 10));
        reportsPanel.setBorder(BorderFactory.createTitledBorder("Reports & Tools"));
        
        JButton pdfReportButton = new JButton("Generate PDF Report");
        pdfReportButton.addActionListener(e -> generatePdfReport());
        reportsPanel.add(pdfReportButton);
        
        JButton excelReportButton = new JButton("Generate Excel Report");
        excelReportButton.addActionListener(e -> generateExcelReport());
        reportsPanel.add(excelReportButton);
        
        JButton topSellingButton = new JButton("Show Top Selling Product");
        topSellingButton.addActionListener(e -> showTopSellingProduct());
        reportsPanel.add(topSellingButton);
        
        JButton lowStockButton = new JButton("Show Low Stock Products");
        lowStockButton.addActionListener(e -> showLowStockProducts());
        reportsPanel.add(lowStockButton);
        
        JButton backupButton = new JButton("Backup Data");
        backupButton.addActionListener(e -> backupData());
        reportsPanel.add(backupButton);
        
        JButton restoreButton = new JButton("Restore Data");
        restoreButton.addActionListener(e -> restoreData());
        reportsPanel.add(restoreButton);
        
        mainPanel.add(reportsPanel, BorderLayout.EAST);
        
        setContentPane(mainPanel);
        
        // Set up menu
        JMenuBar menuBar = new JMenuBar();
        
        JMenu fileMenu = new JMenu("File");
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> System.exit(0));
        fileMenu.add(exitItem);
        
        JMenu dataMenu = new JMenu("Data");
        JMenuItem refreshItem = new JMenuItem("Refresh");
        refreshItem.addActionListener(e -> loadProductData());
        dataMenu.add(refreshItem);
        
        JMenu helpMenu = new JMenu("Help");
        JMenuItem aboutItem = new JMenuItem("About");
        aboutItem.addActionListener(e -> 
            JOptionPane.showMessageDialog(this, 
                "Inventory Management System\nVersion 1.0", 
                "About", 
                JOptionPane.INFORMATION_MESSAGE)
        );
        helpMenu.add(aboutItem);
        
        menuBar.add(fileMenu);
        menuBar.add(dataMenu);
        menuBar.add(helpMenu);
        
        setJMenuBar(menuBar);
    }
    
    private void loadProductData() {
        tableModel.refreshData(productDAO.getAllProducts());
        clearForm();
        statusLabel.setText("Data loaded successfully");
    }
    
    private void clearForm() {
        nameField.setText("");
        descField.setText("");
        priceField.setText("");
        quantityField.setText("");
        productTable.clearSelection();
    }
    
    private void addProduct() {
        try {
            String name = nameField.getText().trim();
            String description = descField.getText().trim();
            double price = Double.parseDouble(priceField.getText().trim());
            int quantity = Integer.parseInt(quantityField.getText().trim());
            
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Name cannot be empty", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (price < 0) {
                JOptionPane.showMessageDialog(this, "Price cannot be negative", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (quantity < 0) {
                JOptionPane.showMessageDialog(this, "Quantity cannot be negative", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            Product product = new Product(0, name, description, price, quantity, 0);
            productDAO.addProduct(product);
            
            loadProductData();
            statusLabel.setText("Product added successfully");
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid number format", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void editProduct() {
        int selectedRow = productTable.getSelectedRow();
        if (selectedRow >= 0) {
            Product product = tableModel.getProductAt(selectedRow);
            
            try {
                String name = nameField.getText().trim();
                String description = descField.getText().trim();
                double price = Double.parseDouble(priceField.getText().trim());
                int quantity = Integer.parseInt(quantityField.getText().trim());
                
                if (name.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Name cannot be empty", "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                if (price < 0) {
                    JOptionPane.showMessageDialog(this, "Price cannot be negative", "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                if (quantity < 0) {
                    JOptionPane.showMessageDialog(this, "Quantity cannot be negative", "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                product.setName(name);
                product.setDescription(description);
                product.setPrice(price);
                product.setQuantity(quantity);
                
                productDAO.updateProduct(product);
                
                loadProductData();
                statusLabel.setText("Product updated successfully");
                
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid number format", "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void deleteProduct() {
        int selectedRow = productTable.getSelectedRow();
        if (selectedRow >= 0) {
            Product product = tableModel.getProductAt(selectedRow);
            
            int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete '" + product.getName() + "'?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION
            );
            
            if (confirm == JOptionPane.YES_OPTION) {
                productDAO.deleteProduct(product.getId());
                loadProductData();
                statusLabel.setText("Product deleted successfully");
            }
        }
    }
    
    private void sellProduct() {
        int selectedRow = productTable.getSelectedRow();
        if (selectedRow >= 0) {
            Product product = tableModel.getProductAt(selectedRow);
            
            String input = JOptionPane.showInputDialog(
                this,
                "Enter quantity to sell (available: " + product.getQuantity() + "):",
                "Sell Product",
                JOptionPane.QUESTION_MESSAGE
            );
            
            if (input != null) {
                try {
                    int quantity = Integer.parseInt(input);
                    
                    if (quantity <= 0) {
                        JOptionPane.showMessageDialog(this, "Quantity must be positive", "Input Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    
                    if (quantity > product.getQuantity()) {
                        JOptionPane.showMessageDialog(this, "Not enough stock available", "Input Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    
                    product.sellItems(quantity);
                    productDAO.updateProduct(product);
                    
                    loadProductData();
                    statusLabel.setText("Product sold successfully");
                    
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, "Invalid number format", "Input Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
    
    private void restockProduct() {
        int selectedRow = productTable.getSelectedRow();
        if (selectedRow >= 0) {
            Product product = tableModel.getProductAt(selectedRow);
            
            String input = JOptionPane.showInputDialog(
                this,
                "Enter quantity to restock:",
                "Restock Product",
                JOptionPane.QUESTION_MESSAGE
            );
            
            if (input != null) {
                try {
                    int quantity = Integer.parseInt(input);
                    
                    if (quantity <= 0) {
                        JOptionPane.showMessageDialog(this, "Quantity must be positive", "Input Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    
                    product.restock(quantity);
                    productDAO.updateProduct(product);
                    
                    loadProductData();
                    statusLabel.setText("Product restocked successfully");
                    
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, "Invalid number format", "Input Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
    
    private void generatePdfReport() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save PDF Report");
        fileChooser.setFileFilter(new FileNameExtensionFilter("PDF Files", "pdf"));
        
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            String path = file.getAbsolutePath();
            if (!path.toLowerCase().endsWith(".pdf")) {
                path += ".pdf";
            }
            
            try {
                ReportGenerator.generatePdfReport(productDAO.getAllProducts(), path);
                statusLabel.setText("PDF report generated successfully");
                JOptionPane.showMessageDialog(this, "PDF report generated successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error generating PDF report: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void generateExcelReport() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Excel Report");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Excel Files", "xlsx"));
        
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            String path = file.getAbsolutePath();
            if (!path.toLowerCase().endsWith(".xlsx")) {
                path += ".xlsx";
            }
            
            try {
                ReportGenerator.generateExcelReport(productDAO.getAllProducts(), path);
                statusLabel.setText("Excel report generated successfully");
                JOptionPane.showMessageDialog(this, "Excel report generated successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error generating Excel report: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void showTopSellingProduct() {
        Product topProduct = productDAO.getTopSellingProduct();
        
        if (topProduct != null) {
            JOptionPane.showMessageDialog(
                this,
                "Top Selling Product:\n\n" +
                "Name: " + topProduct.getName() + "\n" +
                "Total Sold: " + topProduct.getSold() + "\n" +
                "Current Stock: " + topProduct.getQuantity(),
                "Top Selling Product",
                JOptionPane.INFORMATION_MESSAGE
            );
        } else {
            JOptionPane.showMessageDialog(this, "No product data available", "Top Selling Product", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void showLowStockProducts() {
        List<Product> lowStockProducts = productDAO.getLowStockProducts(5); // Threshold of 5
        
        if (!lowStockProducts.isEmpty()) {
            StringBuilder message = new StringBuilder();
            message.append("Low Stock Products (Quantity <= 5):\n\n");
            
            for (Product product : lowStockProducts) {
                message.append("- ").append(product.getName())
                       .append(" (ID: ").append(product.getId())
                       .append(", Stock: ").append(product.getQuantity())
                       .append(")\n");
            }
            
            JOptionPane.showMessageDialog(
                this,
                message.toString(),
                "Low Stock Products",
                JOptionPane.WARNING_MESSAGE
            );
        } else {
            JOptionPane.showMessageDialog(
                this,
                "No products with low stock (Quantity <= 5)",
                "Low Stock Products",
                JOptionPane.INFORMATION_MESSAGE
            );
        }
    }
    
    private void backupData() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Backup Data");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Backup Files", "bak"));
        
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            String path = file.getAbsolutePath();
            if (!path.toLowerCase().endsWith(".bak")) {
                path += ".bak";
            }
            
            try {
                productDAO.backup(path);
                statusLabel.setText("Data backup created successfully");
                JOptionPane.showMessageDialog(this, "Data backup created successfully", "Backup", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error creating backup: " + e.getMessage(), "Backup Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void restoreData() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Restore Data");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Backup Files", "bak"));
        
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            
            int confirm = JOptionPane.showConfirmDialog(
                this,
                "Restoring will overwrite current data. Continue?",
                "Confirm Restore",
                JOptionPane.YES_NO_OPTION
            );
            
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    productDAO.restore(file.getAbsolutePath());
                    loadProductData();
                    statusLabel.setText("Data restored successfully");
                    JOptionPane.showMessageDialog(this, "Data restored successfully", "Restore", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, "Error restoring data: " + e.getMessage(), "Restore Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
}
