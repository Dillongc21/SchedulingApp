package com.scheduler.client.controller;

import com.scheduler.client.util.PromptMessageStore;

import java.net.URL;
import java.util.ResourceBundle;

public class DeleteCustomerPromptController extends DeletePopupController {

    /**
     * Called to initialize a controller after its root element has been completely processed. Calls parent method
     * {@link DeletePopupController#initialize(URL, ResourceBundle)}. Sets prompt message.
     *
     * @param location  The location used to resolve relative paths for the root object, or
     *                  {@code null} if the location is not known.
     * @param resources The resources used to localize the root object, or {@code null} if the root obect was not
     *                  localized.
     * @inheritDoc
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
        promptLabel.setText(PromptMessageStore.CUSTOMER_DELETE_MSG);
    }
}
