/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler.Controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import scheduler.Model.Appointment;
import scheduler.Model.Constants;
import scheduler.Model.Customer;
import scheduler.Scheduler;

/**
 *
 * @author Tyler
 */
public class MainMenuController implements Initializable{

    private final DateTimeFormatter timeDTF = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT);
    private final DateTimeFormatter dateDTF = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT);
    private Scheduler mainScheduler = null;
    private Connection sqlConnection = null;
    
    LocalDateTime today = LocalDateTime.now();
    LocalDateTime maxAlert = today.plusMinutes(15);
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }
    
    @FXML
    private void handleButtonAction(ActionEvent event) {
        Button button = (Button) event.getSource();
        String id = button.getId();
        System.out.println("Button pressed: " + id);
        switch(id){
            case "manageCustomers":
                openCustomerWindow();
                break;
            case "manageAppointments":
                openAppointmentWindow();
                break;
            case "appointmentsPerMonth":
                generateByCountPerMonth();
                break;
            case "typesPerMonth":
                generateByTypesPerMonth();
                break;
                
            case "consultants":
                generateByConsultant();
                break;
            
        }
    }
    
    
    private void showErrorAlert(String message){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(message);
        alert.showAndWait();
    }
    
    private void openAppointmentWindow(){
        if(mainScheduler == null)
            showErrorAlert(Constants.Messages.getNoSchedulerError());
        else
            mainScheduler.openAppointmentWindow();
    }
    
    private void openCustomerWindow(){
        if(mainScheduler == null)
            showErrorAlert(Constants.Messages.getNoSchedulerError());
        else
            mainScheduler.openCustomerMenu();
    }
    

    public void setConnection(Connection connection){
        this.sqlConnection = connection;

    }
    public void setController(Scheduler scheduler){
        this.mainScheduler = scheduler;
        getCustomers();
        getAppointments();
    }
    
    public void getCustomers(){
        System.out.println("Getting customer data");
        
        //We have already loaded the data once, after this occurs we will no longer need to reload data
        if(mainScheduler.customers.size() > 0)
            return;
        try{
            Statement st = sqlConnection.createStatement();
            System.out.println("Select statement executing");
            ResultSet set = st.executeQuery("((SELECT customerId, customerName, address, address2, postalCode, phone, country, city FROM customer C JOIN address A ON C.addressId = A.addressId) JOIN city I ON A.cityId = I.cityId) JOIN country P ON P.countryId = I.countryId");
            
            //(String name, String primaryAddress, String secondaryAddress, String city, String zipcode, String country, String phoneNumber)
            while(set.next()){
                int id = set.getInt("customerId");
                String name = set.getString("customerName");
                String address = set.getString("address");
                String address2 = set.getString("address2");
                String zip = set.getString("postalCode");
                String phone = set.getString("phone");
                String country = set.getString("country");
                String city = set.getString("city");
                
                Customer newCustomer = new Customer(name, address, address2, city, zip, country, phone, id);
                System.out.println(String.format("Added customer with the following data: %s, %s, %s, %s, %s, %s, %s, %d", name, address, address2, zip, phone, country, city, id));
                mainScheduler.customers.add(newCustomer);
            }
        }catch(Exception e){
            e.printStackTrace();
        }       
    }
    
    public void getAppointments(){
        System.out.println("Getting appointment data");
        if(mainScheduler.appointments.size() > 0)
            return;
        try{
            Statement st = sqlConnection.createStatement();
            System.out.println("Select statement executing");
            ResultSet set = st.executeQuery("SELECT appointmentId, customerId, title, description, location, url, start, end, contact FROM appointment WHERE createdBy = \"" + mainScheduler.currentUser + "\"");

            while(set.next()){
                int appointmentId = set.getInt("appointmentId");
                int customerId = set.getInt("customerId");
                String title = set.getString("title");
                String description =  set.getString("description");
                String location = set.getString("location");
                String url = set.getString("url");
             
                String contact = set.getString("contact");
                
                //Data in table at column
                //start: '2018-11-30 16:00:00'
                //end: '2018-12-01 00:00:00'
               // Timestamp start = set.getTimestamp("start");
               // Timestamp end = set.getTimestamp("end");
                
                String start = set.getString("start");
                String end = set.getString("end");
                
                
//                //Debug values
//                //start: '2018-11-30 08:00:00'
                LocalDate startLd = LocalDate.parse(start.split(" ")[0]);
                LocalTime startLt = LocalTime.parse(start.split(" ")[1]);
                LocalDateTime startLdt = LocalDateTime.of(startLd, startLt);
                
                LocalDate endLd = LocalDate.parse(end.split(" ")[0]);
                LocalTime endLt = LocalTime.parse(end.split(" ")[1]);
                LocalDateTime endLdt = LocalDateTime.of(endLd, endLt);

                ZonedDateTime newzdtStart = startLdt.atZone(ZoneId.of("UTC"));
        	ZonedDateTime localStart = newzdtStart.withZoneSameInstant(ZoneId.systemDefault());

                
                ZonedDateTime newzdtEnd = endLdt.atZone(ZoneId.of("UTC"));
        	ZonedDateTime localEnd = newzdtEnd.withZoneSameInstant(ZoneId.systemDefault());

                System.out.println("Zoned utc start: " + newzdtStart.toString());
                String formattedStart = localStart.format(dateDTF);
                String formattedEnd =  localEnd.format(dateDTF);
                System.out.println("Formatted start: " + formattedStart);
                System.out.println("Formatted end: " + formattedEnd);
                
                LocalDateTime fStart = localStart.toLocalDateTime();
                
                
                //Checks if appointment is within 15 minutes of login
                if(fStart.isBefore(maxAlert) && fStart.isAfter(today)){
                    showInfoAlert(Constants.Messages.getAppointmentWarning());
                }
                
                
                Customer toAdd = getCustomerAt(customerId);
                if(toAdd == null)
                    throw new NullPointerException();
            
                mainScheduler.appointments.add(new Appointment(title, location, formattedStart, formattedEnd, contact, description, appointmentId, url, toAdd));
            }
        }catch(Exception e){
            e.printStackTrace();
            
        }
    }
    private void showInfoAlert(String message){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(message);
        alert.showAndWait();
    }
    public Customer getCustomerAt(int customerId){
        for(int i = 0; i < mainScheduler.customers.size(); i++){
            if(mainScheduler.customers.get(i).getId().getValue().intValue() == customerId)
                return mainScheduler.customers.get(i);
        }
        
        return null;
    }
    
    
    private void generateByCountPerMonth(){
        try{
            File yourFile = new File("reports-count-all.log");
            yourFile.createNewFile(); // if file already exists will do nothing 
            FileOutputStream oFile = new FileOutputStream(yourFile, true); 
            PrintWriter printer = new PrintWriter(oFile);            
            
            
            LocalTime min = LocalTime.of(0, 0);
            LocalDate today = LocalDate.now();
        
            for(int i = 0; i < 12; i++){
                LocalDate nextMonth = today.with(TemporalAdjusters.firstDayOfNextMonth());
                LocalDate thisMonth = today.with(TemporalAdjusters.firstDayOfMonth());

                LocalDateTime startDT = LocalDateTime.of(thisMonth, min);
                LocalDateTime endDT = LocalDateTime.of(nextMonth, min);

                ZonedDateTime startUTC = startDT.atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneId.of("UTC"));
                ZonedDateTime endUTC = endDT.atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneId.of("UTC"));            

                Timestamp startsqlts = Timestamp.valueOf(startUTC.toLocalDateTime()); 
                Timestamp endsqlts = Timestamp.valueOf(endUTC.toLocalDateTime());   
                
                
                PreparedStatement pst = sqlConnection.prepareStatement("SELECT * FROM appointment WHERE (start >= ? AND end <= ?)");
                pst.setString(1, startsqlts.toString());
                pst.setString(2, endsqlts.toString());
                System.out.println("Start: " + startsqlts.toString() + " End: " + endsqlts.toString());
                //        types.addAll("Consultation", "Follow up", "Counseling");

                ResultSet set = pst.executeQuery();
                
                int total = 0;
                
                while(set.next()){
                    total++;
                }
                    
                 
                printer.println(thisMonth.getMonth().toString() + " - Total Appointments: " + total);

                
                today = nextMonth;
            }
            
            printer.close();
            oFile.close();
            showInfoAlert("Finished generating report check your working directory for reports-count-all.log");
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    private void generateByTypesPerMonth(){
        
        try{
            File yourFile = new File("reports-types-month.log");
            yourFile.createNewFile(); // if file already exists will do nothing 
            FileOutputStream oFile = new FileOutputStream(yourFile, true); 
            PrintWriter printer = new PrintWriter(oFile);            
            
            
            LocalTime min = LocalTime.of(0, 0);
            LocalDate today = LocalDate.now();
        
            for(int i = 0; i < 12; i++){
                LocalDate nextMonth = today.with(TemporalAdjusters.firstDayOfNextMonth());
                LocalDate thisMonth = today.with(TemporalAdjusters.firstDayOfMonth());

                LocalDateTime startDT = LocalDateTime.of(thisMonth, min);
                LocalDateTime endDT = LocalDateTime.of(nextMonth, min);

                ZonedDateTime startUTC = startDT.atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneId.of("UTC"));
                ZonedDateTime endUTC = endDT.atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneId.of("UTC"));            

                Timestamp startsqlts = Timestamp.valueOf(startUTC.toLocalDateTime()); 
                Timestamp endsqlts = Timestamp.valueOf(endUTC.toLocalDateTime());   
                
                
                PreparedStatement pst = sqlConnection.prepareStatement("SELECT * FROM appointment WHERE (start >= ? AND end <= ?)");
                pst.setString(1, startsqlts.toString());
                pst.setString(2, endsqlts.toString());
                System.out.println("Start: " + startsqlts.toString() + " End: " + endsqlts.toString());
                //        types.addAll("Consultation", "Follow up", "Counseling");

                ResultSet set = pst.executeQuery();
                
                int counselCount = 0;
                int consultCount = 0;
                int followCount = 0;
                
                while(set.next()){
                    String type = set.getString("description").toLowerCase();
                    if(type.contains("coun"))
                        counselCount++;
                    else if(type.contains("fol"))
                        followCount++;
                    else
                        consultCount++;
                    System.out.println("Month: " + thisMonth.getMonth().toString() + " " + "Type: " + type);
                }
                    
                 
                printer.println(thisMonth.getMonth().toString() + " - Counseling: " + counselCount + " - Follow Up: " + followCount + " - Consulting: " + consultCount);

                
                today = nextMonth;
            }
            
            printer.close();
            oFile.close();
            showInfoAlert("Finished generating report check your working directory for reports-types-month.log");
        }catch(Exception e){
            e.printStackTrace();
        }
        
        

        
        
    }
    
    private void generateByConsultant(){
        try{
            ArrayList<String> names = new ArrayList();
            
            PreparedStatement pst = sqlConnection.prepareStatement("SELECT * FROM user");
            ResultSet res = pst.executeQuery();
            
            while(res.next()){
                names.add(res.getString("userName"));
            }
                  
            File yourFile = new File("reports-by-user.log");
            yourFile.createNewFile(); // if file already exists will do nothing 
            FileOutputStream oFile = new FileOutputStream(yourFile, true); 
            PrintWriter printer = new PrintWriter(oFile);         
            
            LocalTime min = LocalTime.of(0, 0);
            LocalDate today = LocalDate.now();
            
            LocalDateTime ldt = LocalDateTime.of(today, min);
            LocalDateTime ldtEnd = ldt.plusWeeks(2);
            ZonedDateTime startUTC = ldt.atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneId.of("UTC"));
            ZonedDateTime endUTC = ldtEnd.atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneId.of("UTC"));            

            Timestamp startsqlts = Timestamp.valueOf(startUTC.toLocalDateTime()); 
            Timestamp endsqlts = Timestamp.valueOf(endUTC.toLocalDateTime());   
            
            System.out.println("Start: " + startsqlts.toString() + " End: " + endsqlts.toString() );
            
            for(int i = 0; i < names.size(); i++){
                String currentName = names.get(i);
                
                pst = sqlConnection.prepareStatement("SELECT * FROM appointment WHERE (createdBy = ? AND ? <= start AND ? >= end)");
                pst.setString(1, currentName);
                pst.setString(2, startsqlts.toString());
                pst.setString(3, endsqlts.toString());
                
                res = pst.executeQuery();
                
                printer.println("User: " + currentName);
                while(res.next()){
                    String start = res.getString("start");
                    String end = res.getString("end");
                    LocalDate startLd = LocalDate.parse(start.split(" ")[0]);
                    LocalTime startLt = LocalTime.parse(start.split(" ")[1]);
                    LocalDateTime startLdt = LocalDateTime.of(startLd, startLt);

                    LocalDate endLd = LocalDate.parse(end.split(" ")[0]);
                    LocalTime endLt = LocalTime.parse(end.split(" ")[1]);
                    LocalDateTime endLdt = LocalDateTime.of(endLd, endLt);

                    ZonedDateTime newzdtStart = startLdt.atZone(ZoneId.of("UTC"));
                    ZonedDateTime localStart = newzdtStart.withZoneSameInstant(ZoneId.systemDefault());


                    ZonedDateTime newzdtEnd = endLdt.atZone(ZoneId.of("UTC"));
                    ZonedDateTime localEnd = newzdtEnd.withZoneSameInstant(ZoneId.systemDefault());
                    printer.println(localStart.format(dateDTF) + " - " +  localEnd.format(dateDTF));
                }
                printer.println("-----------------------------------------------------------------------------------------------------------------------");
            }
            
            printer.close();
            oFile.close();
            
            showInfoAlert("Finished generating report check your working directory for reports-by-user.log");
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
