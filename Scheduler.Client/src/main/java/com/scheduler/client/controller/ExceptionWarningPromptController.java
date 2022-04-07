package com.scheduler.client.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class ExceptionWarningPromptController implements Initializable {
    public static String warningMsg;
    public static boolean proceedConfirmed;

    @FXML private Label promptLabel;
    @FXML private Button submitBtn;
    @FXML private Button cancelBtn;

    /**
     * Called to initialize a controller after its root element has been completely processed.
     *
     * @param location  The location used to resolve relative paths for the root object, or
     *                  {@code null} if the location is not known.
     * @param resources The resources used to localize the root object, or {@code null} if the root object was not
     *                  localized.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        proceedConfirmed = false;
        promptLabel.setText(warningMsg);

        submitBtn.setText("Yes");
        cancelBtn.setText("No");

        cancelBtn.setOnAction(e -> {
            Stage currWindow = (Stage) submitBtn.getScene().getWindow();
            currWindow.close();
        });
        submitBtn.setOnAction(e -> {
            ExceptionWarningPromptController.proceedConfirmed = true;
            Stage currWindow = (Stage) submitBtn.getScene().getWindow();
            currWindow.close();
        });
    }
}
