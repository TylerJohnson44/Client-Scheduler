/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler.Controller;

import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import scheduler.Model.Constants;
import scheduler.Model.Customer;
import scheduler.Scheduler;

/**
 *
 * @author Tyler
 */
public class CustomerController implements Initializable{

    @FXML TextField nameField;
    @FXML TextField addrField;
    @FXML TextField addr2Field;
    @FXML TextField cityField;
    @FXML TextField countryField;
    @FXML TextField zipField;
    @FXML TextField phoneNumberField;
   
    @FXML Label addCustomerLabel; 
    
    private Customer customer;
    private boolean update;
    private Connection sqlConnection;
    private Scheduler mainScheduler;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        customer= null;
        sqlConnection = null;
        update = false;
        mainScheduler = null;
    
    }
    
    @FXML
    private void handleButtonAction(ActionEvent event) {
        Button button = (Button) event.getSource();
        String id = button.getId();
        System.out.println("Button pressed: " + id);
        switch(id){
            case "acceptButton":
                if(attemptSave()){
                    ((Stage) button.getScene().getWindow()).close();
                }
                break;

                
            case "cancelButton":
                ((Stage) button.getScene().getWindow()).close();
                break;
        }
    }
    
    
    public void setCustomer(Customer c){
        addCustomerLabel.setText("Modify Customer");
        nameField.setText(c.getName().get());
        addrField.setText(c.getPrimaryAddress().get());
        addr2Field.setText(c.getSecondaryAddress().get());
        cityField.setText(c.getCity().get());
        countryField.setText(c.getCountry().get());
        zipField.setText(c.getZipcode().get());
        phoneNumberField.setText(c.getPhoneNumber().get());
        this.update = true;
        this.customer = customer;
    }

    public void setConnection(Connection connection){
        this.sqlConnection = connection;
    }
    
    public void setController(Scheduler mainScheduler){
        this.mainScheduler = mainScheduler;
    }
    
    private boolean attemptSave(){
        try {
            if (!validateForm()) {
                return false;
            }
            if (sqlConnection != null) {
                int countryId = -1;
                int cityId = -1;
                int addressId = -1;
                int customerId = -1;

                Statement st = sqlConnection.createStatement();
                System.out.println("Attempting to add data to table");

                ResultSet set = st.executeQuery("SELECT countryId, country FROM country WHERE country=\"" + countryField.getText() + "\"");
                if (set.next()) {
                    countryId = set.getInt("countryId");
                    System.out.println(String.format("Found country in table: %s", countryField.getText()));
                } else {
                    System.out.println(String.format("Did not find %s in country table, attempting to add", countryField.getText()));
                    st.executeUpdate("INSERT INTO country (country, createdBy, createDate, lastUpdate, lastUpdateBy) VALUES (\""
                            + countryField.getText() + "\", \"" + mainScheduler.currentUser + "\", utc_timestamp(), utc_timestamp(), \"" + mainScheduler.currentUser + "\")");
                    set = st.executeQuery("SELECT countryId FROM country WHERE country=\"" + countryField.getText() + "\"");
                    if (set.next()) {
                        countryId = set.getInt("countryId");
                    } else {
                        System.out.println("Failed to add country");
                    }
                }

                set = st.executeQuery("SELECT cityId, city FROM city WHERE city=\"" + cityField.getText() + "\"");
                if (set.next()) {
                    cityId = set.getInt("cityId");
                    System.out.println(String.format("Found city in table:  %s", cityField.getText()));
                } else {
                    System.out.println(String.format("Did not find %s in city table", cityField.getText()));
                    st.executeUpdate("INSERT INTO city (city, countryId, createDate, createdBy, lastUpdate, lastUpdateBy) VALUES (\""
                            + cityField.getText() + "\", " + countryId + ",  utc_timestamp(), \"" + mainScheduler.currentUser + "\",  utc_timestamp(),  \"" + mainScheduler.currentUser + "\")");
                    set = st.executeQuery("SELECT cityId FROM city WHERE city=\"" + cityField.getText() + "\"");
                    if (set.next()) {
                        cityId = set.getInt("cityId");
                    } else {
                        System.out.println("Failed to add city");
                    }
                }

                set = st.executeQuery("SELECT addressId, address, address2 FROM address WHERE address=\"" + addrField.getText() + "\" AND address2=\"" + addr2Field.getText() + "\"");
                if (set.next()) {
                    addressId = set.getInt("addressId");
                    System.out.println(String.format("Found address in table: %s", addrField.getText()));
                } else {
                    System.out.println(String.format("Did not find %s in address table", addrField.getText()));
                    st.executeUpdate("INSERT INTO address (address, address2, cityId, postalCode, phone, createdBy, createDate, lastUpdate, lastUpdateBy)  VALUES(\""
                            + addrField.getText() + "\", \"" + addr2Field.getText() + "\", " + cityId + ", \"" + zipField.getText()
                            + "\", \"" + phoneNumberField.getText() + "\", \"" + mainScheduler.currentUser + "\", utc_timestamp(), utc_timestamp(), \"" + mainScheduler.currentUser + "\")");
                    set = st.executeQuery("SELECT addressId FROM address WHERE address=\"" + addrField.getText() + "\" AND address2=\"" + addr2Field.getText() + "\"");

                    if (set.next()) {
                        addressId = set.getInt("addressId");
                    } else {
                        System.out.println("Failed to add address");
                    }
                }

                /*customerId int(11) AI PK 
customerName varchar(255) 
addressId int(11) 
active tinyint(1) 
createDate datetime 
createdBy varchar(40) 
lastUpdate timestamp 
lastUpdateBy varchar(40*/
                //Update case
                if (update) {
                    st.executeUpdate("UPDATE customer SET customerName=\"" + nameField.getText() + "\", addressId=" + addressId + ", lastUpdate=utc_timestamp() "
                            + "WHERE customerId=" + customer.getId().getValue().intValue() + "");

                    for (int i = 0; i < mainScheduler.customers.size(); i++) {
                        if (mainScheduler.customers.get(i).getId().getValue().intValue() == customer.getId().getValue().intValue()) {
                            mainScheduler.customers.get(i).getName().set(nameField.getText());
                            mainScheduler.customers.get(i).getCity().set(cityField.getText());
                            mainScheduler.customers.get(i).getCountry().set(countryField.getText());
                            mainScheduler.customers.get(i).getPhoneNumber().set(phoneNumberField.getText());
                            mainScheduler.customers.get(i).getPrimaryAddress().set(addrField.getText());
                            mainScheduler.customers.get(i).getSecondaryAddress().set(addr2Field.getText());
                            mainScheduler.customers.get(i).getZipcode().set(zipField.getText());
                        }

                    }

                } //Add new case
                else {
                    st.executeUpdate("INSERT INTO customer (customerName, addressId, active, createDate, createdBy, lastUpdate, lastUpdateBy) VALUES(\""
                            + nameField.getText() + "\", " + addressId + ", 1, utc_timestamp(), \"" + mainScheduler.currentUser + "\", utc_timestamp(), \"" + mainScheduler.currentUser + "\")");

                    set = st.executeQuery("SELECT customerId FROM customer WHERE customerName=\"" + nameField.getText() + "\" AND addressId=" + addressId);

                    if (set.next()) {
                        customerId = set.getInt("customerId");
                    }
                    mainScheduler.customers.add(new Customer(nameField.getText(), addrField.getText(), addr2Field.getText(), cityField.getText(), zipField.getText(), countryField.getText(), phoneNumberField.getText(), customerId));

                    //public Customer(String name, String primaryAddress, String secondaryAddress, String city, String zipcode, String country, String phoneNumber, int id){
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            showErrorAlert(Constants.Messages.getFailedCustomerAdd());
            return false;
        }
        return true;
    }
    
    private boolean validateForm(){
        try{
            if(nameField.getText().isEmpty() ||
                    countryField.getText().isEmpty() || 
                    addr2Field.getText().isEmpty() ||
                    addrField.getText().isEmpty() ||
                    cityField.getText().isEmpty() ||
                    zipField.getText().isEmpty() ||
                    phoneNumberField.getText().isEmpty()){
                showErrorAlert(Constants.Messages.getInvalidInputError());
                return false;
            }
        }catch(Exception e){
            e.printStackTrace();
            showErrorAlert(Constants.Messages.getInvalidInputError());

            return false;
        }
        
        return true;
    }
    private void showErrorAlert(String message){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(message);
        alert.showAndWait();
    }

}
