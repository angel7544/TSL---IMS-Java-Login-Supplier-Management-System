
package dao;

import models.Product;
import java.io.*;
import java.util.*;

public class ProductDAO {
    private static final String FILE_NAME = "products.dat";
    private List<Product> products;
    private int nextId = 1;

    public ProductDAO() {
        this.products = new ArrayList<>();
        loadData();
    }
    
    public List<Product> getAllProducts() {
        return new ArrayList<>(products);
    }
    
    public Product getProductById(int id) {
        for (Product product : products) {
            if (product.getId() == id) {
                return product;
            }
        }
        return null;
    }
    
    public void addProduct(Product product) {
        if (product.getId() == 0) {
            product.setId(nextId++);
        }
        products.add(product);
        saveData();
    }
    
    public void updateProduct(Product product) {
        for (int i = 0; i < products.size(); i++) {
            if (products.get(i).getId() == product.getId()) {
                products.set(i, product);
                saveData();
                return;
            }
        }
    }
    
    public void deleteProduct(int id) {
        products.removeIf(product -> product.getId() == id);
        saveData();
    }
    
    public List<Product> getLowStockProducts(int threshold) {
        List<Product> lowStock = new ArrayList<>();
        for (Product product : products) {
            if (product.getQuantity() <= threshold) {
                lowStock.add(product);
            }
        }
        return lowStock;
    }
    
    public Product getTopSellingProduct() {
        if (products.isEmpty()) return null;
        
        Product topSelling = products.get(0);
        for (Product product : products) {
            if (product.getSold() > topSelling.getSold()) {
                topSelling = product;
            }
        }
        return topSelling;
    }
    
    @SuppressWarnings("unchecked")
    private void loadData() {
        File file = new File(FILE_NAME);
        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                products = (List<Product>) ois.readObject();
                
                // Find the next available ID
                nextId = 1;
                for (Product product : products) {
                    if (product.getId() >= nextId) {
                        nextId = product.getId() + 1;
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("Error loading data: " + e.getMessage());
                products = new ArrayList<>();
            }
        }
    }
    
    private void saveData() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(products);
        } catch (IOException e) {
            System.err.println("Error saving data: " + e.getMessage());
        }
    }
    
    public void backup(String filename) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(products);
        }
    }
    
    @SuppressWarnings("unchecked")
    public void restore(String filename) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
            products = (List<Product>) ois.readObject();
            
            // Update nextId
            nextId = 1;
            for (Product product : products) {
                if (product.getId() >= nextId) {
                    nextId = product.getId() + 1;
                }
            }
            
            // Save to the main data file
            saveData();
        }
    }
}
