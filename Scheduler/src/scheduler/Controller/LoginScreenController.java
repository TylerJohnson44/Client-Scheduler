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
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import scheduler.Model.Constants;
import scheduler.Scheduler;

/**
 * FXML Controller class
 *
 * @author Tyler
 */
public class LoginScreenController implements Initializable {

    @FXML Button loginButton;
    @FXML TextField usernameField;
    @FXML TextField passwordField;
    
    @FXML Label usernameLabel; 
    @FXML Label passwordLabel;
    
    private Scheduler mainScheduler = null;
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }    
    
     @FXML
    private void handleButtonAction(ActionEvent event) {
        Button button = (Button) event.getSource();
        String id = button.getId();
        
        switch(id){
            case "loginButton":
                if(isSchedulerValid()){
                    processLogin();

                }
                break;
        }
    }
    
    private void processLogin(){


        String driver = "com.mysql.jdbc.Driver";
        String db = "applicationdb";
        String url = "jdbc:mysql://localhost:3306/" + db;
        String user = "application";
        String pass = "applogin_10";
        Connection conn = null;
        
        if(usernameField.getText().isEmpty() || passwordField.getText().isEmpty())
                return;
        try {
            Class.forName(driver);
            conn = DriverManager.getConnection(url,user,pass);
            
            Statement s = conn.createStatement();
            ResultSet res = s.executeQuery("SELECT * FROM users WHERE username='" +usernameField.getText() + "' AND password='" + passwordField.getText() + "'");
            
            if(res.next()){
                System.out.println("Connected to database ");
                mainScheduler.setConnection(conn);
                mainScheduler.currentUser = usernameField.getText();
                logData();
                mainScheduler.openMainWindow();

            }else{
                showErrorAlert(Constants.Messages.getLoginError());
            }
            
        } catch (SQLException ex) {
            System.out.println("SQLException: "+ex.getMessage());
            System.out.println("SQLState: "+ex.getSQLState());
            System.out.println("VendorError: "+ex.getErrorCode());
        } catch (Exception e) {
            e.printStackTrace();
        } 
        

        
    }
    private void logData(){
        try{
            File yourFile = new File("logins.log");
            yourFile.createNewFile(); // if file already exists will do nothing 
            FileOutputStream oFile = new FileOutputStream(yourFile, true); 
            PrintWriter p = new PrintWriter(oFile);
            p.println(LocalDateTime.now().toString() + " : " + mainScheduler.currentUser);
            p.close();
            oFile.close();

        }catch (Exception e){
            showErrorAlert("Failed to write file");
        }

    }
    private boolean isSchedulerValid(){
        if(mainScheduler == null){
            showErrorAlert(Constants.Messages.getNoSchedulerError());
            return false;
        }
        return true;
    }
    public void setController(Scheduler scheduler){
        this.mainScheduler = scheduler;
    }
    
    private void showErrorAlert(String message){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(message);
        alert.showAndWait();
    }
}
