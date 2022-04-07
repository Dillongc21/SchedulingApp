package com.scheduler.client.util;

import com.scheduler.client.MainApp;
import com.scheduler.client.controller.*;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import javax.management.modelmbean.ModelMBean;

public class Navigator {
    public static void goToLogin() {
        MainApp.setScreen("Login.fxml",400,400, new LoginController(), false);
    }
    public static void goToHome() {
        MainApp.setScreen("Home.fxml", 900, 600, new HomeController(), false);
    }
    public static void goToAddCustomer() {
        MainApp.setScreen("CustomerForm.fxml", 500, 600,
                new AddCustomerController(), false);
    }
    public static void goToModifyCustomer() {
        MainApp.setScreen("CustomerForm.fxml", 500, 600,
                new ModifyCustomerController(), false);
    }
    public static void goToAddAppointment() {
        MainApp.setScreen("AppointmentForm.fxml", 600, 700,
                new AddAppointmentController(), false);
    }
    public static void goToModifyAppointment() {
        MainApp.setScreen("AppointmentForm.fxml", 600, 700,
                new ModifyAppointmentController(), false);
    }
    public static void popUpCustomerDeletePrompt() {
        MainApp.setScreen("PopupPrompt.fxml", 400, 200,
                new DeleteCustomerPromptController(), true);
    }
    public static void popUpAppointmentDeletePrompt() {
        MainApp.setScreen("PopupPrompt.fxml", 400, 200,
                new DeleteAppointmentPromptController(), true);
    }
    public static void popUpUpcomingAppointmentPrompt() {
        MainApp.setScreen("PopupPrompt_OneBtn.fxml", 400, 200,
                new UpcomingAppointmentPromptController(), true);
    }
    public static void popUpExceptionWarningPrompt(String warningMsg) {
        ExceptionWarningPromptController.warningMsg = warningMsg;
        MainApp.setScreen("PopupPrompt.fxml", 400, 200,
                new ExceptionWarningPromptController(), true);
    }
    public static void popUpMonthTypeReport() {
        MainApp.setScreen("MonthTypeTotalReport.fxml", 375, 550,
                new MonthTypeReportController(), true);
    }
    public static void popUpContactScheduleReport() {
        MainApp.setScreen("ScheduleReport.fxml", 900, 500,
                new ContactScheduleReportController(), true);
    }
    public static void popUpCustomerScheduleReport() {
        MainApp.setScreen("ScheduleReport.fxml", 900, 500,
                new CustomerScheduleReportController(), true);
    }
    public static void closeWindow(ActionEvent event) {
        ((Stage)(((Button)event.getSource()).getScene().getWindow())).close();
    }
}
