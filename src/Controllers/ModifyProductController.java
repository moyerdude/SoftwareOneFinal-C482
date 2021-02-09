/**
 * FXML Controller class
 *
 * @author Stephen Moyer
 */
package Controllers;

import Controllers.MainScreenController;
import Controllers.AlertMessage;
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
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;


    public class ModifyProductController implements Initializable {

    Inventory inv;
    Product product;

    @FXML
    private TextField modifyProductID;
    @FXML
    private TextField modifyProductName;
    @FXML
    private TextField modifyProductPrice;
    @FXML
    private TextField modifyProductINVCount;
    @FXML
    private TextField modifyProductMin;
    @FXML
    private TextField modifyProductMax;
    @FXML
    private TableView<Part> assocPartsTable;
    @FXML
    private TableView<Part> partSearchTable;
    @FXML
    private TextField search;
    private ObservableList<Part> partsInventory = FXCollections.observableArrayList();
    private ObservableList<Part> partsInventorySearch = FXCollections.observableArrayList();
    private ObservableList<Part> assocPartList = FXCollections.observableArrayList();

    public ModifyProductController(Inventory inv, Product product) {
        this.inv = inv;
        this.product = product;
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        populateSearchTable();
        setData();
    }

    private void populateSearchTable() {
        partsInventory.setAll(inv.getAllParts());

        TableColumn<Part, Double> costCol = formatPrice();
        partSearchTable.getColumns().addAll(costCol);

        partSearchTable.setItems(partsInventory);
        partSearchTable.refresh();
    }

    @FXML
    void clearTextField(MouseEvent event) {
        Object source = event.getSource();
        TextField field = (TextField) source;
        field.setText("");
        if (field == search) {
            partSearchTable.setItems(partsInventory);
        }
    }

    @FXML
    private void modifyProductSearch(MouseEvent event) {
        if (search.getText() != null && search.getText().trim().length() != 0) {
            partsInventorySearch.clear();
            for (Part p : inv.getAllParts()) {
                if (p.getName().contains(search.getText().trim())) {
                    partsInventorySearch.add(p);
                }
            }
            partSearchTable.setItems(partsInventorySearch);
        }
    }

    @FXML
    private void deletePart(MouseEvent event) {
        Part removePart = assocPartsTable.getSelectionModel().getSelectedItem();
        boolean deleted = false;
        if (removePart != null) {
            boolean remove = confirmationWindow(removePart.getName());
            if (remove) {
                deleted = product.removeAssociatedPart(removePart.getPartID());
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
    private void addPart(MouseEvent event) {
        Part addPart = partSearchTable.getSelectionModel().getSelectedItem();
        boolean repeatedItem = false;

        if (addPart == null) {
            return;
        } else {
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
            assocPartsTable.setItems(assocPartList);
        }
    }

    @FXML
    private void cancelModify(MouseEvent event) {
        boolean cancel = AlertMessage.cancel();
        if (cancel) {
            mainScreen(event);
        } else {
            return;
        }
    }

    @FXML
    private void saveProduct(MouseEvent event) {
        resetFieldsStyle();
        boolean end = false;
        TextField[] fieldCount = {modifyProductINVCount, modifyProductPrice, modifyProductMin, modifyProductMax};
        double minCost = 0;
        for (int i = 0; i < assocPartList.size(); i++) {
            minCost += assocPartList.get(i).getPrice();
        }
        if (modifyProductName.getText().trim().isEmpty() || modifyProductName.getText().trim().toLowerCase().equals("part name")) {
            AlertMessage.errorProduct(4, modifyProductName);
            return;
        }
        for (int i = 0; i < fieldCount.length; i++) {
            boolean valueError = checkValue(fieldCount[i]);
            if (valueError) {
                end = true;
                break;
            }
            boolean typeError = checkType(fieldCount[i]);
            if (typeError) {
                end = true;
                break;
            }
        }
        if (Integer.parseInt(modifyProductMin.getText().trim()) > Integer.parseInt(modifyProductMax.getText().trim())) {
            AlertMessage.errorProduct(10, modifyProductMin);
            return;
        }
        if (Integer.parseInt(modifyProductINVCount.getText().trim()) < Integer.parseInt(modifyProductMin.getText().trim())) {
            AlertMessage.errorProduct(8, modifyProductINVCount);
            return;
        }
        if (Integer.parseInt(modifyProductINVCount.getText().trim()) > Integer.parseInt(modifyProductMax.getText().trim())) {
            AlertMessage.errorProduct(9, modifyProductINVCount);
            return;
        }
        if (Double.parseDouble(modifyProductPrice.getText().trim()) < minCost) {
            AlertMessage.errorProduct(6, modifyProductPrice);
            return;
        }
        if (assocPartList.size() == 0) {
            AlertMessage.errorProduct(7, null);
            return;
        }

        saveProduct();
        mainScreen(event);

    }

    private void saveProduct() {
        Product product = new Product(Integer.parseInt(modifyProductID.getText().trim()), modifyProductName.getText().trim(), Double.parseDouble(modifyProductPrice.getText().trim()),
                Integer.parseInt(modifyProductINVCount.getText().trim()), Integer.parseInt(modifyProductMin.getText().trim()), Integer.parseInt(modifyProductMax.getText().trim()));
        for (int i = 0; i < assocPartList.size(); i++) {
            product.addAssociatedPart(assocPartList.get(i));
        }

        inv.updateProduct(product);

    }

    private void setData() {
        for (int i = 0; i < 1000; i++) {
            Part part = product.lookupAssociatedPart(i);
            if (part != null) {
                assocPartList.add(part);
            }
        }

        TableColumn<Part, Double> costCol = formatPrice();
        assocPartsTable.getColumns().addAll(costCol);

        assocPartsTable.setItems(assocPartList);

        this.modifyProductName.setText(product.getName());
        this.modifyProductID.setText((Integer.toString(product.getProductID())));
        this.modifyProductINVCount.setText((Integer.toString(product.getInStock())));
        this.modifyProductPrice.setText((Double.toString(product.getPrice())));
        this.modifyProductMin.setText((Integer.toString(product.getMin())));
        this.modifyProductMax.setText((Integer.toString(product.getMax())));

    }

    private void resetFieldsStyle() {
        modifyProductName.setStyle("-fx-border-color: lightgray");
        modifyProductINVCount.setStyle("-fx-border-color: lightgray");
        modifyProductPrice.setStyle("-fx-border-color: lightgray");
        modifyProductMin.setStyle("-fx-border-color: lightgray");
        modifyProductMax.setStyle("-fx-border-color: lightgray");

    }

    private boolean confirmationWindow(String name) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Delete part");
        alert.setHeaderText("Are you sure you want to delete: " + name);
        alert.setContentText("Click ok to confirm");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            return true;
        } else {
            return false;
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
        } catch (IOException e) {

        }
    }

    private boolean checkValue(TextField field) {
        boolean error = false;
        try {
            if (field.getText().trim().isEmpty() || field.getText().trim() == null) {
                AlertMessage.errorProduct(1, field);
                return true;
            }
            if (field == modifyProductPrice && Double.parseDouble(field.getText().trim()) < 0) {
                AlertMessage.errorProduct(5, field);
                error = true;
            }
        } catch (Exception e) {
            error = true;
            AlertMessage.errorProduct(3, field);
            System.out.println(e);

        }
        return error;
    }

    private boolean checkType(TextField field) {

        if (field == modifyProductPrice & !field.getText().trim().matches("\\d+(\\.\\d+)?")) {
            AlertMessage.errorProduct(3, field);
            return true;
        }
        if (field != modifyProductPrice & !field.getText().trim().matches("[0-9]*")) {
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