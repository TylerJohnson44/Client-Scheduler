/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler.Controller;

import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import scheduler.Model.Appointment;
import scheduler.Model.Constants;
import scheduler.Model.Customer;
import scheduler.Scheduler;

/**
 *
 * @author Tyler
 */
public class AppointmentScreenController implements Initializable{

    private Scheduler mainScheduler = null;
    private Connection sqlConnection = null;
    
    @FXML ComboBox<String> filterCombo;
    @FXML DatePicker filterDatePicker;
    
    @FXML TableView<Appointment> appointmentTable;
    @FXML TableColumn<Appointment, String> titleColumn;
    @FXML TableColumn<Appointment, String> locationColumn;
   // @FXML TableColumn<Appointment, String> dateColumn;
    @FXML TableColumn<Appointment, String> contactColumn;
    @FXML TableColumn<Appointment, String> startTimeColumn;
    @FXML TableColumn<Appointment, String> endTimeColumn;
    @FXML TableColumn<Appointment, String> typeColumn;
    
    @FXML TextField searchField;
    
    ObservableList<String> filters = FXCollections.observableArrayList();
    
    private ObservableList<Appointment> searchResults = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        titleColumn.setCellValueFactory(cellData -> cellData.getValue().getTitle());
        locationColumn.setCellValueFactory(cellData -> cellData.getValue().getLocation());
        contactColumn.setCellValueFactory(cellData -> cellData.getValue().getContact());
        startTimeColumn.setCellValueFactory(cellData -> cellData.getValue().getStartTime());
        endTimeColumn.setCellValueFactory(cellData -> cellData.getValue().getEndTime());
        typeColumn.setCellValueFactory(cellData -> cellData.getValue().getType());
        
        filters.addAll("None", "Week", "Month");
        filterCombo.setItems(filters);
    }
    public void setController(Scheduler scheduler){
        this.mainScheduler = scheduler;
        appointmentTable.setItems(mainScheduler.appointments);
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
                mainScheduler.openAddAppointmentWindow();
                break;
            case "modifyButton":
                Appointment customer= appointmentTable.getSelectionModel().getSelectedItem();
                if(customer != null)
                    mainScheduler.openModifyAppointmentWindow(customer);
              
                break;
            case "deleteButton":
                removeAppointment(appointmentTable.getSelectionModel().getSelectedItem());
                break;
            case "searchButton":
                doSearch();
                break;
            case "backButton":
                mainScheduler.openMainWindow();
                break;
                
            case "filterButton":
                attemptFilter();
                break;
            case "clearButton":
                clearFilters();
                break;
                
        }
    }
    private void attemptFilter(){
        if(filterDatePicker != null &&
                filterDatePicker.getValue() != null &&
                filterCombo != null &&
                filterCombo.getValue() != null){
            
            if(filterCombo.getValue().equals("Week")){
                filterByWeek();
            }else if(filterCombo.getValue().equals("Month")){
                filterByMonth();
            }
        }
        else{
            showErrorAlert(Constants.Messages.getNeedsDateChosenError());
        }
    }
    private void clearFilters(){
        if(appointmentTable != null){
                appointmentTable.setItems(mainScheduler.appointments);
        }
    }
    private void filterByMonth(){
        if(searchResults != null &&
                searchResults.size() > 0){
            searchResults.clear();
        }
        try{
            LocalDate localDate = filterDatePicker.getValue();
            LocalTime startTime = LocalTime.of(0, 0);

            LocalDate nextMonth = localDate.with(TemporalAdjusters.firstDayOfNextMonth());
            
            LocalDateTime startDT = LocalDateTime.of(localDate, startTime);
            LocalDateTime endDT = LocalDateTime.of(nextMonth, startTime);
            
            
            ZonedDateTime startUTC = startDT.atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneId.of("UTC"));
            ZonedDateTime endUTC = endDT.atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneId.of("UTC"));

            Timestamp startsqlts = Timestamp.valueOf(startUTC.toLocalDateTime()); 
            Timestamp endsqlts = Timestamp.valueOf(endUTC.toLocalDateTime());
            
            
            PreparedStatement pst = sqlConnection.prepareStatement("SELECT * FROM appointment WHERE start BETWEEN ? AND ?");
            pst.setString(1, startsqlts.toString());
            pst.setString(2, endsqlts.toString());
            
            ResultSet rs = pst.executeQuery();
            
            while(rs.next()){
                int id = rs.getInt("appointmentId");
                Appointment temp = getAppointment(id);
                
                if(temp != null){
                    searchResults.add(temp);
                }
                
            }
            appointmentTable.setItems(searchResults);
        }catch (Exception e){
            e.printStackTrace();
            showErrorAlert(Constants.Messages.failedFilterAppointment());
        }
    }
    private void filterByWeek(){
        if(searchResults != null &&
                searchResults.size() > 0){
            searchResults.clear();
        }
        try{
            LocalDate localDate = filterDatePicker.getValue();
            LocalTime startTime = LocalTime.of(0, 0);

            LocalDateTime startDT = LocalDateTime.of(localDate, startTime);
            LocalDateTime endDT = LocalDateTime.of(localDate.plusDays(8), startTime);
            
            
            ZonedDateTime startUTC = startDT.atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneId.of("UTC"));
            ZonedDateTime endUTC = endDT.atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneId.of("UTC"));

            Timestamp startsqlts = Timestamp.valueOf(startUTC.toLocalDateTime()); 
            Timestamp endsqlts = Timestamp.valueOf(endUTC.toLocalDateTime());
            
            System.out.println("Start time: " + startsqlts.toString() + " End time: " + endsqlts.toString());
            
            PreparedStatement pst = sqlConnection.prepareStatement("SELECT * FROM appointment WHERE start BETWEEN ? AND ?");
            pst.setString(1, startsqlts.toString());
            pst.setString(2, endsqlts.toString());
            
            ResultSet rs = pst.executeQuery();
            
            while(rs.next()){
                int id = rs.getInt("appointmentId");
                Appointment temp = getAppointment(id);
                
                if(temp != null){
                    searchResults.add(temp);
                }
                
            }
            appointmentTable.setItems(searchResults);
            
        }catch (Exception e){
            e.printStackTrace();
            showErrorAlert(Constants.Messages.failedFilterAppointment());
        }
    }
    
    private void showErrorAlert(String message){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(message);
        alert.showAndWait();
    }
  
    
    
    private Appointment getAppointment(int id){
        if(mainScheduler.appointments != null){
            for(int i = 0; i < mainScheduler.appointments.size(); i++){
                if(mainScheduler.appointments.get(i).getId().intValue() == id){
                    return mainScheduler.appointments.get(i);
                }
            }
        }
        return null;
    }
    
     private void removeAppointment(Appointment c){
        System.out.println("Removing appointment");
        try{
            Statement st = sqlConnection.createStatement();
            st.executeUpdate("DELETE FROM appointment WHERE appointmentId=" + c.getId().getValue().intValue());
            mainScheduler.appointments.remove(c);
            
        }catch(Exception e){
            e.printStackTrace();
            showErrorAlert(Constants.Messages.failedDeleteAppointment());
        }
    }
     
     private void doSearch(){
        String search = searchField.getText().toLowerCase();
        searchResults.clear();
        if(search == ""){
            appointmentTable.setItems(mainScheduler.appointments);
        }else{
            for(int i = 0; i < mainScheduler.appointments.size(); i++){
                if(mainScheduler.appointments.get(i).getTitle().get().toLowerCase().contains(search)){
                    searchResults.add(mainScheduler.appointments.get(i));
                }
            } 
            
            appointmentTable.setItems(searchResults);
        }
    }
    
 
    
}
