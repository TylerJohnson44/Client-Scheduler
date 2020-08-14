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
public class Appointment {
    
    private SimpleStringProperty title;
    private SimpleStringProperty location;
    private SimpleStringProperty date;
    private SimpleStringProperty startTime;
    private SimpleStringProperty endTime;
    private SimpleStringProperty contact;
    private SimpleStringProperty type;
    private SimpleStringProperty url;

    private SimpleIntegerProperty id;
    private Customer customer;
    
    public SimpleStringProperty getType(){
        return type;
    }
    public void setType(SimpleStringProperty type){
        this.type = type;
    }

    public SimpleStringProperty getTitle() {
        return title;
    }

    public void setTitle(SimpleStringProperty title) {
        this.title = title;
    }

    public SimpleStringProperty getLocation() {
        return location;
    }
    
    public void setLocation(SimpleStringProperty location) {
        this.location = location;
    }

    public SimpleStringProperty getDate() {
        return date;
    }

    public void setDate(SimpleStringProperty date) {
        this.date = date;
    }

    public SimpleStringProperty getStartTime() {
        return startTime;
    }

    public void setStartTime(SimpleStringProperty startTime) {
        this.startTime = startTime;
    }

    public SimpleStringProperty getEndTime() {
        return endTime;
    }

    public void setEndTime(SimpleStringProperty endTime) {
        this.endTime = endTime;
    }

    public SimpleStringProperty getContact() {
        return contact;
    }

    public void setContact(SimpleStringProperty contact) {
        this.contact = contact;
    }
    
    public SimpleIntegerProperty getId(){
        return id;
    }
    
    public Customer getCustomerId(){
        return customer;
    }
    
    public void setCustomer(Customer customer){
        this.customer = customer;
    }
    
    public SimpleStringProperty getUrl(){
        return url;
    }
    public void setUrl(SimpleStringProperty url){
        this.url = url;
    }
    public Appointment(String title, String location,String startTime, String endTime, String contact, String type, int id, String url, Customer customer){
        this.title = new SimpleStringProperty(title);
        this.location = new SimpleStringProperty(location);
        this.startTime = new SimpleStringProperty(startTime);
        this.endTime = new SimpleStringProperty(endTime);
        this.contact = new SimpleStringProperty(contact);
        this.type = new SimpleStringProperty(type);
        this.id = new SimpleIntegerProperty(id);
        this.url = new SimpleStringProperty(url);
        this.customer = customer;
        
        
    }
    
    
    
    
}
