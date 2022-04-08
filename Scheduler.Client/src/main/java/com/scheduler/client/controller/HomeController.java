package com.scheduler.client.controller;

import com.scheduler.business.AppointmentService;
import com.scheduler.business.CountryService;
import com.scheduler.business.CustomerService;
import com.scheduler.client.util.*;
import com.scheduler.client.util.Navigator;
import com.scheduler.client.util.SelectedTab;
import com.scheduler.common.exception.AppointmentDeletionFailedException;
import com.scheduler.common.exception.NoAppointmentSelectedException;
import com.scheduler.common.exception.NoCustomerSelectedException;
import com.scheduler.common.model.Appointment;
import com.scheduler.common.model.Customer;
import com.scheduler.common.util.AppointmentTimeSpan;
import com.scheduler.common.util.Formatter;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import org.javatuples.Pair;

import java.net.URL;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * Controller class for the Home screen. The view file that this class controls is <code>Home.fxml</code>.
 *
 * @author Dillon Christensen
 */
public class HomeController implements Initializable {

    @FXML private TableView<Customer> custTable;
    @FXML private TableView<Appointment> appTable;

    @FXML private TableColumn<Customer, String> custNameCol;
    @FXML private TableColumn<Customer, String> custAddressCol;
    @FXML private TableColumn<Customer, String> custPostalCol;
    @FXML private TableColumn<Customer, String> custPhoneCol;

    @FXML private TableColumn<Appointment, Integer> appIdCol;
    @FXML private TableColumn<Appointment, String> appTitleCol;
    @FXML private TableColumn<Appointment, String> appDescCol;
    @FXML private TableColumn<Appointment, String> appLocationCol;
    @FXML private TableColumn<Appointment, String> appContactCol;
    @FXML private TableColumn<Appointment, String> appTypeCol;
    @FXML private TableColumn<Appointment, ZonedDateTime> appStartCol;
    @FXML private TableColumn<Appointment, ZonedDateTime> appEndCol;
    @FXML private TableColumn<Appointment, Integer> appCustIdCol;
    @FXML private TableColumn<Appointment, Integer> appUserIdCol;

    @FXML private ToggleGroup appRadio;
    @FXML private RadioButton appRadioAll;
    @FXML private RadioButton appRadioMonth;
    @FXML private RadioButton appRadioWeek;

    @FXML private ChoiceBox<String> custCountryChoiceBox;

    @FXML private Button custAddBtn;
    @FXML private Button custDeleteBtn;
    @FXML private Button custModifyBtn;
    @FXML private Label custWarningLabel;

    @FXML private Button appAddBtn;
    @FXML private Button appDeleteBtn;
    @FXML private Button appModifyBtn;
    @FXML private Label appWarningLabel;

    @FXML private Button reportBtn;
    @FXML private ChoiceBox<String> reportChoiceBox;

    @FXML private TabPane applicationTabs;
    @FXML private Tab customersTab;
    @FXML private Tab appointmentsTab;

    static SelectedTab selectedTab;
    static boolean notifiedOfUpcoming = false;
    static ObservableList<Customer> customers;
    static ObservableList<Appointment> appointments;
    private AppointmentService appointmentService;
    private CustomerService customerService;

    /**
     * Called to initialize a controller after its root element has been completely processed. Initializes class
     * members, sets up event handlers for {@link Button buttons} and {@link ChoiceBox dropdowns}, binds and formats
     * table cells, and sets up report generation and tab functionality.
     *
     * @param location  The location used to resolve relative paths for the root object, or
     *                  {@code null} if the location is not known.
     * @param resources The resources used to localize the root object, or {@code null} if the root object was not
     *                  localized.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialize class members
        customerService = CustomerService.getInstance();
        CountryService countryService = CountryService.getInstance();
        appointmentService = AppointmentService.getInstance();
        customers = FXCollections.observableArrayList(customerService.getAllCustomers());
        appointments = FXCollections.observableArrayList(appointmentService.getAllAppointments());
        var countryNames = FXCollections.observableArrayList(countryService.getCountryNames());

        // Event handlers
        custCountryChoiceBox.setOnAction(this::handleCustomerCountryCB);
        custAddBtn.setOnAction(e -> Navigator.goToAddCustomer());
        custModifyBtn.setOnAction(this::handleCustomerModifyBtn);
        custDeleteBtn.setOnAction(this::handleCustomerDeleteBtn);

        // Bind Customer Table columns to Customer model members
        custNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        custAddressCol.setCellValueFactory(new PropertyValueFactory<>("extendedAddress"));
        custPostalCol.setCellValueFactory(new PropertyValueFactory<>("postalCode"));
        custPhoneCol.setCellValueFactory(new PropertyValueFactory<>("phone"));

        custTable.setItems(customers);

        // Set up Customer table Country dropdown filter
        countryNames.add(0, "-- All --");
        countryNames.forEach(name -> custCountryChoiceBox.getItems().add(name));
        custCountryChoiceBox.setValue(countryNames.get(0));

        // Appointment event handlers
        appAddBtn.setOnAction(e -> Navigator.goToAddAppointment());
        appDeleteBtn.setOnAction(this::handleAppointmentDeleteBtn);
        appModifyBtn.setOnAction(this::handleAppointmentModifyBtn);
        appRadio.selectedToggleProperty().addListener(appRadioListener);
        appRadioAll.setSelected(true);

        // Bind Appointment Table columns to Appointment model members
        appIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        appTitleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        appDescCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        appLocationCol.setCellValueFactory(new PropertyValueFactory<>("location"));
        appContactCol.setCellValueFactory(new PropertyValueFactory<>("contactName"));
        appTypeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        appStartCol.setCellValueFactory(new PropertyValueFactory<>("startDefault"));

        appStartCol.setCellFactory(column -> formatForZDTCell());
        appEndCol.setCellValueFactory(new PropertyValueFactory<>("endDefault"));
        appEndCol.setCellFactory(column -> formatForZDTCell());
        appCustIdCol.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        appUserIdCol.setCellValueFactory(new PropertyValueFactory<>("userId"));

        // Populate Appointment table
        appTable.setItems(appointments);

        // Set up Tab Pane
        applicationTabs.getSelectionModel().selectedItemProperty().addListener(tabChangeListener);
        openSelectedTab();

        reportChoiceBox.getItems().add("Month/Type Totals");
        reportChoiceBox.getItems().add("Contact Schedules");
        reportChoiceBox.getItems().add("Customer Schedules");
        reportChoiceBox.setValue(reportChoiceBox.getItems().get(0));
        reportBtn.setOnAction(this::handleReportBtn);

        if (!notifiedOfUpcoming)
            popUpAny15MinuteUpcomingAppointments();
    }

    /**
     * Handles button to generate reports. Depends on value from {@link #reportChoiceBox} to generate the correct
     * report.
     * Lambda Justification 1: Lambdas are used here for <code>switch</code> statement syntax. This syntax simplifies
     * the statements, making them more human-readable and eliminates the need for <code>break</code> statements.
     *
     * @param event button click event
     */
    private void handleReportBtn(ActionEvent event) {
        String selected = reportChoiceBox.getSelectionModel().getSelectedItem();

        switch (selected) {
            case "Month/Type Totals" -> Navigator.popUpMonthTypeReport();
            case "Contact Schedules" -> Navigator.popUpContactScheduleReport();
            case "Customer Schedules" -> Navigator.popUpCustomerScheduleReport();
        }
    }

    /**
     * Pops up a prompt for any upcoming appointments, within the next 15 minutes. Only triggers on login.
     */
    private void popUpAny15MinuteUpcomingAppointments() {
        appointments.forEach(a -> {
            if (a.getStartDefault().isAfter(ZonedDateTime.now()) &&
                    a.getStartDefault().isBefore(ZonedDateTime.now().plusMinutes(15))) {
                UpcomingAppointmentPromptController.notificationAppointment = a;
                Navigator.popUpUpcomingAppointmentPrompt();
            }
        });
        notifiedOfUpcoming = true;
    }

    /**
     * Handler for the Country ChoiceBox on the "Customer" application tab. Filters the "customers" table
     * by country or shows all customers, based on the selection.
     *
     * @param event ActionEvent that triggered method
     */
    public void handleCustomerCountryCB(ActionEvent event) {
        String countryNameFilter = custCountryChoiceBox.getValue();

        if (Objects.equals(countryNameFilter, "-- All --")) {
            custTable.setItems(customers);
        } else {
            var filtered = customerService.getCustomersByCountryName(countryNameFilter);
            var filteredObservable = FXCollections.observableArrayList(filtered);
            custTable.setItems(filteredObservable);
        }
    }

    /**
     * Handler for the Delete Button on the "Customers" application tab. Deletes any of the customer's
     * appointments before deleting the customer, due to foreign key constraints. Warns before deletion.
     * If any of the associated appointments were not successfully deleted, the customer deletion is
     * aborted.
     *
     * @param event ActionEvent that triggered method
     */
    public void handleCustomerDeleteBtn(ActionEvent event) {
        Navigator.popUpCustomerDeletePrompt();
        if (DeleteCustomerPromptController.deleteConfirmed) {
            try {
                int id = custTable.getSelectionModel().getSelectedItem().getId();

                Pair<List<Appointment>, List<Appointment>> allDeletedAndNotDeleted
                        = appointmentService.deleteAppointmentsByCustomerId(id);

                List<Appointment> allDeleted = allDeletedAndNotDeleted.getValue0();
                List<Appointment> allNotDeleted = allDeletedAndNotDeleted.getValue1();

                allDeleted.forEach(a -> appointments.remove(a));
                if (!allNotDeleted.isEmpty())
                    throw new AppointmentDeletionFailedException(ErrorMessageStore.APPOINTMENTS_NOT_DELETED);

                Customer toDelete = customerService.deleteCustomer(id);
                customers.remove(toDelete);
            } catch (AppointmentDeletionFailedException ex) {
                custWarningLabel.setVisible(true);
                custWarningLabel.setText(ex.getMessage());
            }
        }
    }

    public void handleAppointmentDeleteBtn(ActionEvent event) {
        Navigator.popUpAppointmentDeletePrompt();
        if (DeleteAppointmentPromptController.deleteConfirmed) {
            int id = appTable.getSelectionModel().getSelectedItem().getId();
            Appointment toDelete = appointmentService.deleteAppointment(id);
            appointments.remove(toDelete);
        }
    }

    /**
     * Handles 'Modify' button on the 'Customers' tab. Opens form to modify the currently selected customer. If no
     * customer is selected, a {@link NoCustomerSelectedException} is thrown, and caught with an error message
     * displayed in the UI.
     *
     * @param event button click event
     */
    public void handleCustomerModifyBtn(ActionEvent event) {
        try {
            ModifyCustomerController.customerToModify = custTable.getSelectionModel().getSelectedItem();
            if (ModifyCustomerController.customerToModify == null)
                throw new NoCustomerSelectedException(ErrorMessageStore.NO_CUSTOMER_SELECTED);
            Navigator.goToModifyCustomer();
        } catch (NoCustomerSelectedException ex) {
            custWarningLabel.setVisible(true);
            custWarningLabel.setText(ex.getMessage());
        }
    }

    /**
     * Handles 'Modify' button on the 'Appointments' tab. Opens form to modify the currently selected appointment. If no
     * appointment is selected, a {@link NoAppointmentSelectedException} is thrown, and caught with an error message
     * displayed in the UI.
     *
     * @param event button click event
     */
    public void handleAppointmentModifyBtn(ActionEvent event) {
        try {
            ModifyAppointmentController.appointmentToModify = appTable.getSelectionModel().getSelectedItem();
            if (ModifyAppointmentController.appointmentToModify == null)
                throw new NoAppointmentSelectedException(ErrorMessageStore.NO_APPOINTMENT_SELECTED);
            Navigator.goToModifyAppointment();
        } catch (NoAppointmentSelectedException ex) {
            appWarningLabel.setVisible(true);
            appWarningLabel.setText(ex.getMessage());
        }
    }

    /**
     * Opens selected tab. Tab is remembered, i.e. when navigating to a different screen, the last tab opened is opened
     * again when returning to the Home Screen.
     */
    private void openSelectedTab() {
        if (selectedTab == SelectedTab.APPOINTMENTS)
            applicationTabs.getSelectionModel().select(appointmentsTab);
        else
            applicationTabs.getSelectionModel().select(customersTab);
    }

    /**
     * Formats cell to display a {@link ZonedDateTime} in a more human-readable form. {@link Formatter} is used to
     * generate the formatted string.
     *
     * @return formatted {@link TableCell}
     */
    private TableCell<Appointment, ZonedDateTime> formatForZDTCell() {
        return new TableCell<>() {
            @Override
            public void updateItem(ZonedDateTime item, boolean empty) {
                super.updateItem(item, empty);
                if (empty)
                    setText(null);
                else
                    setText(Formatter.toDisplay(item));
            }
        };
    }

    /**
     * Listener for {@link Tab} change. The newly selected Tab is stored statically to be reopened, if navigated away
     * and back to Home.
     */
    private final ChangeListener<Tab> tabChangeListener = new ChangeListener<>() {
        @Override
        public void changed(ObservableValue<? extends Tab> observable, Tab oldValue, Tab newValue) {
            selectedTab = (newValue == appointmentsTab) ? SelectedTab.APPOINTMENTS : SelectedTab.CUSTOMERS;
        }
    };

    /**
     * Listener for toggling radio buttons. When toggled, the {@link Appointment} table is filtered according to
     * {@link AppointmentTimeSpan}.
     */
    private final ChangeListener<Toggle> appRadioListener = new ChangeListener<>() {
        @Override
        public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
            if (appRadio.getSelectedToggle() != null) {
                RadioButton selected = (RadioButton) appRadio.getSelectedToggle();
                ObservableList<Appointment> filteredAppointments;
                switch (selected.getText()) {
                    case "Month" -> {
                        filteredAppointments = FXCollections.observableArrayList(
                                appointmentService.getAppointmentsByTimeSpan(AppointmentTimeSpan.MONTH));
                        appTable.setItems(filteredAppointments);
                    }
                    case "Week" -> {
                        filteredAppointments = FXCollections.observableArrayList(
                                appointmentService.getAppointmentsByTimeSpan(AppointmentTimeSpan.WEEK));
                        appTable.setItems(filteredAppointments);
                    }
                    default -> appTable.setItems(appointments);
                }
            }
        }
    };
}
