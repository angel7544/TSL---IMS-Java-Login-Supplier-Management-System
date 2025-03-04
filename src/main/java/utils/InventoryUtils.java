
package utils;

import java.util.List;
import models.Product;

public class InventoryUtils {
    
    public static double calculateTotalInventoryValue(List<Product> products) {
        return products.stream()
            .mapToDouble(product -> product.getPrice() * product.getQuantity())
            .sum();
    }
    
    public static String formatCurrency(double amount) {
        return String.format("$%.2f", amount);
    }
}
