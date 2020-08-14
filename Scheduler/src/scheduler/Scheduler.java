/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler;

import java.sql.Connection;
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
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import scheduler.Controller.AppointmentController;
import scheduler.Controller.AppointmentScreenController;
import scheduler.Controller.CustomerController;
import scheduler.Controller.CustomerScreenController;
import scheduler.Controller.LoginScreenController;
import scheduler.Controller.MainMenuController;
import scheduler.Model.Appointment;
import scheduler.Model.Constants;
import scheduler.Model.Customer;

/**
 *
 * @author Tyler
 */
public class Scheduler extends Application {

    
    public ObservableList<Customer> customers = FXCollections.observableArrayList();
    public ObservableList<Appointment> appointments = FXCollections.observableArrayList();
    public String currentUser;
    
    private Stage mainStage = null;
    private Connection sqlConnection = null;

    @Override
    public void start(Stage primaryStage) {
        currentUser = null;
        mainStage = primaryStage;
        openLoginWindow();

    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    public void openLoginWindow() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Scheduler.class.getResource(Constants.LoginScreen));
            AnchorPane root = loader.load();

            LoginScreenController loginWindow = loader.getController();
            loginWindow.setController(this);
            Scene scene = new Scene(root);
            System.out.println("Show scene");
            mainStage.setScene(scene);
            mainStage.show();
        } catch (Exception e) {
            System.out.println(e.toString());
            e.printStackTrace();
        }
    }

    public void openMainWindow() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Scheduler.class.getResource(Constants.MainMenu));
            AnchorPane root = loader.load();
            MainMenuController mainMenu = loader.getController();
            mainMenu.setConnection(sqlConnection);
            mainMenu.setController(this);
            Scene scene = new Scene(root);
            System.out.println("Show scene");
            mainStage.setScene(scene);
            mainStage.show();
        } catch (Exception e) {
            System.out.println(e.toString());
            e.printStackTrace();
        }
    }

    public void openAppointmentWindow() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Scheduler.class.getResource(Constants.AppointmentScreen));
            AnchorPane root = loader.load();
            AppointmentScreenController mainMenu = loader.getController();

            mainMenu.setController(this);
            mainMenu.setConnection(sqlConnection);

            Scene scene = new Scene(root);
            System.out.println("Show scene");
            mainStage.setScene(scene);
            mainStage.show();
        } catch (Exception e) {
            System.out.println(e.toString());
            e.printStackTrace();
        }
    }

    public void openAddAppointmentWindow() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Scheduler.class.getResource(Constants.AddAppointment));
            AnchorPane root = loader.load();
            AppointmentController addMenu = loader.getController();

            addMenu.setController(this);
            addMenu.setConnection(sqlConnection);
            Scene scene = new Scene(root);
            Stage addAppointment = new Stage();
            addAppointment.initModality(Modality.APPLICATION_MODAL);
            addAppointment.initOwner(mainStage);
            System.out.println("Show scene");
            addAppointment.setScene(scene);
            addAppointment.show();
        } catch (Exception e) {
            System.out.println(e.toString());
            e.printStackTrace();
        }
    }
    
    public void openModifyAppointmentWindow(Appointment c) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Scheduler.class.getResource(Constants.AddAppointment));
            AnchorPane root = loader.load();
            AppointmentController addMenu = loader.getController();
            addMenu.setAppointment(c);
            addMenu.setController(this);
            addMenu.setConnection(sqlConnection);
            Scene scene = new Scene(root);
            Stage addAppointment = new Stage();
            addAppointment.initModality(Modality.APPLICATION_MODAL);
            addAppointment.initOwner(mainStage);
            System.out.println("Show scene");
            addAppointment.setScene(scene);
            addAppointment.show();
        } catch (Exception e) {
            System.out.println(e.toString());
            e.printStackTrace();
        }
    }

    
    public void openCustomerMenu() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Scheduler.class.getResource(Constants.CustomerScreen));
            AnchorPane root = loader.load();
            CustomerScreenController mainMenu = loader.getController();

            mainMenu.setController(this);
            mainMenu.setConnection(sqlConnection);

            Scene scene = new Scene(root);
            System.out.println("Show scene");
            mainStage.setScene(scene);
            mainStage.show();
        } catch (Exception e) {
            System.out.println(e.toString());
            e.printStackTrace();
        }
    }

    public void openAddCustomerMenu() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Scheduler.class.getResource(Constants.AddCustomer));
            AnchorPane root = (AnchorPane) loader.load();

            
            CustomerController addMenu = loader.getController();
            addMenu.setController(this);
            Scene scene = new Scene(root);
            System.out.println("Show scene");
            Stage addCustomer = new Stage();
            addCustomer.initModality(Modality.APPLICATION_MODAL);
            addCustomer.initOwner(mainStage);
            addCustomer.setScene(scene);
            addMenu.setConnection(sqlConnection);
            addCustomer.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    public void openModifyCustomerMenu(Customer c) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Scheduler.class.getResource(Constants.AddCustomer));
            AnchorPane root = (AnchorPane) loader.load();

            CustomerController addMenu = loader.getController();
            addMenu.setController(this);
            Scene scene = new Scene(root);
            System.out.println("Show scene");
            Stage addCustomer = new Stage();
            addMenu.setCustomer(c);
            addCustomer.initModality(Modality.APPLICATION_MODAL);
            addCustomer.initOwner(mainStage);
            addCustomer.setScene(scene);
            addCustomer.show();
            addMenu.setConnection(sqlConnection);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public void setConnection(Connection connection) {
        this.sqlConnection = connection;
    }

    @Override
    public void stop() {

    }
}
