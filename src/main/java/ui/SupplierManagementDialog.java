
package ui;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import dao.SupplierDAO;
import models.Supplier;

public class SupplierManagementDialog extends JDialog {
    private static final long serialVersionUID = 1L;
    
    private SupplierDAO supplierDAO;
    private JTable supplierTable;
    private SupplierTableModel tableModel;
    private JTextField nameField, contactField, emailField, phoneField;
    private JTextArea addressArea;
    private JButton addButton, editButton, deleteButton, saveButton, cancelButton;
    
    public SupplierManagementDialog(JFrame parent, SupplierDAO supplierDAO) {
        super(parent, "Supplier Management", true);
        this.supplierDAO = supplierDAO;
        
        initComponents();
        loadData();
        
        setSize(800, 500);
        setLocationRelativeTo(parent);
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        
        // Create table
        tableModel = new SupplierTableModel();
        supplierTable = new JTable(tableModel);
        supplierTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        supplierTable.getSelectionModel().addListSelectionListener(e -> {
            boolean rowSelected = supplierTable.getSelectedRow() >= 0;
            editButton.setEnabled(rowSelected);
            deleteButton.setEnabled(rowSelected);
        });
        
        JScrollPane scrollPane = new JScrollPane(supplierTable);
        add(scrollPane, BorderLayout.CENTER);
        
        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Supplier Details"));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Name
        formPanel.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        nameField = new JTextField(20);
        formPanel.add(nameField, gbc);
        
        // Contact Person
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Contact Person:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        contactField = new JTextField(20);
        formPanel.add(contactField, gbc);
        
        // Email
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        emailField = new JTextField(20);
        formPanel.add(emailField, gbc);
        
        // Phone
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Phone:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        phoneField = new JTextField(20);
        formPanel.add(phoneField, gbc);
        
        // Address
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Address:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;
        addressArea = new JTextArea(3, 20);
        addressArea.setLineWrap(true);
        formPanel.add(new JScrollPane(addressArea), gbc);
        
        // Buttons
        JPanel buttonPanel = new JPanel();
        addButton = new JButton("Add New");
        editButton = new JButton("Edit");
        deleteButton = new JButton("Delete");
        saveButton = new JButton("Save");
        cancelButton = new JButton("Cancel");
        
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(buttonPanel, gbc);
        
        add(formPanel, BorderLayout.SOUTH);
        
        // Set up initial button states
        editButton.setEnabled(false);
        deleteButton.setEnabled(false);
        saveButton.setEnabled(false);
        cancelButton.setEnabled(false);
        
        // Add action listeners
        addButton.addActionListener(e -> prepareAdd());
        editButton.addActionListener(e -> prepareEdit());
        deleteButton.addActionListener(e -> deleteSupplier());
        saveButton.addActionListener(e -> saveSupplier());
        cancelButton.addActionListener(e -> cancelEdit());
        
        setFormEnabled(false);
    }
    
    private void loadData() {
        tableModel.refresh(supplierDAO.getAllSuppliers());
    }
    
    private void prepareAdd() {
        clearForm();
        setFormEnabled(true);
        addButton.setEnabled(false);
        editButton.setEnabled(false);
        deleteButton.setEnabled(false);
        saveButton.setEnabled(true);
        cancelButton.setEnabled(true);
        supplierTable.clearSelection();
    }
    
    private void prepareEdit() {
        int selectedRow = supplierTable.getSelectedRow();
        if (selectedRow >= 0) {
            Supplier supplier = tableModel.getSupplierAt(selectedRow);
            nameField.setText(supplier.getName());
            contactField.setText(supplier.getContactPerson());
            emailField.setText(supplier.getEmail());
            phoneField.setText(supplier.getPhone());
            addressArea.setText(supplier.getAddress());
            
            setFormEnabled(true);
            addButton.setEnabled(false);
            editButton.setEnabled(false);
            deleteButton.setEnabled(false);
            saveButton.setEnabled(true);
            cancelButton.setEnabled(true);
        }
    }
    
    private void deleteSupplier() {
        int selectedRow = supplierTable.getSelectedRow();
        if (selectedRow >= 0) {
            Supplier supplier = tableModel.getSupplierAt(selectedRow);
            int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete " + supplier.getName() + "?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION
            );
            
            if (confirm == JOptionPane.YES_OPTION) {
                supplierDAO.deleteSupplier(supplier);
                loadData();
                clearForm();
            }
        }
    }
    
    private void saveSupplier() {
        Supplier supplier;
        int selectedRow = supplierTable.getSelectedRow();
        
        if (selectedRow >= 0) {
            supplier = tableModel.getSupplierAt(selectedRow);
        } else {
            supplier = new Supplier();
        }
        
        supplier.setName(nameField.getText());
        supplier.setContactPerson(contactField.getText());
        supplier.setEmail(emailField.getText());
        supplier.setPhone(phoneField.getText());
        supplier.setAddress(addressArea.getText());
        
        supplierDAO.saveSupplier(supplier);
        
        loadData();
        clearForm();
        setFormEnabled(false);
        addButton.setEnabled(true);
        editButton.setEnabled(false);
        deleteButton.setEnabled(false);
        saveButton.setEnabled(false);
        cancelButton.setEnabled(false);
    }
    
    private void cancelEdit() {
        clearForm();
        setFormEnabled(false);
        addButton.setEnabled(true);
        editButton.setEnabled(false);
        deleteButton.setEnabled(false);
        saveButton.setEnabled(false);
        cancelButton.setEnabled(false);
    }
    
    private void clearForm() {
        nameField.setText("");
        contactField.setText("");
        emailField.setText("");
        phoneField.setText("");
        addressArea.setText("");
    }
    
    private void setFormEnabled(boolean enabled) {
        nameField.setEnabled(enabled);
        contactField.setEnabled(enabled);
        emailField.setEnabled(enabled);
        phoneField.setEnabled(enabled);
        addressArea.setEnabled(enabled);
    }
    
    private class SupplierTableModel extends AbstractTableModel {
        private static final long serialVersionUID = 1L;
        
        private final String[] columnNames = {"ID", "Name", "Contact Person", "Email", "Phone"};
        private List<Supplier> suppliers;
        
        public SupplierTableModel() {
            this.suppliers = List.of();
        }
        
        public void refresh(List<Supplier> suppliers) {
            this.suppliers = suppliers;
            fireTableDataChanged();
        }
        
        public Supplier getSupplierAt(int row) {
            if (row >= 0 && row < suppliers.size()) {
                return suppliers.get(row);
            }
            return null;
        }
        
        @Override
        public int getRowCount() {
            return suppliers.size();
        }
        
        @Override
        public int getColumnCount() {
            return columnNames.length;
        }
        
        @Override
        public String getColumnName(int column) {
            return columnNames[column];
        }
        
        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            Supplier supplier = suppliers.get(rowIndex);
            
            return switch (columnIndex) {
                case 0 -> supplier.getId();
                case 1 -> supplier.getName();
                case 2 -> supplier.getContactPerson();
                case 3 -> supplier.getEmail();
                case 4 -> supplier.getPhone();
                default -> null;
            };
        }
    }
}
