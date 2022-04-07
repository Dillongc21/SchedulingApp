package com.scheduler.client.controller;

import com.scheduler.business.CustomerScheduleReportService;
import com.scheduler.business.CustomerService;
import com.scheduler.client.util.Navigator;
import com.scheduler.common.model.CustomerScheduleReportItem;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class CustomerScheduleReportController implements Initializable {

    @FXML private TreeTableView<CustomerScheduleReportItem> table;
    @FXML private TreeTableColumn<CustomerScheduleReportItem, String> nameCol;
    @FXML private TreeTableColumn<CustomerScheduleReportItem, Integer> apptIdCol;
    @FXML private TreeTableColumn<CustomerScheduleReportItem, String> titleCol;
    @FXML private TreeTableColumn<CustomerScheduleReportItem, String> typeCol;
    @FXML private TreeTableColumn<CustomerScheduleReportItem, String> descriptionCol;
    @FXML private TreeTableColumn<CustomerScheduleReportItem, String> startCol;
    @FXML private TreeTableColumn<CustomerScheduleReportItem, String> endCol;
    @FXML private TreeTableColumn<CustomerScheduleReportItem, Integer> foreignIdCol;
    @FXML private Button closeBtn;

    private List<CustomerScheduleReportItem> items;
    private List<String> customers;
    private TreeItem<CustomerScheduleReportItem> root;

    /**
     * Called to initialize a controller after its root element has been
     * completely processed.
     *
     * @param location  The location used to resolve relative paths for the root object, or
     *                  {@code null} if the location is not known.
     * @param resources The resources used to localize the root object, or {@code null} if
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        CustomerScheduleReportService service = new CustomerScheduleReportService();
        CustomerService customerService = CustomerService.getInstance();
        items = service.getItems();
        customers = customerService.getCustomerNames();

        createAssignmentTree();

        nameCol.setText("Customer Name");
        foreignIdCol.setText("Contact ID");

        nameCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getValue().getCustomerName()));
        apptIdCol.setCellValueFactory(cellData ->
                new SimpleObjectProperty<>(cellData.getValue().getValue().getAppointmentId()));
        titleCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getValue().getTitle()));
        typeCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getValue().getType()));
        descriptionCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getValue().getDescription()));
        startCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getValue().getStart()));
        endCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getValue().getEnd()));
        foreignIdCol.setCellValueFactory(cellData ->
                new SimpleObjectProperty<>(cellData.getValue().getValue().getContactId()));

        table.setRoot(root);

        closeBtn.setOnAction(Navigator::closeWindow);
    }

    private void createAssignmentTree() {
        root = new TreeItem<>(
                new CustomerScheduleReportItem("All Items", null, null, null, null,
                        null, null, null));
        root.setExpanded(true);
        customers.forEach(customer -> {
                    TreeItem<CustomerScheduleReportItem> customerTitleItem = new TreeItem<>(
                            new CustomerScheduleReportItem(customer, null, null, null, null,
                                    null, null, null));
                    root.getChildren().add(customerTitleItem);
                    customerTitleItem.setExpanded(true);
                    items.stream()
                            .filter(item -> customer.equals(item.getCustomerName()))
                            .forEach(item -> {
                                TreeItem<CustomerScheduleReportItem> lineItem = new TreeItem<>(
                                        new CustomerScheduleReportItem(null, item.getAppointmentId(), item.getTitle(),
                                                item.getType(), item.getDescription(), item.getStart(), item.getEnd(),
                                                item.getContactId()));
                                customerTitleItem.getChildren().add(lineItem);
                            });
                });
    }

}
