
package utils;

import java.io.*;
import java.util.*;
import models.Product;
import dao.ProductDAO;

public class CsvUtils {
    
    public static void exportToCsv(List<Product> products, File file) throws IOException {
        try (PrintWriter writer = new PrintWriter(file)) {
            // Write header
            writer.println("ID,Name,Description,Price,Quantity,Sold");
            
            // Write data
            for (Product product : products) {
                writer.println(
                    product.getId() + "," +
                    escapeCSV(product.getName()) + "," +
                    escapeCSV(product.getDescription()) + "," +
                    product.getPrice() + "," +
                    product.getQuantity() + "," +
                    product.getSold()
                );
            }
        }
    }
    
    public static List<Product> importFromCsv(File file, ProductDAO productDAO) throws IOException {
        List<Product> importedProducts = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            // Skip header
            reader.readLine();
            
            while ((line = reader.readLine()) != null) {
                String[] data = parseCSVLine(line);
                if (data.length >= 6) {
                    Product product = new Product(
                        Integer.parseInt(data[0]),
                        data[1],
                        data[2],
                        Double.parseDouble(data[3]),
                        Integer.parseInt(data[4]),
                        Integer.parseInt(data[5])
                    );
                    importedProducts.add(product);
                    productDAO.saveProduct(product);
                }
            }
        }
        
        return importedProducts;
    }
    
    private static String escapeCSV(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
    
    private static String[] parseCSVLine(String line) {
        List<String> result = new ArrayList<>();
        StringBuilder currentField = new StringBuilder();
        boolean inQuotes = false;
        
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (inQuotes) {
                if (c == '\"') {
                    if (i + 1 < line.length() && line.charAt(i + 1) == '\"') {
                        // Two double quotes - add one quote to the field
                        currentField.append('\"');
                        i++;
                    } else {
                        // End of quoted section
                        inQuotes = false;
                    }
                } else {
                    currentField.append(c);
                }
            } else {
                if (c == '\"') {
                    inQuotes = true;
                } else if (c == ',') {
                    result.add(currentField.toString());
                    currentField.setLength(0);
                } else {
                    currentField.append(c);
                }
            }
        }
        
        result.add(currentField.toString());
        return result.toArray(new String[0]);
    }
}
