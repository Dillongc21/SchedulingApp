package com.scheduler.client.controller;

import com.scheduler.client.util.ErrorMessageStore;
import com.scheduler.client.util.Navigator;
import com.scheduler.common.exception.AppointmentNotBusinessHoursException;
import com.scheduler.common.exception.AppointmentOverlapException;
import com.scheduler.common.exception.EndBeforeStartException;
import com.scheduler.common.exception.RequiredFieldException;
import com.scheduler.common.model.Appointment;
import javafx.event.ActionEvent;

import java.util.Objects;

public class AddAppointmentController extends AppointmentController {

    /**
     * Handles submit button being clicked. Runs {@link AppointmentController#setupSubmit()} and attempts
     * the {@link Appointment} update submission then, adds the result of the attempted submission in
     * {@link HomeController#appointments}, if the result is not <code>null</code>.
     * <p>
     * Displays error message if {@link RequiredFieldException}, {@link AppointmentNotBusinessHoursException}, or
     * {@link AppointmentOverlapException} is thrown
     * @param event {@link ActionEvent} of the button click
     */
    @Override
    protected void handleSubmitBtn(ActionEvent event) {
        try {
            setupSubmit();
            Appointment formAppointment = new Appointment(titleField.getText(), descriptionField.getText(),
                    locationField.getText(), typeField.getText(), startTimestamp, endTimestamp, customerId, userId,
                    contactId);
            Appointment created = appointmentService.createAppointment(formAppointment);

            if (!Objects.isNull(created))
                HomeController.appointments.add(created);
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
