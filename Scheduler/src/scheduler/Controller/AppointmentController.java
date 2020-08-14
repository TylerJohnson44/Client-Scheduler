/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler.Controller;

import java.net.URL;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import scheduler.Model.Appointment;
import scheduler.Model.Constants;
import scheduler.Model.Customer;
import scheduler.Scheduler;

/**
 *
 * @author Tyler
 */
public class AppointmentController implements Initializable{

    private Connection sqlConnection;
    private Scheduler mainScheduler;

    
    @FXML TextField titleField;
    @FXML TextField locationField;
    @FXML TextField contactField;
    @FXML TextField urlField;
    @FXML TextField searchField;
    @FXML ComboBox<String> startTimeField;
    @FXML ComboBox<String> endTimeField;
    @FXML ComboBox<String> typeField;
    @FXML DatePicker datePicker;
    
    @FXML TableView<Customer> customerTable;
    @FXML TableColumn<Customer, String> idColumn;
    @FXML TableColumn<Customer, String> nameColumn;

    
    private Appointment appointment;
    private ObservableList<Customer> searchResults = FXCollections.observableArrayList();

    
    private final DateTimeFormatter dateGet = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final ObservableList<String> types = FXCollections.observableArrayList();
    private final ObservableList<String> startTimes = FXCollections.observableArrayList();
    private final ObservableList<String> endTimes = FXCollections.observableArrayList();
    private final DateTimeFormatter timeDTF = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT);
    private final DateTimeFormatter dateDTF = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT);
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        sqlConnection = null;
        mainScheduler = null;
        appointment = null;
        initCallbacks();
        initCombo();
    }
    
    private void initCallbacks(){
        
        
        idColumn.setCellValueFactory(cellData -> cellData.getValue().getId().asString());
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().getName());
        //Both the columns are setup via anonymous lambda, it allows for the code to be much smaller than creating a non-anonymous function call like the datepicker below
        datePicker.setDayCellFactory(picker -> new DateCell() {
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                LocalDate today = LocalDate.now();

                setDisable(empty || date.compareTo(today) < 0 );
            }
        });
    }
    private void initCombo(){
       LocalTime time = LocalTime.of(8, 0);
	do {
		startTimes.add(time.format(timeDTF));
		endTimes.add(time.format(timeDTF));
		time = time.plusMinutes(15);
	} while(!time.equals(LocalTime.of(17, 15)));
        
	startTimes.remove(startTimes.size() - 1);
	endTimes.remove(0);
        
        datePicker.setValue(LocalDate.now());
        startTimeField.setItems(startTimes);
	endTimeField.setItems(endTimes);
	startTimeField.getSelectionModel().select(LocalTime.of(8, 0).format(timeDTF));
	endTimeField.getSelectionModel().select(LocalTime.of(8, 15).format(timeDTF));
        
        types.addAll("Consultation", "Follow up", "Counseling");
        typeField.setItems(types);
        typeField.getSelectionModel().select(0);
    }


    
    public void setConnection(Connection sqlConnection){
        this.sqlConnection = sqlConnection;
    }

    public void setController(Scheduler mainScheduler){
        this.mainScheduler = mainScheduler;
        customerTable.setItems(mainScheduler.customers);
    }

    public void setAppointment(Appointment c){
        try{
            titleField.setText(c.getTitle().getValue());
            locationField.setText(c.getLocation().getValue());
            contactField.setText(c.getContact().getValue());
            urlField.setText(c.getUrl().getValue());
            
            typeField.setValue(c.getType().getValue());
            String start = c.getStartTime().getValue();
            String end = c.getEndTime().getValue();
            
             System.out.println("Start: " + start);
            System.out.println("End: " + end);
            LocalDateTime startDatetime = LocalDateTime.parse(start, dateDTF);
            LocalDateTime endDateTime = LocalDateTime.parse(end, dateDTF);        
            LocalDate date = LocalDate.parse(start, dateDTF);
           
            System.out.println("Date: " + date);
            //System.out.println("End: " + end);
            datePicker.setValue(date);
            
            String boxStartFormatted = startDatetime.toLocalTime().format(timeDTF);
            String boxEndFormatted = endDateTime.toLocalTime().format(timeDTF);
            
            System.out.println("Formatted for box start: " + boxStartFormatted);
            System.out.println("Formatted for box end: " + boxEndFormatted);
            
            startTimeField.getSelectionModel().select(boxStartFormatted);
            endTimeField.getSelectionModel().select(boxEndFormatted);
            
            customerTable.getSelectionModel().select(c.getCustomerId());
            this.appointment = c;
            
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleButtonAction(ActionEvent event) {
        Button button = (Button) event.getSource();
        String id = button.getId();
        System.out.println("Button pressed: " + id);
        switch(id){
            case "acceptButton":
                if(validateForm()){
                    if(checkSelection()){
                        if(appointment == null){
                            if(saveForm()){
                                ((Stage) button.getScene().getWindow()).close();
                            }
                        }else{
                            if(updateForm()){
                                ((Stage) button.getScene().getWindow()).close();
                            }
                        }
                       
                    }
                }
                break;
            case "cancelButton":
                ((Stage) button.getScene().getWindow()).close();
                break;
            case "searchButton":
                doSearch();
                break;
        }
    }
    
    



    private boolean checkSelection(){
        if(customerTable.getSelectionModel().getSelectedItem() != null)
            return true;
        else
            return false;
    }

    private void setSelection(Customer c){
        if(customerTable != null){
            customerTable.getSelectionModel().select(c);
        }
    }

    private void doSearch(){
        String search = searchField.getText().toLowerCase();
        searchResults.clear();
        if(search.isEmpty()){
            customerTable.setItems(mainScheduler.customers);
        }else{
            for(int i = 0; i < mainScheduler.customers.size(); i++){
                if(mainScheduler.customers.get(i).getId().getValue().toString().toLowerCase().contains(search)){
                    searchResults.add(mainScheduler.customers.get(i));
                }
            } 
            
            customerTable.setItems(searchResults);
        }
    }

    private void showErrorAlert(String message){
        Alert alert = new Alert(AlertType.ERROR);
        alert.setHeaderText(message);
        alert.showAndWait();
    }

    private boolean saveForm(){
        //TODO: implement   
        
        LocalDate localDate = datePicker.getValue();
	LocalTime startTime = LocalTime.parse(startTimeField.getSelectionModel().getSelectedItem(), timeDTF);
	LocalTime endTime = LocalTime.parse(endTimeField.getSelectionModel().getSelectedItem(), timeDTF);
        
        LocalDateTime startDT = LocalDateTime.of(localDate, startTime);
        LocalDateTime endDT = LocalDateTime.of(localDate, endTime);

        ZonedDateTime startUTC = startDT.atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneId.of("UTC"));
        ZonedDateTime endUTC = endDT.atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneId.of("UTC"));            
	
	Timestamp startsqlts = Timestamp.valueOf(startUTC.toLocalDateTime()); 
        Timestamp endsqlts = Timestamp.valueOf(endUTC.toLocalDateTime());     

        
        try{
            Statement st = sqlConnection.createStatement();
            int retVal = st.executeUpdate("INSERT INTO appointment (customerId, title, description, location, contact, url, start, end, createDate, createdBy, lastUpdate, lastUpdateBy) VALUES(" +
                                                customerTable.getSelectionModel().getSelectedItem().getId().getValue() + ", \"" + titleField.getText() + "\", \"" + typeField.getValue() + "\", \"" +
                                                locationField.getText() + "\", \"" + contactField.getText() + "\", \"" + urlField.getText() + "\", \"" + startsqlts + "\", \"" + endsqlts + "\", CURRENT_TIMESTAMP, \"" +
                                                mainScheduler.currentUser + "\", CURRENT_TIMESTAMP, \"" + mainScheduler.currentUser + "\")");
            
            if(retVal > 0){
                
                PreparedStatement pst = sqlConnection.prepareStatement("SELECT * FROM appointment WHERE start = ? AND end = ?");
                pst.setString(1, startsqlts.toString());
                pst.setString(2, endsqlts.toString());
                
                ResultSet res = pst.executeQuery();
                if(!res.next()){
                    new NullPointerException();
                }
                System.out.println("Updated " + retVal + " rows");
                Customer customer= customerTable.getSelectionModel().getSelectedItem();
                mainScheduler.appointments.add(new Appointment(titleField.getText(), locationField.getText(), startDT.format(dateDTF), endDT.format(dateDTF), contactField.getText(), typeField.getValue(), 
                        res.getInt("appointmentId"), urlField.getText(), customer));
                return true;
            }else{
                System.out.println("No updates!");
            }
        }catch(Exception e ){
            e.printStackTrace();
            showErrorAlert(Constants.Messages.getFailedAddAppointment());
        }
        return false;
    }
    
    private boolean updateForm(){
        LocalDate localDate = datePicker.getValue();
	LocalTime startTime = LocalTime.parse(startTimeField.getSelectionModel().getSelectedItem(), timeDTF);
	LocalTime endTime = LocalTime.parse(endTimeField.getSelectionModel().getSelectedItem(), timeDTF);
        
        LocalDateTime startDT = LocalDateTime.of(localDate, startTime);
        LocalDateTime endDT = LocalDateTime.of(localDate, endTime);

        ZonedDateTime startUTC = startDT.atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneId.of("UTC"));
        ZonedDateTime endUTC = endDT.atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneId.of("UTC"));            
	
	Timestamp startsqlts = Timestamp.valueOf(startUTC.toLocalDateTime()); //this value can be inserted into database
        Timestamp endsqlts = Timestamp.valueOf(endUTC.toLocalDateTime()); //this value can be inserted into database        
       
        
        System.out.println("Timestamp value start: " + startsqlts.toString() + " end: " + endsqlts.toString());
        try{
                PreparedStatement st = sqlConnection.prepareStatement("UPDATE appointment SET customerId = ?, title = ?, description = ?, start = ?, end = ?, lastUpdate = CURRENT_TIMESTAMP, lastUpdateBy = ?, url = ?, location = ?, contact = ? WHERE appointmentId = ?");
            
                st.setString(1, Integer.toString(customerTable.getSelectionModel().getSelectedItem().getId().getValue().intValue()));
                st.setString(2, titleField.getText());
                st.setString(3, typeField.getValue());
                st.setString(4, startsqlts.toString());
                st.setString(5, endsqlts.toString());
                st.setString(6, mainScheduler.currentUser);
                st.setString(7, urlField.getText());
                st.setString(8, locationField.getText());
                st.setString(9, contactField.getText());
                st.setString(10, Integer.toString(appointment.getId().getValue().intValue()));
                
                if(st.executeUpdate() > 0){
                    System.out.println("Updated appointment");
                     
                    
                    for(int i = 0; i < mainScheduler.appointments.size(); i++){
                        if(mainScheduler.appointments.get(i).getId().getValue().intValue() == appointment.getId().getValue().intValue()){
                            mainScheduler.appointments.get(i).getContact().set(contactField.getText());
                            mainScheduler.appointments.get(i).setCustomer(customerTable.getSelectionModel().getSelectedItem());
                            mainScheduler.appointments.get(i).getEndTime().set(endDT.format(dateDTF));
                            mainScheduler.appointments.get(i).getStartTime().set(startDT.format(dateDTF));
                            mainScheduler.appointments.get(i).getUrl().set(urlField.getText());
                            mainScheduler.appointments.get(i).getLocation().set(locationField.getText());
                            mainScheduler.appointments.get(i).getTitle().set(titleField.getText());
                            mainScheduler.appointments.get(i).getType().set(typeField.getValue());
                        }
                        
                    }
                    return true;
                }else{
                    System.out.println("Failed to update appointment");
                    showErrorAlert(Constants.Messages.getFailedUpdate());
                }
                
        }catch(Exception e){
            e.printStackTrace();
            showErrorAlert(Constants.Messages.getFailedUpdate());

        }
        return false;
    }
    
    private boolean validateAppointment(){
        LocalDate localDate = datePicker.getValue();
        LocalTime startTime = LocalTime.parse(startTimeField.getSelectionModel().getSelectedItem(), timeDTF);
	LocalTime endTime = LocalTime.parse(endTimeField.getSelectionModel().getSelectedItem(), timeDTF);
        
        LocalDateTime startDT = LocalDateTime.of(localDate, startTime);
        LocalDateTime endDT = LocalDateTime.of(localDate, endTime);

        ZonedDateTime startUTC = startDT.atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneId.of("UTC"));
        ZonedDateTime endUTC = endDT.atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneId.of("UTC"));   
        
        if(startUTC.isAfter(endUTC) || startUTC.isEqual(endUTC)){
            showErrorAlert(Constants.Messages.getConflict());
            return false;
        }
        int appointmentId = -1;
        
        if(appointment != null){
            appointmentId = appointment.getId().getValue().intValue();
        }
        
        try{
            //Builds a prepared statement with ? being filled by index positions below
            PreparedStatement st = sqlConnection.prepareStatement("SELECT * FROM appointment WHERE ((? >= start AND ? < end) OR (? > start AND ? <= end) OR (? < start AND ? > end)) AND (createdBy = ? AND appointmentID != ?)");
            
            //It would've been nice to know about this before
            st.setString(1, Timestamp.valueOf(startUTC.toLocalDateTime()).toString());
            st.setString(2, Timestamp.valueOf(startUTC.toLocalDateTime()).toString());
            st.setString(3, Timestamp.valueOf(endUTC.toLocalDateTime()).toString());
            st.setString(4, Timestamp.valueOf(endUTC.toLocalDateTime()).toString());
            st.setString(5, Timestamp.valueOf(startUTC.toLocalDateTime()).toString());
            st.setString(6, Timestamp.valueOf(endUTC.toLocalDateTime()).toString());
            st.setString(7, mainScheduler.currentUser);
            st.setString(8, Integer.toString(appointmentId));
            
            ResultSet set = st.executeQuery();
            
            if(set.next()){
                showErrorAlert(Constants.Messages.getConflict());
                return false;
            }
        }catch(Exception e){
            e.printStackTrace();
            showErrorAlert(Constants.Messages.getConflict());

        }
        System.out.println("No appointment conflict");
        return true;
    }
    
    private boolean validateForm(){
        System.out.println("Type value: " + typeField.getValue());
        try{
            if(titleField.getText().isEmpty() ||
                    locationField.getText().isEmpty() ||
                    contactField.getText().isEmpty() ||
                    urlField.getText().isEmpty() || 
                    endTimeField.getSelectionModel().getSelectedItem().isEmpty() ||
                    startTimeField.getSelectionModel().getSelectedItem().isEmpty() ||
                    datePicker.getValue() == null || 
                    !validateAppointment()){
              
                showErrorAlert(Constants.Messages.getInvalidInputError());
                return false;
            }
            if(customerTable == null ||
                    customerTable.getSelectionModel().getSelectedItem() == null){
                showErrorAlert(Constants.Messages.getNeedsCustomerChosenError());
            }
        }catch(Exception e){
            e.printStackTrace();
            showErrorAlert(Constants.Messages.getInvalidInputError());

            return false;
        }
        
        return true;
    }
}
