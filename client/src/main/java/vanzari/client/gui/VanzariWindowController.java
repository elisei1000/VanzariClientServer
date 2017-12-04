package vanzari.client.gui;

import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import vanzari.client.ClientCtrl;
import vanzari.domain.Produs;
import vanzari.domain.Vanzare;
import vanzari.services.rmi.VanzariServiceException;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;

/**
 * Created by elisei on 01.12.2017.
 */
public class VanzariWindowController {
    private ClientCtrl controller;
    private BorderPane view;


    @FXML
    TextField fxCantitateTextField;
    @FXML
    DatePicker fxDataDatePicker;


    @FXML
    public TableView<Produs> fxProduseTable;
    @FXML
    public TableView<Vanzare> fxVanzariTable;

    @FXML
    public TableColumn<Produs, Integer> fxProduseTableIdColumn;
    @FXML
    public TableColumn<Produs, String >fxProduseTableNumeColumn, fxProduseTableUMColumn;
    @FXML
    public TableColumn<Produs, Float> fxProduseTablePretUnitarColumn;


    @FXML
    public TableColumn<Vanzare, Integer> fxVanzariTableIdColumn;
    @FXML
    public TableColumn<Vanzare, Produs> fxVanzariTableProdusColumn;
    @FXML
    public TableColumn<Vanzare, Float> fxVanzariTableCantitateColumn;
    @FXML
    public TableColumn<Vanzare, Date>  fxVanzariTableDataColumn;


    @FXML
    public void initialize(){
        fxProduseTableIdColumn.setCellValueFactory(new PropertyValueFactory<Produs, Integer>("id"));
        fxProduseTableNumeColumn.setCellValueFactory(new PropertyValueFactory<Produs, String>("nume"));
        fxProduseTablePretUnitarColumn.setCellValueFactory(new PropertyValueFactory<Produs,
                Float>("pretUnitar"));
        fxProduseTableUMColumn.setCellValueFactory(new PropertyValueFactory<Produs, String>("unitateDeMasura"));


        fxVanzariTableIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
//        fxVanzariTableProdusColumn.setCellValueFactory(
//                vanzareIntegerCellDataFeatures ->
//                        Bindings.createIntegerBinding(() ->
//                                vanzareIntegerCellDataFeatures.getValue().getProdus().getId()
//                        )
//
//        );
        fxVanzariTableProdusColumn.setCellValueFactory(new PropertyValueFactory<>("produs"));
        fxVanzariTableProdusColumn.setCellFactory(vanzareIntegerTableColumn ->
                new TableCell<Vanzare, Produs>(){
                    @Override
                    protected void updateItem(Produs produs, boolean empty){
                        super.updateItem(produs, empty);
                        if(produs == null || empty){
                            setText(null);
                            setStyle("");
                        }
                        else{
                            setText(String.format("Produs #%d", produs.getId()));
                            setStyle("");
                        }
                    }

                });

        fxVanzariTableCantitateColumn.setCellValueFactory(new PropertyValueFactory<>("cantitate"));
        fxVanzariTableDataColumn.setCellValueFactory(new PropertyValueFactory<>("date"));

    }

    public void setController(ClientCtrl controller) {
        this.controller = controller;


        try {
            fxProduseTable.setItems(controller.getAllProduses());
        } catch (VanzariServiceException e) {
            showErrorMessage("Error on fetching zbors from server!");
            e.printStackTrace();
        }

        try{
            fxVanzariTable.setItems(controller.getAllVanzari());
        } catch (VanzariServiceException e) {
            showErrorMessage("Error on fetching zbors from server!");
            e.printStackTrace();
        }
    }

    public void setView(BorderPane view) {
        this.view = view;
    }


    private void showErrorMessage(String string) {
        Alert message = new Alert(Alert.AlertType.ERROR);
        message.setTitle("Eroare");
        message.getDialogPane().setContent(new Label(string));
        message.showAndWait();
        System.out.print(string);
    }
    private Optional<ButtonType> showConfirmationMessage(String message){
        Alert alert= new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setContentText(message);
        return alert.showAndWait();
    }

    @FXML
    private void handleAddButtonEvent(ActionEvent e){
        String cantitateString = fxCantitateTextField.getText();
        Float cantitate = 0f;

        if(fxProduseTable.getSelectionModel().getSelectedItem() == null){
            showErrorMessage("You must select a produs first!");
            return;
        }

        if(cantitateString == null || cantitateString.isEmpty()){
            showErrorMessage("Invalid cantitate!");
            return;
        }
        try{
            cantitate = Float.parseFloat(cantitateString);
        }
        catch (NumberFormatException ex){
            showErrorMessage("Invalid cantitate!");
            return;
        }
        LocalDate localDate = fxDataDatePicker.getValue();
        Date date;
        if(localDate == null)
            date = new Date();
        else
            date = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

        try{
            controller.addVanzare(new Vanzare(0, date, fxProduseTable.getSelectionModel().getSelectedItem(), cantitate));

        } catch (VanzariServiceException e1) {
            showErrorMessage(e1.getMessage());
        }

    }
}

