/**
 * FXML Controller class
 *
 * @author Stephen Moyer
 */
package Controllers;

import Controllers.MainScreenController;
import Controllers.AlertMessage;
import Model.InHouse;
import Model.Inventory;
import Model.OutSourced;
import Model.Part;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;


public class ModifyPartController implements Initializable {

    Inventory inv;
    Part part;

    @FXML
    private RadioButton inHouseRadio;
    @FXML
    private RadioButton outSourcedRadio;
    @FXML
    private Label companyLabel;
    @FXML
    private TextField modifyPartID;
    @FXML
    private TextField modifyPartName;
    @FXML
    private TextField modifyPartPrice;
    @FXML
    private TextField modifyPartsINVcount;
    @FXML
    private TextField modifyPartsMin;
    @FXML
    private TextField modifyPartsMax;
    @FXML
    private TextField modifyPartsCompanyLabel;
    @FXML
    private Button modifyPartSaveButton;

    public ModifyPartController(Inventory inv, Part part) {
        this.inv = inv;
        this.part = part;
    }

//    public void setSelectPart(Part sel){
//        this.part = sel;
//    }
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setData();
    }

    private void setData() {

        if (part instanceof InHouse) {

            InHouse part1 = (InHouse) part;
            inHouseRadio.setSelected(true);
            companyLabel.setText("Machine ID");
            this.modifyPartName.setText(part1.getName());
            this.modifyPartID.setText((Integer.toString(part1.getPartID())));
            this.modifyPartsINVcount.setText((Integer.toString(part1.getInStock())));
            this.modifyPartPrice.setText((Double.toString(part1.getPrice())));
            this.modifyPartsMin.setText((Integer.toString(part1.getMin())));
            this.modifyPartsMax.setText((Integer.toString(part1.getMax())));
            this.modifyPartsCompanyLabel.setText((Integer.toString(part1.getMachineID())));

        }

        if (part instanceof OutSourced) {

            OutSourced part2 = (OutSourced) part;
            outSourcedRadio.setSelected(true);
            companyLabel.setText("Company Name");
            this.modifyPartName.setText(part2.getName());
            this.modifyPartID.setText((Integer.toString(part2.getPartID())));
            this.modifyPartsINVcount.setText((Integer.toString(part2.getInStock())));
            this.modifyPartPrice.setText((Double.toString(part2.getPrice())));
            this.modifyPartsMin.setText((Integer.toString(part2.getMin())));
            this.modifyPartsMax.setText((Integer.toString(part2.getMax())));
            this.modifyPartsCompanyLabel.setText(part2.getCompanyName());
        }
    }

    @FXML
    private void clearTextField(MouseEvent event
    ) {
        Object source = event.getSource();
        TextField field = (TextField) source;
        field.setText("");
    }

    @FXML
    private void selectInHouse(MouseEvent event
    ) {
        companyLabel.setText("Machine ID");

    }

    @FXML
    private void selectOutSourced(MouseEvent event
    ) {
        companyLabel.setText("Company Name");

    }

    @FXML
    private void idDisabled(MouseEvent event
    ) {
    }

    @FXML
    private void cancelModifyPart(MouseEvent event
    ) {
        boolean cancel = cancel();
        if (cancel) {
            mainScreen(event);
        } else {
            return;
        }
    }

    @FXML
    private void saveModifyPart(MouseEvent event
    ) {
        resetFieldsStyle();
        boolean end = false;
        TextField[] fieldCount = {modifyPartsINVcount, modifyPartPrice, modifyPartsMin, modifyPartsMax};
        if (inHouseRadio.isSelected() || outSourcedRadio.isSelected()) {
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
            if (modifyPartName.getText().trim().isEmpty() || modifyPartName.getText().trim().toLowerCase().equals("part name")) {
                AlertMessage.errorPart(4, modifyPartName);
                return;
            }
            if (Integer.parseInt(modifyPartsMin.getText().trim()) > Integer.parseInt(modifyPartsMax.getText().trim())) {
                AlertMessage.errorPart(8, modifyPartsMin);
                return;
            }
            if (Integer.parseInt(modifyPartsINVcount.getText().trim()) < Integer.parseInt(modifyPartsMin.getText().trim())) {
                AlertMessage.errorPart(6, modifyPartsINVcount);
                return;
            }
            if (Integer.parseInt(modifyPartsINVcount.getText().trim()) > Integer.parseInt(modifyPartsMax.getText().trim())) {
                AlertMessage.errorPart(7, modifyPartsINVcount);
                return;
            }

            if (end) {
                return;
            } else if (outSourcedRadio.isSelected() && modifyPartsCompanyLabel.getText().trim().isEmpty()) {
                AlertMessage.errorPart(1, modifyPartsCompanyLabel);
                return;
            } else if (inHouseRadio.isSelected() && !modifyPartsCompanyLabel.getText().matches("[0-9]*")) {
                AlertMessage.errorPart(9, modifyPartsCompanyLabel);
                return;

            } else if (inHouseRadio.isSelected() & part instanceof InHouse) {
                updateItemInHouse();

            } else if (inHouseRadio.isSelected() & part instanceof OutSourced) {
                updateItemInHouse();
            } else if (outSourcedRadio.isSelected() & part instanceof OutSourced) {
                updateItemOutSourced();
            } else if (outSourcedRadio.isSelected() & part instanceof InHouse) {
                updateItemOutSourced();
            }

        } else {
            AlertMessage.errorPart(2, null);
            return;

        }
        mainScreen(event);
    }

    private void updateItemInHouse() {
        inv.updatePart(new InHouse(Integer.parseInt(modifyPartID.getText().trim()), modifyPartName.getText().trim(),
                Double.parseDouble(modifyPartPrice.getText().trim()), Integer.parseInt(modifyPartsINVcount.getText().trim()),
                Integer.parseInt(modifyPartsMin.getText().trim()), Integer.parseInt(modifyPartsMax.getText().trim()), Integer.parseInt(modifyPartsCompanyLabel.getText().trim())));
    }

    private void updateItemOutSourced() {
        inv.updatePart(new OutSourced(Integer.parseInt(modifyPartID.getText().trim()), modifyPartName.getText().trim(),
                Double.parseDouble(modifyPartPrice.getText().trim()), Integer.parseInt(modifyPartsINVcount.getText().trim()),
                Integer.parseInt(modifyPartsMin.getText().trim()), Integer.parseInt(modifyPartsMax.getText().trim()), modifyPartsCompanyLabel.getText().trim()));
    }

    private void resetFieldsStyle() {
        modifyPartName.setStyle("-fx-border-color: lightgray");
        modifyPartsINVcount.setStyle("-fx-border-color: lightgray");
        modifyPartPrice.setStyle("-fx-border-color: lightgray");
        modifyPartsMin.setStyle("-fx-border-color: lightgray");
        modifyPartsMax.setStyle("-fx-border-color: lightgray");
        modifyPartsCompanyLabel.setStyle("-fx-border-color: lightgray");

    }

    private void mainScreen(MouseEvent event) {
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
                AlertMessage.errorPart(1, field);
                return true;
            }
            if (field == modifyPartPrice && Double.parseDouble(field.getText().trim()) <= 0.0) {
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

    private boolean checkType(TextField field) {

        if (field == modifyPartPrice & !field.getText().trim().matches("\\d+(\\.\\d+)?")) {
            AlertMessage.errorPart(3, field);
            return true;
        }
        if (field != modifyPartPrice & !field.getText().trim().matches("[0-9]*")) {
            AlertMessage.errorPart(3, field);
            return true;
        }
        return false;

    }

    private boolean cancel() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Cancel");
        alert.setHeaderText("Sure you want to cancel?");
        alert.setContentText("Click ok to confirm");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            return true;
        } else {
            return false;
        }
    }

}