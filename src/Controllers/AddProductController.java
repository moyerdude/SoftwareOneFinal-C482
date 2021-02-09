/**
 * FXML Controller class
 *
 * @author Stephen Moyer
 */
package Controllers;

import Model.Inventory;
import Model.Part;
import Model.Product;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.ResourceBundle;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;


public class AddProductController implements Initializable {

    Inventory inv;

    @FXML
    private TextField addProductID;
    @FXML
    private TextField addProductName;
    @FXML
    private TextField addProductPrice;
    @FXML
    private TextField addProductINVCount;
    @FXML
    private TextField addProductMin;
    @FXML
    private TextField addProductMax;
    @FXML
    private TextField search;
    @FXML
    private TableView<Part> partSearchTable;
    @FXML
    private TableView<Part> assocPartsTable;
    private ObservableList<Part> partsInventory = FXCollections.observableArrayList();
    private ObservableList<Part> partsInventorySearch = FXCollections.observableArrayList();
    private ObservableList<Part> assocPartList = FXCollections.observableArrayList();

    public AddProductController(Inventory inv) {
        this.inv = inv;
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        generateProductID();
        populateSearchTable();
    }

    private void generateProductID() {
        boolean match;
        Random randomNum = new Random();
        Integer num = randomNum.nextInt(1000);


        if (inv.productListSize() == 0) {
            addProductID.setText(num.toString());

        }
        if (inv.productListSize() == 1000) {
            AlertMessage.errorProduct(3, null);
        } else {
            match = generateNum(num);

            if (match == false) {
                addProductID.setText(num.toString());
            } else {
                generateProductID();
            }
        }

        addProductID.setText(num.toString());
    }

    private boolean generateNum(Integer num) {
        Part match = inv.lookUpPart (num);
        return match != null;
    }


    private void populateSearchTable() {
        
        partsInventory.setAll(inv.getAllParts());
        TableColumn<Part, Double> costCol = formatPrice();
        partSearchTable.getColumns().addAll(costCol);
        partSearchTable.setItems(partsInventory);
        partSearchTable.refresh();
    }

    @FXML
    private void clearTextField(MouseEvent event) {
        
        Object source = event.getSource();
        TextField field = (TextField) source;
        field.setText("");
    }

    @FXML
    private void searchForPart(MouseEvent event) {
        
        if (!search.getText().trim().isEmpty()) {
            partsInventory.clear();
            partSearchTable.setItems(inv.lookUpPart(search.getText().trim()));
            partSearchTable.refresh();
        }
    }

    @FXML
    private void addPart(MouseEvent event) {
        
        Part addPart = partSearchTable.getSelectionModel().getSelectedItem();
        boolean repeatedItem = false;

        if (addPart != null) {
            int id = addPart.getPartID();
            for (int i = 0; i < assocPartList.size(); i++) {
                if (assocPartList.get(i).getPartID() == id) {
                    AlertMessage.errorProduct(2, null);
                    repeatedItem = true;
                }
            }

            if (!repeatedItem) {
                assocPartList.add(addPart);

            }

            TableColumn<Part, Double> costCol = formatPrice();
            assocPartsTable.getColumns().addAll(costCol);

            assocPartsTable.setItems(assocPartList);
        }
    }

    @FXML
    private void deletePart(MouseEvent event) {
        
        Part removePart = assocPartsTable.getSelectionModel().getSelectedItem();
        boolean deleted = false;
        if (removePart != null) {
            boolean remove = AlertMessage.confirmationWindow(removePart.getName());
            if (remove) {
                assocPartList.remove(removePart);
                assocPartsTable.refresh();
            }
        } else {
            return;
        }
        if (deleted) {
            AlertMessage.infoWindow(1, removePart.getName());
        } else {
            AlertMessage.infoWindow(2, "");
        }

    }


    @FXML
    private void cancelAddProduct(MouseEvent event) {
        
        boolean cancel = AlertMessage.cancel();
        if (cancel) {
            mainScreen(event);
        }
    }

    @FXML
    private void saveAddProduct(MouseEvent event) {
        
        resetFieldsStyle();
        boolean end = false;
        TextField[] fieldCount = {addProductINVCount, addProductPrice, addProductMin, addProductMax};
        double minCost = 0;
        for (int i = 0; i < assocPartList.size(); i++) {
            minCost += assocPartList.get(i).getPrice();
        }
        if (addProductName.getText().trim().isEmpty() || addProductName.getText().trim().toLowerCase().equals("part name")) {
            
            AlertMessage.errorProduct(4, addProductName);
            return;
        }
        for (TextField fieldCount1 : fieldCount) {
            boolean valueError = checkValue(fieldCount1);
            if (valueError) {
                end = true;
                break;
            }
            boolean typeError = checkType(fieldCount1);
            if (typeError) {
                
                end = true;
                break;
            }
        }
        if (Integer.parseInt(addProductMin.getText().trim()) > Integer.parseInt(addProductMax.getText().trim())) {
            
            AlertMessage.errorProduct(10, addProductMin);
            return;
        }
        if (Integer.parseInt(addProductINVCount.getText().trim()) < Integer.parseInt(addProductMin.getText().trim())) {
            
            AlertMessage.errorProduct(8, addProductINVCount);
            return;
        }
        if (Integer.parseInt(addProductINVCount.getText().trim()) > Integer.parseInt(addProductMax.getText().trim())) {
            
            AlertMessage.errorProduct(9, addProductINVCount);
            return;
        }
        if (Double.parseDouble(addProductPrice.getText().trim()) < minCost) {
            
            AlertMessage.errorProduct(6, addProductPrice);
            return;
        }
        if (assocPartList.isEmpty()) {
            
            AlertMessage.errorProduct(7, null);
            return;
        }

        saveProduct();
        mainScreen(event);

    }

    private void fieldError(TextField field) {
        
        if (field != null) {
            
            field.setStyle("-fx-border-color: red");
            
        }
    }

    private void saveProduct() {
        
                 Product product = new Product(Integer.parseInt(addProductID.getText().trim()), addProductName.getText().trim(), Double.parseDouble(addProductPrice.getText().trim()),
                Integer.parseInt(addProductINVCount.getText().trim()), Integer.parseInt(addProductMin.getText().trim()), Integer.parseInt(addProductMax.getText().trim()));
        
        for (int i = 0; i < assocPartList.size(); i++) {
            
            product.addAssociatedPart(assocPartList.get(i));
        }

        inv.addProduct(product);

    }

    private void resetFieldsStyle() {
        
        
        addProductName.setStyle("-fx-border-color: lightgray");
        addProductINVCount.setStyle("-fx-border-color: lightgray");
        addProductPrice.setStyle("-fx-border-color: lightgray");
        addProductMin.setStyle("-fx-border-color: lightgray");
        addProductMax.setStyle("-fx-border-color: lightgray");

    }

    @FXML
    void clearField(MouseEvent event) {
        
        search.setText("");
        
        if (!partsInventory.isEmpty()) {
            
            partSearchTable.setItems(partsInventory);
            
        }
        

    }

    private void mainScreen(Event event) {
        try {
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/MainScreen.fxml"));
            MainScreenController controller = new MainScreenController(inv);

            loader.setController(controller);
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();
            
        } 
        
        catch (IOException e) {

        }
    }

    private boolean checkValue(TextField field) {
        
        boolean error = false;
        
        try {
            if (field.getText().trim().isEmpty() || field.getText().trim() == null) {
                
                AlertMessage.errorProduct(1, field);
                return true;
            }
            if (field == addProductPrice && Double.parseDouble(field.getText().trim()) < 0) {
                
                AlertMessage.errorProduct(5, field);
                error = true;
            }
        } 
        
        catch (NumberFormatException e) {
            
            error = true;
            AlertMessage.errorProduct(3, field);
            System.out.println(e);

        }
        return error;
    }

    private boolean checkType(TextField field) {

        if (field == addProductPrice & !field.getText().trim().matches("\\d+(\\.\\d+)?")) {
            
            AlertMessage.errorProduct(3, field);
            return true;
        }
        if (field != addProductPrice & !field.getText().trim().matches("[0-9]*")) {
            
            AlertMessage.errorProduct(3, field);
            return true;
        }
        return false;

    }
    
    private <T> TableColumn<T, Double> formatPrice() {
        TableColumn<T, Double> costCol = new TableColumn("Price");
        costCol.setCellValueFactory(new PropertyValueFactory<>("Price"));
        // Format as currency
        costCol.setCellFactory((TableColumn<T, Double> column) -> {
            return new TableCell<T, Double>() {
                @Override
                protected void updateItem(Double item, boolean empty) {
                    if (!empty) {
                        setText("$" + String.format("%10.2f", item));
                    }
                    else{
                        setText("");
                    }
                }
            };
        });
        return costCol;
    }

}