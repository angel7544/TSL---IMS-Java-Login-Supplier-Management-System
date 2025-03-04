
package models;

import java.io.Serializable;

public class Product implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int id;
    private String name;
    private String description;
    private double price;
    private int quantity;
    private int sold;
    private String category;
    private int supplierId;
    
    public Product() {
        this(0, "", "", 0.0, 0, 0, "", 0);
    }
    
    public Product(int id, String name, String description, double price, int quantity, int sold) {
        this(id, name, description, price, quantity, sold, "", 0);
    }
    
    public Product(int id, String name, String description, double price, int quantity, int sold, String category, int supplierId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.quantity = quantity;
        this.sold = sold;
        this.category = category;
        this.supplierId = supplierId;
    }
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public double getPrice() {
        return price;
    }
    
    public void setPrice(double price) {
        this.price = price;
    }
    
    public int getQuantity() {
        return quantity;
    }
    
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    
    public int getSold() {
        return sold;
    }
    
    public void setSold(int sold) {
        this.sold = sold;
    }
    
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
    
    public int getSupplierId() {
        return supplierId;
    }
    
    public void setSupplierId(int supplierId) {
        this.supplierId = supplierId;
    }
    
    public void sellItems(int quantity) {
        if (this.quantity >= quantity) {
            this.quantity -= quantity;
            this.sold += quantity;
        }
    }
    
    public void restock(int quantity) {
        this.quantity += quantity;
    }
    
    @Override
    public String toString() {
        return name;
    }
}
