
package ui;

import models.Product;
import javax.swing.table.AbstractTableModel;
import java.util.List;

public class ProductTableModel extends AbstractTableModel {
    private static final long serialVersionUID = 1L;
    
    private final List<Product> products;
    private final String[] columnNames = {"ID", "Name", "Description", "Price", "Quantity", "Sold"};
    
    public ProductTableModel(List<Product> products) {
        this.products = products;
    }
    
    @Override
    public int getRowCount() {
        return products.size();
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
        Product product = products.get(rowIndex);
        
        switch (columnIndex) {
            case 0: return product.getId();
            case 1: return product.getName();
            case 2: return product.getDescription();
            case 3: return String.format("$%.2f", product.getPrice());
            case 4: return product.getQuantity();
            case 5: return product.getSold();
            default: return null;
        }
    }
    
    public Product getProductAt(int rowIndex) {
        if (rowIndex >= 0 && rowIndex < products.size()) {
            return products.get(rowIndex);
        }
        return null;
    }
    
    public void refreshData(List<Product> products) {
        this.products.clear();
        this.products.addAll(products);
        fireTableDataChanged();
    }
}
package ui;

import java.util.List;
import javax.swing.table.AbstractTableModel;
import models.Product;
import dao.SupplierDAO;

public class ProductTableModel extends AbstractTableModel {
    private static final long serialVersionUID = 1L;
    
    private final String[] COLUMN_NAMES = {"ID", "Name", "Description", "Category", "Price", "Quantity", "Sold"};
    private List<Product> products;
    private SupplierDAO supplierDAO;
    
    public ProductTableModel(List<Product> products) {
        this.products = products;
    }
    
    public ProductTableModel(List<Product> products, SupplierDAO supplierDAO) {
        this.products = products;
        this.supplierDAO = supplierDAO;
    }
    
    public void refreshData(List<Product> products) {
        this.products = products;
        fireTableDataChanged();
    }
    
    @Override
    public int getRowCount() {
        return products.size();
    }
    
    @Override
    public int getColumnCount() {
        return COLUMN_NAMES.length;
    }
    
    @Override
    public String getColumnName(int column) {
        return COLUMN_NAMES[column];
    }
    
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Product product = products.get(rowIndex);
        
        return switch (columnIndex) {
            case 0 -> product.getId();
            case 1 -> product.getName();
            case 2 -> product.getDescription();
            case 3 -> product.getCategory();
            case 4 -> String.format("$%.2f", product.getPrice());
            case 5 -> product.getQuantity();
            case 6 -> product.getSold();
            default -> null;
        };
    }
    
    public Product getProductAt(int row) {
        if (row >= 0 && row < products.size()) {
            return products.get(row);
        }
        return null;
    }
}
