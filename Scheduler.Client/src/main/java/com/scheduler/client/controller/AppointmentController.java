package com.scheduler.client.controller;

import com.scheduler.business.AppointmentService;
import com.scheduler.business.ContactService;
import com.scheduler.business.CustomerService;
import com.scheduler.business.UserService;
import com.scheduler.client.util.ErrorMessageStore;
import com.scheduler.common.exception.EndBeforeStartException;
import com.scheduler.common.util.Formatter;
import com.scheduler.client.util.Initializer;
import com.scheduler.client.util.Navigator;
import com.scheduler.common.exception.AppointmentNotBusinessHoursException;
import com.scheduler.common.exception.AppointmentOverlapException;
import com.scheduler.common.exception.RequiredFieldException;
import com.scheduler.common.util.TimeMeridiem;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.sql.Timestamp;
import java.time.*;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * Abstract controller class that provides common functionality to (is the Parent of) {@link AddAppointmentController}
 * and {@link ModifyAppointmentController}. The view file that this class controls is <code>CustomerForm.fxml</code>.
 *
 * @author Dillon Christensen
 */
public abstract class AppointmentController implements Initializable {

    protected static boolean businessHoursExceptionOverride;
    protected static boolean overlapExceptionOverride;

    @FXML protected TextField titleField;
    @FXML protected TextField descriptionField;
    @FXML protected TextField locationField;
    @FXML protected ChoiceBox<String> contactChoiceBox;
    @FXML protected TextField typeField;
    @FXML protected DatePicker startPicker;
    @FXML protected DatePicker endPicker;
    @FXML protected Spinner<Integer> startHourSpinner;
    @FXML protected Spinner<Integer> startMinuteSpinner;
    @FXML protected Spinner<String> startMeridiemSpinner;
    @FXML protected Spinner<Integer> endHourSpinner;
    @FXML protected Spinner<Integer> endMinuteSpinner;
    @FXML protected Spinner<String> endMeridiemSpinner;
    @FXML protected ChoiceBox<String> customerChoiceBox;
    @FXML protected ChoiceBox<String> userChoiceBox;

    @FXML protected Button cancelBtn;
    @FXML protected Button submitBtn;

    @FXML protected Label titleFlag;
    @FXML protected Label descriptionFlag;
    @FXML protected Label locationFlag;
    @FXML protected Label contactFlag;
    @FXML protected Label typeFlag;
    @FXML protected Label startDateFlag;
    @FXML protected Label endDateFlag;
    @FXML protected Label startTimeFlag;
    @FXML protected Label endTimeFlag;
    @FXML protected Label customerFlag;
    @FXML protected Label userFlag;
    @FXML protected Label warningLabel;

    protected AppointmentService appointmentService;
    protected ContactService contactService;
    protected CustomerService customerService;
    protected UserService userService;

    protected Timestamp startTimestamp;
    protected Timestamp endTimestamp;
    protected int contactId;
    protected int customerId;
    protected int userId;

    /**
     * Called to initialize a controller after its root element has been completely processed. Initializes class
     * members, set up {@link ChoiceBox dropdowns}, set up time {@link Spinner spinners}, and event handlers.
     *
     * @param location  The location used to resolve relative paths for the root object, or
     *                  {@code null} if the location is not known.
     * @param resources The resources used to localize the root object, or {@code null} if the root object was not
     *                  localized.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialize members
        businessHoursExceptionOverride = false;
        overlapExceptionOverride = false;
        appointmentService = AppointmentService.getInstance();
        contactService = ContactService.getInstance();
        customerService = CustomerService.getInstance();
        userService = UserService.getInstance();
        ObservableList<String> contactNames = FXCollections.observableArrayList(contactService.getContactNames());
        ObservableList<String> customerNames = FXCollections.observableArrayList(customerService.getCustomerNames());
        ObservableList<String> usernames = FXCollections.observableArrayList(userService.getUsernames());

        contactNames.forEach(name -> contactChoiceBox.getItems().add(name));
        customerNames.forEach(name -> customerChoiceBox.getItems().add(name));
        usernames.forEach(name -> userChoiceBox.getItems().add(name));

        // Generate spinner factories
        var startHourFactory = Initializer.generateHourSpinnerFactory();
        var startMinuteFactory = Initializer.generateMinuteSpinnerFactory();
        var startTypeFactory = Initializer.generateMeridiemSpinnerFactory();
        var endHourFactory = Initializer.generateHourSpinnerFactory();
        var endMinuteFactory = Initializer.generateMinuteSpinnerFactory();
        var endTypeFactory = Initializer.generateMeridiemSpinnerFactory();

        // Bind factories to time spinners
        startHourSpinner.setValueFactory(startHourFactory);
        startMinuteSpinner.setValueFactory(startMinuteFactory);
        startMeridiemSpinner.setValueFactory(startTypeFactory);
        endHourSpinner.setValueFactory(endHourFactory);
        endMinuteSpinner.setValueFactory(endMinuteFactory);
        endMeridiemSpinner.setValueFactory(endTypeFactory);

        // Event handlers
        submitBtn.setOnAction(this::handleSubmitBtn);
        cancelBtn.setOnAction(e -> Navigator.goToHome());
    }

    /**
     * Sets up all the common {@link #submitBtn submit button} functionality between child implementations of
     * {@link #handleSubmitBtn(ActionEvent)}. Checks that the required fields are completed, handles and formats the
     * date and time inputs into usable data for {@link ZonedDateTime} and {@link Timestamp} storage, and formats
     * that date/time data into correct forms for the necessary {@link ZoneId zones}.
     *
     * @throws RequiredFieldException if required fields are not completed
     * @throws AppointmentNotBusinessHoursException if appointment does not fall within ETC business hours
     * @throws AppointmentOverlapException if appointment time overlaps with appointment of the same customer
     */
    protected void setupSubmit() throws RequiredFieldException, AppointmentNotBusinessHoursException,
            AppointmentOverlapException, EndBeforeStartException {

        resetFlags();

        if (!checkForNoEmptyRequiredFields())
            throw new RequiredFieldException(ErrorMessageStore.REQUIRED_FIELDS);

        contactId = contactService.getIdByName(contactChoiceBox.getValue());
        customerId = customerService.getIdByName(customerChoiceBox.getValue());
        userId = userService.getIdByUsername(userChoiceBox.getValue());

        ZonedDateTime startZDT = setupSystemDefaultZDT(startHourSpinner, startMinuteSpinner, startMeridiemSpinner,
                startPicker);
        ZonedDateTime endZDT = setupSystemDefaultZDT(endHourSpinner, endMinuteSpinner, endMeridiemSpinner, endPicker);

        if (endZDT.isBefore(startZDT)) {
            flagDateTimeFields();
            throw new EndBeforeStartException(ErrorMessageStore.APPOINTMENT_END_BEFORE_START);
        }
        if (!businessHoursExceptionOverride && !(appointmentService.checkForESTBusinessHours(startZDT) &&
                appointmentService.checkForESTBusinessHours(endZDT))) {
            flagDateTimeFields();
            throw new AppointmentNotBusinessHoursException(ErrorMessageStore.APPOINTMENT_HOURS_INVALID);
        }
        if (!overlapExceptionOverride &&
                !appointmentService.checkForNoOverlapByCustomer(startZDT, endZDT, customerId)) {
            flagDateTimeFields();
            throw new AppointmentOverlapException(ErrorMessageStore.APPOINTMENT_OVERLAP);
        }

        // Set up UTC Timestamp members
        ZonedDateTime startUTC = startZDT.withZoneSameInstant(ZoneId.of("UTC"));
        ZonedDateTime endUTC = endZDT.withZoneSameInstant(ZoneId.of("UTC"));

        startTimestamp = Timestamp.valueOf(startUTC.toLocalDateTime());
        endTimestamp = Timestamp.valueOf(endUTC.toLocalDateTime());
    }

    protected abstract void handleSubmitBtn(ActionEvent event);

    /**
     * Resets all field flags to not be visible.
     */
    private void resetFlags(){
        titleFlag.setVisible(false);
        descriptionFlag.setVisible(false);
        locationFlag.setVisible(false);
        contactFlag.setVisible(false);
        typeFlag.setVisible(false);
        startDateFlag.setVisible(false);
        startTimeFlag.setVisible(false);
        endDateFlag.setVisible(false);
        endTimeFlag.setVisible(false);
        customerFlag.setVisible(false);
        userFlag.setVisible(false);
        warningLabel.setVisible(false);
    }

    /**
     * Checks whether all required form fields are completed. Flags uncompleted fields and provides an error message
     * if exception is thrown.
     */
    private boolean checkForNoEmptyRequiredFields() {
        boolean reqFieldsNotEmpty = true;

        if (Objects.equals(titleField.getText(), "")) {
            titleFlag.setVisible(true);
            reqFieldsNotEmpty = false;
        }
        if (Objects.equals(descriptionField.getText(), "")) {
            descriptionFlag.setVisible(true);
            reqFieldsNotEmpty = false;
        }
        if (Objects.equals(locationField.getText(), "")) {
            locationFlag.setVisible(true);
            reqFieldsNotEmpty = false;
        }
        if (Objects.equals(contactChoiceBox.getValue(), "")) {
            contactFlag.setVisible(true);
            reqFieldsNotEmpty = false;
        }
        if (Objects.equals(typeField.getText(), "")) {
            typeFlag.setVisible(true);
            reqFieldsNotEmpty = false;
        }
        if (Objects.isNull(startPicker.getValue())) {
            startDateFlag.setVisible(true);
            reqFieldsNotEmpty = false;
        }
        if (Objects.isNull(endPicker.getValue())) {
            endDateFlag.setVisible(true);
            reqFieldsNotEmpty = false;
        }
        if (Objects.equals(customerChoiceBox.getValue(), "")) {
            customerFlag.setVisible(true);
            reqFieldsNotEmpty = false;
        }
        if (Objects.equals(userChoiceBox.getValue(), "")) {
            userFlag.setVisible(true);
            reqFieldsNotEmpty = false;
        }
        return reqFieldsNotEmpty;
    }

    /**
     * Sets up a {@link ZonedDateTime} from the date and time form input fields, zoned to {@link ZoneId#systemDefault()}
     * .
     *
     * @param hourSpinner {@link Spinner} with value of hour in standard (12 Hour) format
     * @param minuteSpinner {@link Spinner} with value of minute
     * @param meridiemSpinner {@link Spinner} with value of meridiem (AM/PM)
     * @param picker {@link DatePicker} of selected date
     * @return newly created {@link ZonedDateTime} from user input data
     */
    private ZonedDateTime setupSystemDefaultZDT(Spinner<Integer> hourSpinner, Spinner<Integer> minuteSpinner,
                                               Spinner<String> meridiemSpinner, DatePicker picker) {
        int hour = Formatter.toMilitaryHour(hourSpinner.getValue(), TimeMeridiem.valueOf(meridiemSpinner.getValue()));
        LocalDate date = picker.getValue();
        LocalTime time = LocalTime.of(hour, minuteSpinner.getValue());
        LocalDateTime dateTime = LocalDateTime.of(date, time);
        return ZonedDateTime.of(dateTime, ZoneId.systemDefault());
    }

    /**
     * Flags all date and time fields
     */
    private void flagDateTimeFields() {
        startDateFlag.setVisible(true);
        startTimeFlag.setVisible(true);
        endDateFlag.setVisible(true);
        endTimeFlag.setVisible(true);
    }
}
