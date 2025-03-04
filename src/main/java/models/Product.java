
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

    public Product(int id, String name, String description, double price, int quantity) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.quantity = quantity;
        this.sold = 0;
    }

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    
    public int getSold() { return sold; }
    public void setSold(int sold) { this.sold = sold; }
    
    public void addStock(int amount) {
        this.quantity += amount;
    }
    
    public boolean removeStock(int amount) {
        if (this.quantity >= amount) {
            this.quantity -= amount;
            this.sold += amount;
            return true;
        }
        return false;
    }
    
    @Override
    public String toString() {
        return name;
    }
}
