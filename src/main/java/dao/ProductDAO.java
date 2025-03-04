package dao;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import models.Product;

public class ProductDAO {
    private static final String DATA_FILE = "products.dat";
    private List<Product> products;
    private int nextId = 1;

    public ProductDAO() {
        loadData();
    }

    public List<Product> getAllProducts() {
        return new ArrayList<>(products);
    }

    public List<Product> filterProducts(String searchTerm) {
        if (searchTerm == null || searchTerm.isEmpty()) {
            return getAllProducts();
        }

        String lowerSearchTerm = searchTerm.toLowerCase();
        return products.stream()
            .filter(p -> 
                p.getName().toLowerCase().contains(lowerSearchTerm) ||
                p.getDescription().toLowerCase().contains(lowerSearchTerm) ||
                p.getCategory().toLowerCase().contains(lowerSearchTerm))
            .collect(Collectors.toList());
    }

    public List<Product> getProductsByCategory(String category) {
        if (category == null || category.isEmpty()) {
            return getAllProducts();
        }

        return products.stream()
            .filter(p -> p.getCategory().equalsIgnoreCase(category))
            .collect(Collectors.toList());
    }

    public List<String> getAllCategories() {
        return products.stream()
            .map(Product::getCategory)
            .filter(c -> c != null && !c.isEmpty())
            .distinct()
            .sorted()
            .collect(Collectors.toList());
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
        return products.stream()
            .filter(p -> p.getQuantity() <= threshold)
            .collect(Collectors.toList());
    }

    public Map<String, Double> getCategorySalesData() {
        Map<String, Double> categorySales = new HashMap<>();

        for (Product product : products) {
            String category = product.getCategory();
            if (category == null || category.isEmpty()) {
                category = "Uncategorized";
            }

            double salesValue = product.getSold() * product.getPrice();
            categorySales.put(category, categorySales.getOrDefault(category, 0.0) + salesValue);
        }

        return categorySales;
    }

    public Map<Integer, List<Product>> getProductsBySupplier() {
        return products.stream()
            .filter(p -> p.getSupplierId() > 0)
            .collect(Collectors.groupingBy(Product::getSupplierId));
    }

    public double getTotalInventoryValue() {
        return products.stream()
            .mapToDouble(p -> p.getQuantity() * p.getPrice())
            .sum();
    }

    @SuppressWarnings("unchecked")
    private void loadData() {
        File file = new File(DATA_FILE);
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
        } else {
            products = new ArrayList<>();
        }
    }

    private void saveData() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
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
}