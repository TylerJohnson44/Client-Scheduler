/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler.Controller;

import com.sun.corba.se.impl.orbutil.closure.Constant;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import scheduler.Model.Constants;
import scheduler.Model.Customer;
import scheduler.Scheduler;

/**
 *
 * @author Tyler
 */
public class CustomerScreenController implements Initializable{
    
    private Scheduler mainScheduler = null;
    private Connection sqlConnection = null;
    
    @FXML TextField searchField;
    
    @FXML TableView<Customer> customerTable;
    @FXML TableColumn<Customer, String> nameColumn;
    @FXML TableColumn<Customer, String> cityColumn;
    @FXML TableColumn<Customer, String> countryColumn;
    @FXML TableColumn<Customer, String> phoneNumberColumn;
    @FXML TableColumn<Customer, String> primaryAddressColumn;
    @FXML TableColumn<Customer, String> secondaryAddressColumn;
    @FXML TableColumn<Customer, String> zipcodeColumn;
    @FXML TableColumn<Customer, String> idColumn;
    
    
    private ObservableList<Customer> searchResults = FXCollections.observableArrayList();

    
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("Initializing screen controller");
        //The below lambda expressions allow for on the fly creation of anonmyous classes in which we leave out alot of the ugly syntax and make the code more readable overall
       nameColumn.setCellValueFactory(cellData -> cellData.getValue().getName());
       cityColumn.setCellValueFactory(cellData -> cellData.getValue().getCity());
       countryColumn.setCellValueFactory(cellData -> cellData.getValue().getCountry());
       phoneNumberColumn.setCellValueFactory(cellData -> cellData.getValue().getPhoneNumber());
       primaryAddressColumn.setCellValueFactory(cellData -> cellData.getValue().getPrimaryAddress());
       secondaryAddressColumn.setCellValueFactory(cellData -> cellData.getValue().getSecondaryAddress());
       idColumn.setCellValueFactory(cellData -> cellData.getValue().getId().asString());
       zipcodeColumn.setCellValueFactory(cellData -> cellData.getValue().getZipcode());
    }
    public void setController(Scheduler scheduler){
        this.mainScheduler = scheduler;
        customerTable.setItems(mainScheduler.customers);
    }
    public void setConnection(Connection connection){
        this.sqlConnection = connection;
        
    }
    @FXML
    private void handleButtonAction(ActionEvent event) {
        Button button = (Button) event.getSource();
        String id = button.getId();
        System.out.println("Button pressed: " + id);
        switch(id){
            case "addButton":
                mainScheduler.openAddCustomerMenu();
                break;
            case "modifyButton":
                Customer customer= customerTable.getSelectionModel().getSelectedItem();
                if(customer != null)
                    mainScheduler.openModifyCustomerMenu(customer);
              
                break;
            case "deleteButton":
                removeCustomer(customerTable.getSelectionModel().getSelectedItem());
                break;
            case "searchButton":
                doSearch();
                break;
            case "backButton":
                mainScheduler.openMainWindow();
                break;
        }
    }
    
    private void doSearch(){
        String search = searchField.getText().toLowerCase();
        searchResults.clear();
        if(search == ""){
            customerTable.setItems(mainScheduler.customers);
        }else{
            for(int i = 0; i < mainScheduler.customers.size(); i++){
                if(mainScheduler.customers.get(i).getName().get().toLowerCase().contains(search)){
                    searchResults.add(mainScheduler.customers.get(i));
                }
            } 
            
            customerTable.setItems(searchResults);
        }
    }
   
    private void removeCustomer(Customer c){
        System.out.println("Removing customer");
        try{
            Statement st = sqlConnection.createStatement();
            st.executeUpdate("DELETE FROM customer WHERE customerId=" + c.getId().getValue().intValue());
            mainScheduler.customers.remove(c);
            
        }catch(Exception e){
            e.printStackTrace();
            showErrorAlert(Constants.Messages.getFailedCustomerDelete());
        }
    }
    
    private void showErrorAlert(String message){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(message);
        alert.showAndWait();
    }
    



    
}
