package com.scheduler.client.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public abstract class DeletePopupController implements Initializable {

    static boolean deleteConfirmed = false;

    @FXML protected Label promptLabel;
    @FXML private Button cancelBtn;
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
        DeletePopupController.deleteConfirmed = false;

        cancelBtn.setText("No");
        submitBtn.setText("Yes");

        cancelBtn.setOnAction(event -> {
            Stage currWindow = (Stage) cancelBtn.getScene().getWindow();
            currWindow.close();
        });
        submitBtn.setOnAction(event -> {
            DeletePopupController.deleteConfirmed = true;
            Stage currWindow = (Stage) submitBtn.getScene().getWindow();
            currWindow.close();
        });
    }
}
