package com.scheduler.client.controller;

import com.scheduler.client.util.Navigator;
import com.scheduler.common.exception.RequiredFieldException;
import com.scheduler.common.model.Country;
import com.scheduler.common.model.Customer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * Controller class for the FXML form for modifying a {@link Customer}. The view file that this class controls is
 * <code>CustomerForm.fxml</code>.
 *
 * @author Dillon Christensen
 */
public class ModifyCustomerController extends CustomerController {

    @FXML private TextField idField;
    @FXML private DatePicker createdPicker;
    @FXML private TextField createdByField;
    @FXML private DatePicker updatedPicker;
    @FXML private TextField updatedByField;

    static Customer customerToModify;

    /**
     * Called to initialize a controller after its root element has been completely processed. Calls parent method
     * {@link CustomerController#initialize(URL, ResourceBundle)}. Initializes some class members, then fills in form
     * data with {@link #customerToModify} instance.
     *
     * @param location  The location used to resolve relative paths for the root object, or
     *                  {@code null} if the location is not known.
     * @param resources The resources used to localize the root object, or {@code null} if the root obect was not
     *                  localized.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);

        // Initialize private members
        customerDivision = divisionService.getDivisionById(customerToModify.getDivisionId());
        Country customerCountry = countryService.getCountryById(customerDivision.getCountryId());

        // Populate input fields / set up Division Dropdown
        idField.setText(Integer.toString(customerToModify.getId()));
        nameField.setText(customerToModify.getName());
        addressField.setText(customerToModify.getAddress());
        postalField.setText(customerToModify.getPostalCode());
        phoneField.setText(customerToModify.getPhone());
        countryChoiceBox.setValue(customerCountry.getName());
        handleCountryChoiceBox(new ActionEvent());
        divisionChoiceBox.setValue(customerDivision.getName());
        createdPicker.setValue(customerToModify.getCreateDate()
                .toLocalDateTime().toLocalDate());
        createdByField.setText(customerToModify.getCreatedBy());
        updatedPicker.setValue(customerToModify.getLastUpdate()
                .toLocalDateTime().toLocalDate());
        updatedByField.setText(customerToModify.getLastUpdatedBy());
    }

    /**
     * Handles submit button being clicked. Performs checks on form data, preps the data for submission, and attempts
     * the {@link Customer} update submission then, replaces the result of the attempted submission in
     * {@link HomeController#customers}, if the result is not <code>null</code>.
     * <p>
     * Displays error message if {@link RequiredFieldException} is thrown
     * @param event {@link ActionEvent} of the button click
     */
    @Override
    protected void handleSubmitBtn(ActionEvent event) {
        try {
            checkRequiredFields();

            Customer formCustomer = new Customer(customerToModify.getId(), nameField.getText(), addressField.getText(),
                    postalField.getText(), phoneField.getText(), customerToModify.getCreateDate(),
                    customerToModify.getCreatedBy(), customerToModify.getLastUpdate(),
                    customerToModify.getLastUpdatedBy(), customerDivision.getId());
            Customer updated = customerService.updateCustomer(formCustomer);

            if (!Objects.isNull(updated)) {
                Customer defunct = customerService.getCustomerById(updated.getId());
                HomeController.customers.remove(defunct);
                HomeController.customers.add(updated);
            }
            Navigator.goToHome();
        } catch (RequiredFieldException ex) {
            warningLabel.setVisible(true);
            warningLabel.setText(ex.getMessage());
        }
    }
}
