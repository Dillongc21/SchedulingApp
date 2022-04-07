package com.scheduler.client.controller;

import com.scheduler.business.CountryService;
import com.scheduler.business.CustomerService;
import com.scheduler.business.DivisionService;
import com.scheduler.client.util.ErrorMessageStore;
import com.scheduler.client.util.Navigator;
import com.scheduler.common.exception.RequiredFieldException;
import com.scheduler.common.model.Country;
import com.scheduler.common.model.Division;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * Abstract controller class that provides common functionality to (is the Parent of) {@link AddCustomerController} and
 * {@link ModifyAppointmentController}. The view file that this class controls is <code>CustomerForm.fxml</code>.
 *
 * @author Dillon Christensen
 */
public abstract class CustomerController implements Initializable {

    @FXML protected TextField nameField;
    @FXML protected TextField addressField;
    @FXML protected TextField postalField;
    @FXML protected TextField phoneField;
    @FXML protected ChoiceBox<String> countryChoiceBox;
    @FXML protected ChoiceBox<String> divisionChoiceBox;
    @FXML protected Button cancelBtn;
    @FXML protected Button submitBtn;

    @FXML protected Label nameFlag;
    @FXML protected Label addressFlag;
    @FXML protected Label postalFlag;
    @FXML protected Label phoneFlag;
    @FXML protected Label countryFlag;
    @FXML protected Label divisionFlag;
    @FXML protected Label warningLabel;

    protected ObservableList<String> divisionNames;
    protected CustomerService customerService;
    protected CountryService countryService;
    protected DivisionService divisionService;
    protected Country customerCountry;
    protected Division customerDivision;

    /**
     * Called to initialize a controller after its root element has been completely processed. Initializes class
     * members, sets up event handlers and loads values into {@link #countryChoiceBox}.
     *
     * @param location  The location used to resolve relative paths for the root object, or
     *                  {@code null} if the location is not known.
     * @param resources The resources used to localize the root object, or {@code null} if the root obect was not
     *                  localized.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialize private/local members
        countryService = CountryService.getInstance();
        divisionService = DivisionService.getInstance();
        customerService = CustomerService.getInstance();
        ObservableList<String> countryNames = FXCollections.observableArrayList(countryService.getCountryNames());
        divisionNames = FXCollections.observableArrayList();

        // Event handlers
        cancelBtn.setOnAction(e -> Navigator.goToHome());
        submitBtn.setOnAction(this::handleSubmitBtn);
        countryChoiceBox.setOnAction(this::handleCountryChoiceBox);
        divisionChoiceBox.setOnAction(this::handleDivisionChoiceBox);

        // Set up Country Dropdown
        countryNames.forEach(name -> countryChoiceBox.getItems().add(name));
    }

    /**
     * Handles {@link #countryChoiceBox} event. When a {@link Country} value is selected, the {@link #customerCountry}
     * class member is set and the selections for the {@link #divisionChoiceBox} are dynamically generated.
     *
     * @param event {@link ActionEvent} of selection click
     */
    protected void handleCountryChoiceBox(ActionEvent event) {
        if (!Objects.equals(countryChoiceBox.getValue(), "")) {
            String currCountryName = countryChoiceBox.getValue();
            if (!Objects.equals(currCountryName, "")) {
                Country country = countryService.getCountryByName(currCountryName);
                customerCountry = country;
                divisionNames.clear();
                divisionChoiceBox.getItems().clear();
                divisionNames = FXCollections.observableArrayList(divisionService.getDivisionNamesByCountry(country));
                divisionNames.forEach(name -> divisionChoiceBox.getItems().add(name));
            }
        }
    }

    /**
     * Handles {@link #divisionChoiceBox} event. When a {@link Division} value is selected, the
     * {@link  #customerDivision} class member is set.
     * @param event {@link ActionEvent} of selection click
     */
    private void handleDivisionChoiceBox(ActionEvent event) {
        String name = divisionChoiceBox.getValue();
        customerDivision = divisionService.getDivisionByName(name);
    }

    /**
     * Resets all field flags to not be visible.
     */
    private void resetFieldFlags() {
        nameFlag.setVisible(false);
        addressFlag.setVisible(false);
        postalFlag.setVisible(false);
        phoneFlag.setVisible(false);
        countryFlag.setVisible(false);
        divisionFlag.setVisible(false);
    }

    /**
     * Checks whether all required form fields are completed. Flags uncompleted fields and provides an error message
     * if exception is thrown.
     * @throws RequiredFieldException if required fields not completed
     */
    protected void checkRequiredFields() throws RequiredFieldException {
        boolean reqFieldsEmpty = false;

        resetFieldFlags();
        warningLabel.setVisible(false);

        if (Objects.equals(nameField.getText(), "")) {
            nameFlag.setVisible(true);
            reqFieldsEmpty = true;
        }
        if (Objects.equals(addressField.getText(), "")) {
            addressFlag.setVisible(true);
            reqFieldsEmpty = true;
        }
        if (Objects.equals(postalField.getText(), "")) {
            postalFlag.setVisible(true);
            reqFieldsEmpty = true;
        }
        if (Objects.equals(phoneField.getText(), "")) {
            phoneFlag.setVisible(true);
            reqFieldsEmpty = true;
        }
        if (Objects.isNull(customerCountry)) {
            countryFlag.setVisible(true);
            reqFieldsEmpty = true;
        }
        if (Objects.isNull(customerDivision)) {
            divisionFlag.setVisible(true);
            reqFieldsEmpty = true;
        }
        if (reqFieldsEmpty)
            throw new RequiredFieldException(ErrorMessageStore.REQUIRED_FIELDS);
    }

    protected abstract void handleSubmitBtn(ActionEvent event);
}
