/**
 * FXML Controller class
 *
 * @author Stephen Moyer
 */
package Model;

import java.util.ArrayList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;


// Inventory Class
public class Inventory {

    //Added form first submission
    private ArrayList<Product> allProducts;
    private ArrayList<Part> allParts;

    public Inventory() {
        allProducts = new ArrayList<>();
        allParts = new ArrayList<>();
    }

    //Add a new product
    public void addProduct(Product productToAdd) {
        if (productToAdd != null) {
            this.allProducts.add(productToAdd);
        }
    }

    
    //Remove product
    public boolean removeProduct(int productToRemove) {
        for (int i = 0; i < allProducts.size(); i++) {
            if (allProducts.get(i).getProductID() == productToRemove) {
                allProducts.remove(i);
                return true;
            }
        }
        return false;
    }

    
    public Product findProducts(int productToSearch) {
        if (!allProducts.isEmpty()) {
            for (int i = 0; i < allProducts.size(); i++) {
                if (allProducts.get(i).getProductID() == productToSearch) {
                    return allProducts.get(i);
                }
            }
        }
        return null;
    }

    public ObservableList<Part> findProducts(String productNameToLookUp) {
        return null;
    }
    
    public void updateProduct(Product product) {
        for (int i = 0; i < allProducts.size(); i++) {
            if (allProducts.get(i).getProductID() == product.getProductID()) {
                allProducts.set(i, product);
                break;
            }
        }
        return;
    }
    
    //Add Part
    public void addPart(Part partToAdd) {
        
        if (partToAdd != null) {
            allParts.add(partToAdd);
        }
    }

    //Delete Parts
    public boolean deletePart(Part partToDelete) {
        
        for (int i = 0; i < allParts.size(); i++) {
            if (allParts.get(i).getPartID() == partToDelete.getPartID()) {
                allParts.remove(i);
                return true;
            }
        }
        return false;
    }

    public Part lookUpPart(int partToLookUp) {
        
        if (!allParts.isEmpty()) {
            
            for (int k = 0; k < allParts.size(); k++) {
                
                if (allParts.get(k).getPartID() == partToLookUp) {
                    return allParts.get(k);
                }
            }

        }
        return null;
    }
    
    public int partListSize() {
        return allParts.size();
    }
    
    public ArrayList<Product> getAllProducts() {
        return allProducts;
    }
    
    public ArrayList<Part> getAllParts() {
        return allParts;
    }

    public int productListSize() {
        return allProducts.size();
    }

    public ObservableList<Part> lookUpPart(String partNameToLookUp) {
        
        if (!allParts.isEmpty()) {
            ObservableList searchPartsList = FXCollections.observableArrayList();
             for (Part p : getAllParts()) {
                 
                if (p.getName().contains(partNameToLookUp)) {
                    searchPartsList.add(p);
                }
            }
             return searchPartsList;
        }
        return null;
    }

    public void updatePart(Part partToUpdate) {
        for (int i = 0; i < allParts.size(); i++) {
            if (allParts.get(i).getPartID() == partToUpdate.partID) {
                allParts.set(i, partToUpdate);
                break;
            }
        }
        return;
    }

}