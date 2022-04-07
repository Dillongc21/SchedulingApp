package com.scheduler.client.controller;

import com.scheduler.client.util.ErrorMessageStore;
import com.scheduler.common.exception.EndBeforeStartException;
import com.scheduler.common.model.Customer;
import com.scheduler.common.util.Formatter;
import com.scheduler.client.util.Navigator;
import com.scheduler.common.exception.AppointmentNotBusinessHoursException;
import com.scheduler.common.exception.AppointmentOverlapException;
import com.scheduler.common.exception.RequiredFieldException;
import com.scheduler.common.model.Appointment;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * Controller class for modifying an {@link Appointment}. The view file that this class controls is
 * <code>AppointmentForm.fxml</code>.
 *
 * @author Dillon Christensen
 */
public class ModifyAppointmentController extends AppointmentController {
    @FXML private TextField idField;
    @FXML private DatePicker createdPicker;
    @FXML private TextField createdByField;
    @FXML private DatePicker updatedPicker;
    @FXML private TextField updatedByField;

    static Appointment appointmentToModify;

    /**
     * Called to initialize a controller after its root element has been completely processed. Calls parent method
     * {@link AppointmentController#initialize(URL, ResourceBundle)}. Initializes class members, then fills in form
     * data with {@link #appointmentToModify} instance.
     *
     * @param location  The location used to resolve relative paths for the root object, or
     *                  {@code null} if the location is not known.
     * @param resources The resources used to localize the root object, or {@code null} if the root object was not
     *                  localized.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);

        String contactName = appointmentToModify.getContactName();
        String customerName = customerService.getNameById(appointmentToModify.getCustomerId());
        String username = userService.getUsernameById(appointmentToModify.getUserId());

        // Populate input fields
        idField.setText(Integer.toString(appointmentToModify.getId()));
        titleField.setText(appointmentToModify.getTitle());
        descriptionField.setText(appointmentToModify.getDescription());
        locationField.setText(appointmentToModify.getLocation());
        contactChoiceBox.setValue(contactName);
        typeField.setText(appointmentToModify.getType());
        startPicker.setValue(appointmentToModify.getStartDefault()
                .toLocalDateTime()
                .toLocalDate());
        endPicker.setValue(appointmentToModify.getEndDefault()
                .toLocalDateTime()
                .toLocalDate());
        startHourSpinner.getValueFactory().setValue(
                Formatter.toStandardHour(appointmentToModify.getStartDefault()));
        startMinuteSpinner.getValueFactory().setValue(
                Formatter.toStandardMinute(appointmentToModify.getStartDefault()));
        startMeridiemSpinner.getValueFactory().setValue(
                Formatter.toStandardMeridiem(appointmentToModify.getStartDefault()));
        endHourSpinner.getValueFactory().setValue(
                Formatter.toStandardHour(appointmentToModify.getEndDefault()));
        endMinuteSpinner.getValueFactory().setValue(
                Formatter.toStandardMinute(appointmentToModify.getEndDefault()));
        endMeridiemSpinner.getValueFactory().setValue(
                Formatter.toStandardMeridiem(appointmentToModify.getEndDefault()));
        customerChoiceBox.setValue(customerName);
        userChoiceBox.setValue(username);
        createdPicker.setValue(appointmentToModify.getCreateDate()
                .toLocalDateTime().toLocalDate());
        createdByField.setText(appointmentToModify.getCreatedBy());
        updatedPicker.setValue(appointmentToModify.getLastUpdate()
                .toLocalDateTime().toLocalDate());
        updatedByField.setText(appointmentToModify.getLastUpdatedBy());
    }

    /**
     * Handles submit button being clicked. Runs {@link AppointmentController#setupSubmit()} and attempts
     * the {@link Appointment} update submission then, replaces the result of the attempted submission in
     * {@link HomeController#customers}, if the result is not <code>null</code>.
     * <p>
     * Displays error message if {@link RequiredFieldException}, {@link AppointmentNotBusinessHoursException}, or
     * {@link AppointmentOverlapException} is thrown
     * @param event {@link ActionEvent} of the button click
     */
    @Override
    protected void handleSubmitBtn(ActionEvent event) {
        try {
            setupSubmit();
            Appointment formAppointment = new Appointment(appointmentToModify.getId(), titleField.getText(),
                    descriptionField.getText(), locationField.getText(), typeField.getText(), startTimestamp,
                    endTimestamp, appointmentToModify.getCreateDate(), appointmentToModify.getCreatedBy(),
                    appointmentToModify.getLastUpdate(), appointmentToModify.getLastUpdatedBy(), customerId, userId,
                    contactId);

            Appointment updated = appointmentService.updateAppointment(formAppointment);

            if (!Objects.isNull(updated)) {
                Appointment defunct = appointmentService.getAppointmentById(updated.getId());
                HomeController.appointments.remove(defunct);
                HomeController.appointments.add(updated);
            }
            Navigator.goToHome();
        } catch (RequiredFieldException | EndBeforeStartException ex) {
            warningLabel.setVisible(true);
            warningLabel.setText(ex.getMessage());
        } catch (AppointmentNotBusinessHoursException ex) {
            Navigator.popUpExceptionWarningPrompt(ErrorMessageStore.APPOINTMENT_HOURS_INVALID);
            if (ExceptionWarningPromptController.proceedConfirmed) {
                businessHoursExceptionOverride = true;
                handleSubmitBtn(new ActionEvent());
            }
        } catch (AppointmentOverlapException ex) {
            Navigator.popUpExceptionWarningPrompt(ErrorMessageStore.APPOINTMENT_OVERLAP);
            if (ExceptionWarningPromptController.proceedConfirmed) {
                overlapExceptionOverride = true;
                handleSubmitBtn(new ActionEvent());
            }
        }
    }
}
