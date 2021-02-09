/**
 * FXML Controller class
 *
 * @author Stephen Moyer
 */
package Controllers;

import Model.InHouse;
import Model.Inventory;
import Model.OutSourced;
import Model.Part;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Random;
import java.util.ResourceBundle;


public class AddPartController implements Initializable {

    Inventory inv;

    @FXML
    private RadioButton inHouseRadio;
    
    @FXML
    private RadioButton outSourcedRadio;
    
    @FXML
    private TextField addPartsID;
    
    @FXML
    private TextField addPartsName;
    
    @FXML
    private TextField countINV;
    
    @FXML
    private TextField addPartsPrice;
    
    @FXML
    private TextField addPartsMax;
    
    @FXML
    private TextField companyNameField;
    
    @FXML
    private Label companyLabel;
    
    @FXML
    private TextField addPartsMin;

    
    
    
    public AddPartController(Inventory inv) {
        this.inv = inv;
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        generatePartID();
        resetFields();
        
    }

    private void resetFields() {
        
        
        addPartsName.setText("Part Name");
        countINV.setText("Inv Count");
        addPartsPrice.setText("Part Price");
        addPartsMin.setText("Min");
        addPartsMax.setText("Max");
        companyNameField.setText("Machine ID");
        companyLabel.setText("Machine ID");
        inHouseRadio.setSelected(true);
        
    }

    private void generatePartID() {
        
        
        boolean match;
        Random randomNum = new Random();
        Integer num = randomNum.nextInt(1000);

        if (inv.partListSize() == 0) {
            addPartsID.setText(num.toString());

        }
        if (inv.partListSize() == 1000) {
            AlertMessage.errorPart(3, null);
        } 
        else {
            match = verifyIfTaken(num);

            if (match == false) {
                addPartsID.setText(num.toString());
            } 
            else {
                generatePartID();
            }
        }
    }

    private boolean verifyIfTaken(Integer num) {
        
        Part match = inv.lookUpPart(num);
        return match != null;
        
    }

    @FXML
    private void clearTextField(MouseEvent event) {
        
        Object source = event.getSource();
        TextField field = (TextField) source;
        field.setText("");
    }

    @FXML
    private void selectInHouse(MouseEvent event) {
        
        companyLabel.setText("Machine ID");
        companyNameField.setText("Machine ID");
    }

    @FXML
    private void selectOutSourced(MouseEvent event) {
        
        companyLabel.setText("Company Name");
        companyNameField.setText("Company Name");
    }

    @FXML
    private void idDisabled(MouseEvent event) {
    }

    @FXML
    private void cancelAddPart(MouseEvent event) {
        
        boolean cancel = AlertMessage.cancel();
        
        if (cancel) {
            mainScreen(event);
        }
    }

    @FXML
    private void saveAddPart(MouseEvent event) {
        
        resetFieldsStyle();
        boolean end = false;
        
        TextField[] fieldCount = {countINV, addPartsPrice, addPartsMin, addPartsMax};
        
        if (inHouseRadio.isSelected() || outSourcedRadio.isSelected()) {
            
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
            if (addPartsName.getText().trim().isEmpty() || addPartsName.getText().trim().toLowerCase().equals("part name")) {
                
                AlertMessage.errorPart(4, addPartsName);
                return;
                
            }
            if (Integer.parseInt(addPartsMin.getText().trim()) > Integer.parseInt(addPartsMax.getText().trim())) {
                
                AlertMessage.errorPart(8, addPartsMin);
                return;
                
            }
            if (Integer.parseInt(countINV.getText().trim()) < Integer.parseInt(addPartsMin.getText().trim())) {
                
                AlertMessage.errorPart(6, countINV);
                return;
                
            }
            if (Integer.parseInt(countINV.getText().trim()) > Integer.parseInt(addPartsMax.getText().trim())) {
                
                AlertMessage.errorPart(7, countINV);
                return;
            }
            if (end) {
                
                return;
                
            } 
            else if (companyNameField.getText().trim().isEmpty() || companyNameField.getText().trim().toLowerCase().equals("company name")) {
                
                AlertMessage.errorPart(3, companyNameField);
                return;

            } else if (inHouseRadio.isSelected() && !companyNameField.getText().trim().matches("[0-9]*")) {
                
                AlertMessage.errorPart(9, companyNameField);
                return;
                
            } 
            else if (inHouseRadio.isSelected()) {
                
                addInHouse();

            } 
            else if (outSourcedRadio.isSelected()) {
                
                addOutSourced();

            }

        } 
        else {
            AlertMessage.errorPart(2, null);
            return;

        }
        mainScreen(event);
    }
    
    
    private void addOutSourced() {
        
                inv.addPart(new OutSourced(Integer.parseInt(addPartsID.getText().trim()), addPartsName.getText().trim(),
                Double.parseDouble(addPartsPrice.getText().trim()), Integer.parseInt(countINV.getText().trim()),
                Integer.parseInt(addPartsMin.getText().trim()), Integer.parseInt(addPartsMax.getText().trim()), companyNameField.getText().trim()));

    }

    private void addInHouse() {
        
        
                inv.addPart(new InHouse(Integer.parseInt(addPartsID.getText().trim()), addPartsName.getText().trim(),
                Double.parseDouble(addPartsPrice.getText().trim()), Integer.parseInt(countINV.getText().trim()),
                Integer.parseInt(addPartsMin.getText().trim()), Integer.parseInt(addPartsMax.getText().trim()), (Integer.parseInt(companyNameField.getText().trim()))));

    }



    private void resetFieldsStyle() {
        addPartsName.setStyle("-fx-border-color: lightgray");
        countINV.setStyle("-fx-border-color: lightgray");
        addPartsPrice.setStyle("-fx-border-color: lightgray");
        addPartsMin.setStyle("-fx-border-color: lightgray");
        addPartsMax.setStyle("-fx-border-color: lightgray");
        companyNameField.setStyle("-fx-border-color: lightgray");
    }


    
    private boolean checkType(TextField field) {

        if (field == addPartsPrice & !field.getText().trim().matches("\\d+(\\.\\d+)?")) {
            AlertMessage.errorPart(3, field);
            return true;
        }
        if (field != addPartsPrice & !field.getText().trim().matches("[0-9]*")) {
            AlertMessage.errorPart(3, field);
            return true;
        }
        return false;
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
            if (field.getText().trim().isEmpty() | field.getText().trim() == null) {
                AlertMessage.errorPart(1, field);
                return true;
            }
            if (field == addPartsPrice && Double.parseDouble(field.getText().trim()) < 0) {
                AlertMessage.errorPart(5, field);
                error = true;
            }
        } catch (Exception e) {
            error = true;
            AlertMessage.errorPart(3, field);
            System.out.println(e);

        }
        return error;
    }


}