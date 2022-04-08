package com.scheduler.client.controller;

import com.scheduler.client.util.Navigator;
import com.scheduler.common.exception.RequiredFieldException;
import com.scheduler.common.model.Customer;

import javafx.event.ActionEvent;

import java.util.Objects;

/**
 * Controller class for the {@link Customer} adding UI. The view file that this class controls is
 * <code>CustomerForm.fxml</code>.
 *
 * @author Dillon Christensen
 */
public class AddCustomerController extends CustomerController {

    /**
     * Handles submit button being clicked. Performs checks on form data, preps the data for submission, and attempts
     * the {@link Customer} creation submission then, adds the result of the attempted submission if not
     * <code>null</code>.
     * <p>
     * Displays error message if {@link RequiredFieldException} is thrown
     * @param event {@link ActionEvent} of the button click
     */
    @Override
    protected void handleSubmitBtn(ActionEvent event) {
        try {
            checkRequiredFields();

            Customer customer = new Customer(nameField.getText(), addressField.getText(), postalField.getText(),
                    phoneField.getText(), customerDivision.getId());
            customer = customerService.createCustomer(customer);

            if (!Objects.isNull(customer))
                HomeController.customers.add(customer);

            Navigator.goToHome();
        } catch (RequiredFieldException ex) {
            warningLabel.setVisible(true);
            warningLabel.setText(ex.getMessage());
        }
    }
}
