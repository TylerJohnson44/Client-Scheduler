/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler.Model;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;


/**
 *
 * @author Tyler
 */
public class Customer {
    
    
    
    private SimpleStringProperty primaryAddress;
    private SimpleStringProperty secondaryAddress;
    private SimpleStringProperty city;
    private SimpleStringProperty zipcode;
    private SimpleStringProperty country;
    private SimpleStringProperty phoneNumber;
    private SimpleStringProperty name;
    
    
    //Used exclusively for updating a customer record
    private SimpleIntegerProperty id;

    public SimpleStringProperty getPrimaryAddress() {
        return primaryAddress;
    }
   public SimpleIntegerProperty getId() {
        return id;
    }
    public void setPrimaryAddress(SimpleStringProperty primaryAddress) {
        this.primaryAddress = primaryAddress;
    }

    public SimpleStringProperty getSecondaryAddress() {
        return secondaryAddress;
    }

    public void setSecondaryAddress(SimpleStringProperty secondaryAddress) {
        this.secondaryAddress = secondaryAddress;
    }

    public SimpleStringProperty getCity() {
        return city;
    }

    public void setCity(SimpleStringProperty city) {
        this.city = city;
    }

    public SimpleStringProperty getZipcode() {
        return zipcode;
    }

    public void setZipcode(SimpleStringProperty zipcode) {
        this.zipcode = zipcode;
    }

    public SimpleStringProperty getCountry() {
        return country;
    }

    public void setCountry(SimpleStringProperty country) {
        this.country = country;
    }

    public SimpleStringProperty getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(SimpleStringProperty phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public SimpleStringProperty getName() {
        return name;
    }

    public void setName(SimpleStringProperty name) {
        this.name = name;
    }

    public void setId(SimpleIntegerProperty id){
        this.id = id;
    }
    
    public Customer(String name, String primaryAddress, String secondaryAddress, String city, String zipcode, String country, String phoneNumber, int id){
        this.primaryAddress = new SimpleStringProperty(primaryAddress);
        this.secondaryAddress = new SimpleStringProperty(secondaryAddress);
        this.city = new SimpleStringProperty(city);
        this.zipcode = new SimpleStringProperty(zipcode);
        this.country = new SimpleStringProperty(country);
        this.phoneNumber = new SimpleStringProperty(phoneNumber);
        this.name = new SimpleStringProperty(name);
        this.id = new SimpleIntegerProperty(id);
        
    }
}
