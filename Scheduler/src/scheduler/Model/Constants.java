/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler.Model;

import java.util.Locale;

/**
 *
 * @author Tyler
 */
public class Constants {
    
    public static final String LoginScreen = "/scheduler/View/LOGINSCREEN.fxml";
    public static final String MainMenu = "/scheduler/View/MAINMENU.fxml";
    public static final String CustomerScreen = "/scheduler/View/CUSTOMERTABLE.fxml";
    public static final String AppointmentScreen = "/scheduler/View/APPOINTMENTTABLE.fxml";
    public static final String AddAppointment = "/scheduler/View/ADDAPPOINTMENT.fxml";
    public static final String AddCustomer = "/scheduler/View/ADDCUSTOMER.fxml";
    
    
    /*
        I did not like the way that dealing with ResourceBundles looked so I made this simple utility
    */
    
    public static class Messages{
        
        static String lang = Locale.getDefault().toString().toLowerCase();
        static boolean isFrench = lang.contains("fr");
        
        //English Messages
        private static class EnglishMessage{
            public final static String invalidLogin = "Failed to login, incorrect username or password";
            public final static String noScheduler = "Message, no scheduler set.";
            public final static String invalidInput = "Please make sure to fill out all fields";
            public final static String appointmentWarning = "You have an appointment within 15 minutes!";
            public final static String failedCustomerDelete = "Failed to delete customer, delete all appointments from customer first if this was not a mistake!";
            public final static String failedCustomerAdd = "Failed to add customer";
            public final static String needsDateChosen = "Please click choose a date as well as a filter from the dropdown";
            public final static String failedGetAppointment = "Error getting appointment";
            public final static String failedFilterAppointment = "Failed to filter appointments";
            public final static String failedDeleteAppointment = "Failed to delete appointment";
            
            public final static String failedAddAppointment = "Failed to add appointment";
            public final static String appointmentConflict = "Conflicting appointment, check schedule";
            
            public final static String failedUpdate = "Failed to update";
            public final static String getNeedsCustomerChosenError = "Select a customer from the table";
            
            public final static String failedWrite = "Failed to write file";
            
        }
        
        //French Messages
        private static class FrenchMessage{
            public final static String invalidLogin = "Échec de la connexion, nom d'utilisateur ou mot de passe incorrect";
            public final static String noScheduler = "Erreur, pas de planificateur défini";
            public final static String invalidInput = "Assurez-vous de remplir tous les champs";
			
            public final static String appointmentWarning = "Vous avez rendez-vous dans les 15 minutes!";
            public final static String failedCustomerDelete = "Échec de la suppression du client, supprimez d'abord tous les rendez-vous du client s'il ne s'agissait pas d'une erreur!";
            public final static String failedCustomerAdd = "Impossible d'ajouter le client";
            public final static String needsDateChosen = "S'il vous plaît cliquer sur choisir une date ainsi qu'un filtre de la liste déroulante";
            public final static String failedGetAppointment = "Erreur lors du rendez-vous";
            public final static String failedFilterAppointment = "Impossible de filtrer les rendez-vous";
            public final static String failedDeleteAppointment = "Échec de la suppression du rendez-vous";

            public final static String failedAddAppointment = "Impossible d'ajouter un rendez-vous";
            public final static String appointmentConflict = "Rendez-vous conflictuel, vérifiez l'horaire";
            public final static String failedUpdate = "Échec de mise à jour";

             public final static String getNeedsCustomerChosenError = "Sélectionnez un client de la table";
               
            public final static String failedWrite = "Échec de l'écriture du fichier";
        }
        public static String getFailedWrite(){
            return (isFrench ? FrenchMessage.failedWrite : EnglishMessage.failedWrite);
        }
        public static String getNeedsCustomerChosenError(){
            return (isFrench ? FrenchMessage.getNeedsCustomerChosenError : EnglishMessage.getNeedsCustomerChosenError);
        }
        
        public static String getFailedUpdate(){
            return (isFrench ? FrenchMessage.failedUpdate : EnglishMessage.failedUpdate);
        }
        
        //Login screen 
        public static String getLoginError(){
            return (isFrench ? FrenchMessage.invalidLogin : EnglishMessage.invalidLogin);
        }
        

        //Main menu 
        public static String getAppointmentWarning(){
            return (isFrench ? FrenchMessage.appointmentWarning : EnglishMessage.appointmentWarning);
	}
        
        //Appointment screen 
        public static String getNeedsDateChosenError(){
            return (isFrench ? FrenchMessage.needsDateChosen : EnglishMessage.needsDateChosen);
        }
        public static String failedGetAppointment(){
            return (isFrench ? FrenchMessage.failedGetAppointment : EnglishMessage.failedGetAppointment);
        }
        public static String failedFilterAppointment(){
            return (isFrench ? FrenchMessage.failedFilterAppointment : EnglishMessage.failedFilterAppointment);
        }
        public static String failedDeleteAppointment(){
            return (isFrench ? FrenchMessage.failedDeleteAppointment : EnglishMessage.failedDeleteAppointment);
        }
        
        //Appointment add screen 
        
        
        //Customer screen 
        public static String getFailedCustomerDelete(){
            return (isFrench ? FrenchMessage.failedCustomerDelete : EnglishMessage.failedCustomerDelete);
        }
        
        //Customer add screen 
        public static String getFailedCustomerAdd(){
            return (isFrench ? FrenchMessage.failedCustomerAdd : EnglishMessage.failedCustomerAdd);
        }
        
        //Generic
        public static String getNoSchedulerError(){
            return (isFrench ? FrenchMessage.noScheduler : EnglishMessage.noScheduler);
        }
        
        public static String getInvalidInputError(){
            return (isFrench ? FrenchMessage.invalidInput : EnglishMessage.invalidInput);
        }
        
        
        public static String getFailedAddAppointment(){
            return (isFrench ? FrenchMessage.failedAddAppointment : EnglishMessage.failedAddAppointment);
        }
        public static String getConflict(){
            return (isFrench ? FrenchMessage.appointmentConflict : EnglishMessage.appointmentConflict);
        }
    }
}
