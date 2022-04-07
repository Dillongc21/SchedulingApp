package com.scheduler.client.controller;

import com.scheduler.business.ContactScheduleReportService;
import com.scheduler.business.ContactService;
import com.scheduler.client.util.Navigator;
import com.scheduler.common.model.ContactScheduleReportItem;

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

public class ContactScheduleReportController implements Initializable {

    @FXML private TreeTableView<ContactScheduleReportItem> table;
    @FXML private TreeTableColumn<ContactScheduleReportItem, String> nameCol;
    @FXML private TreeTableColumn<ContactScheduleReportItem, Integer> apptIdCol;
    @FXML private TreeTableColumn<ContactScheduleReportItem, String> titleCol;
    @FXML private TreeTableColumn<ContactScheduleReportItem, String> typeCol;
    @FXML private TreeTableColumn<ContactScheduleReportItem, String> descriptionCol;
    @FXML private TreeTableColumn<ContactScheduleReportItem, String> startCol;
    @FXML private TreeTableColumn<ContactScheduleReportItem, String> endCol;
    @FXML private TreeTableColumn<ContactScheduleReportItem, Integer> foreignIdCol;
    @FXML private Button closeBtn;

    private List<ContactScheduleReportItem> items;
    private List<String> contacts;
    private TreeItem<ContactScheduleReportItem> root;

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
        ContactScheduleReportService service = new ContactScheduleReportService();
        ContactService contactService = ContactService.getInstance();
        items = service.getItems();
        contacts = contactService.getContactNames();

        createAssignmentTree();

        nameCol.setText("Contact Name");
        foreignIdCol.setText("Cust. ID");

        nameCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getValue().getContactName()));
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
                new SimpleObjectProperty<>(cellData.getValue().getValue().getCustomerId()));

        table.setRoot(root);

        closeBtn.setOnAction(Navigator::closeWindow);
    }

    private void createAssignmentTree() {
        root = new TreeItem<>(
                new ContactScheduleReportItem("All Items", null, null, null, null,
                        null, null, null));
        root.setExpanded(true);
        contacts.forEach(contact -> {
                    TreeItem<ContactScheduleReportItem> contactTitleItem = new TreeItem<>(
                            new ContactScheduleReportItem(contact, null, null, null, null,
                                    null, null, null));
                    root.getChildren().add(contactTitleItem);
                    contactTitleItem.setExpanded(true);
                    items.stream()
                            .filter(item -> contact.equals(item.getContactName()))
                            .forEach(item -> {
                                TreeItem<ContactScheduleReportItem> lineItem = new TreeItem<>(
                                        new ContactScheduleReportItem(null, item.getAppointmentId(), item.getTitle(),
                                                item.getType(), item.getDescription(), item.getStart(), item.getEnd(),
                                                item.getCustomerId()));
                                contactTitleItem.getChildren().add(lineItem);
                            });
                });
    }

}
