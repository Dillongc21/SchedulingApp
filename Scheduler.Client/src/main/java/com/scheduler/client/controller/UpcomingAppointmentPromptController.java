package com.scheduler.client.controller;

import com.scheduler.client.util.Navigator;
import com.scheduler.common.model.Appointment;
import com.scheduler.common.util.Formatter;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class UpcomingAppointmentPromptController implements Initializable {

    static Appointment notificationAppointment;

    @FXML private Label promptLabel;
    @FXML private Button submitBtn;

    /**
     * Called to initialize a controller after its root element has been completely processed. Initializes class members
     * and set up event handlers.
     *
     * @param location  The location used to resolve relative paths for the root object, or
     *                  {@code null} if the location is not known.
     * @param resources The resources used to localize the root object, or {@code null} if the root object was not
     *                  localized.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        submitBtn.setText("OK");

        String displayDateTime = Formatter.toDisplay(notificationAppointment.getStartDefault());

        promptLabel.setText("Upcoming Appointment:\n" +
                            "ID: " + notificationAppointment.getId() + "\nTitle: " + notificationAppointment.getTitle() +
                            "\n" + displayDateTime);

        submitBtn.setOnAction(e -> {
            Stage currWindow = (Stage) submitBtn.getScene().getWindow();
            currWindow.close();
        });
    }
}
