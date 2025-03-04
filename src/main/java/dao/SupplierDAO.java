
package dao;

import java.io.*;
import java.util.*;
import models.Supplier;

public class SupplierDAO {
    private static final String DATA_FILE = "suppliers.dat";
    private List<Supplier> suppliers;
    private int nextId = 1;
    
    public SupplierDAO() {
        loadData();
    }
    
    public List<Supplier> getAllSuppliers() {
        return new ArrayList<>(suppliers);
    }
    
    public Supplier getSupplierById(int id) {
        return suppliers.stream()
            .filter(supplier -> supplier.getId() == id)
            .findFirst()
            .orElse(null);
    }
    
    public void saveSupplier(Supplier supplier) {
        if (supplier.getId() == 0) {
            supplier.setId(nextId++);
            suppliers.add(supplier);
        } else {
            for (int i = 0; i < suppliers.size(); i++) {
                if (suppliers.get(i).getId() == supplier.getId()) {
                    suppliers.set(i, supplier);
                    break;
                }
            }
        }
        saveData();
    }
    
    public void deleteSupplier(Supplier supplier) {
        suppliers.removeIf(p -> p.getId() == supplier.getId());
        saveData();
    }
    
    @SuppressWarnings("unchecked")
    private void loadData() {
        File file = new File(DATA_FILE);
        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                suppliers = (List<Supplier>) ois.readObject();
                
                // Find the highest ID for nextId
                for (Supplier supplier : suppliers) {
                    if (supplier.getId() >= nextId) {
                        nextId = supplier.getId() + 1;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                suppliers = new ArrayList<>();
            }
        } else {
            suppliers = new ArrayList<>();
        }
    }
    
    private void saveData() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            oos.writeObject(suppliers);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
