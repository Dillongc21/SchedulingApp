package com.scheduler.client.controller;

import com.scheduler.business.UserService;
import com.scheduler.client.util.ErrorMessageStore;
import com.scheduler.client.util.Navigator;
import com.scheduler.common.exception.PasswordIncorrectException;
import com.scheduler.common.exception.SetFileReadOnlyFailedException;
import com.scheduler.common.exception.SetFileWritableFailedException;
import com.scheduler.common.exception.UserNotFoundException;
import com.scheduler.common.model.User;
import com.scheduler.common.util.Formatter;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    private UserService service;
    private ResourceBundle bundle;

    @FXML private TextField userField;
    @FXML private PasswordField passField;
    @FXML private Label greetingLabel;
    @FXML private Label userLabel;
    @FXML private Label passLabel;
    @FXML private Button signinBtn;
    @FXML private Label notifyMsg;
    @FXML private Label localeDesc;

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
        signinBtn.setOnAction(this::handleSubmit);
        userField.setOnAction(this::handleSubmit);
        passField.setOnAction(this::handleSubmit);

        service = UserService.getInstance();

        localeDesc.setText(localeDesc.getText() + ZoneId.systemDefault());
        bundle = ResourceBundle.getBundle("localization.Login", Locale.getDefault());

        greetingLabel.setText(bundle.getString("greeting"));
        userLabel.setText(bundle.getString("username") + ":");
        passLabel.setText(bundle.getString("password") + ":");
        signinBtn.setText(bundle.getString("signin"));
    }

    public void handleSubmit(ActionEvent event) {

        try {
            String username = userField.getText();
            String password = passField.getText();

            User user = service.getUserByUsername(username);

            if(user == null) {
                String errorMsg = bundle.getString("user_nf_error");
                writeToLoginAttemptFile(username, errorMsg);
                throw new UserNotFoundException(errorMsg);
            }
            if (!password.equals(user.getPassword())) {
                String errorMsg = bundle.getString("pass_error");
                writeToLoginAttemptFile(username, errorMsg);
                throw new PasswordIncorrectException(errorMsg);
            }
            writeToLoginAttemptFile(username, null);
            Navigator.goToHome();

        } catch (UserNotFoundException | PasswordIncorrectException e) {
            notifyMsg.setText(e.getMessage());
        } catch (IOException | SetFileReadOnlyFailedException | SetFileWritableFailedException ex) {
            ex.printStackTrace();
        }
    }

    private void writeToLoginAttemptFile(String username, String failureCause) throws IOException,
            SetFileWritableFailedException, SetFileReadOnlyFailedException {
        String successFailStr = Objects.isNull(failureCause) ? "Success" : "Failed | Cause: " + failureCause;
        String dateTimeStr = Formatter.toAttemptDisplay(ZonedDateTime.now());
        String lineToWrite = "User: " + username + " | " + dateTimeStr + " | " + successFailStr + System.lineSeparator();
        File file = new File("login_activity.txt");
        FileWriter fileWriter;
        if (file.exists()) {
            if (file.setWritable(true))
                fileWriter = new FileWriter(file, true);
            else
                throw new SetFileWritableFailedException(ErrorMessageStore.SET_FILE_WRITABLE_FAILED);
        } else {
            fileWriter = new FileWriter(file);
        }
        BufferedWriter writer = new BufferedWriter(fileWriter);
        writer.append(lineToWrite);
        writer.close();
        if (!file.setReadOnly())
            throw new SetFileReadOnlyFailedException(ErrorMessageStore.SET_FILE_READONLY_FAILED);
    }
}
