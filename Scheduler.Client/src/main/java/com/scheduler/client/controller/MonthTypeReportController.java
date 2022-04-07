package com.scheduler.client.controller;

import com.scheduler.business.MonthTypeReportService;
import com.scheduler.client.util.Navigator;
import com.scheduler.common.model.MonthTypeReportItem;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;

import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

public class MonthTypeReportController implements Initializable {

    @FXML private TreeTableView<MonthTypeReportItem> treeTable;
    @FXML private TreeTableColumn<MonthTypeReportItem, String> monthCol;
    @FXML private TreeTableColumn<MonthTypeReportItem, String> typeCol;
    @FXML private TreeTableColumn<MonthTypeReportItem, Integer> totalCol;
    @FXML private Button closeBtn;

    private List<MonthTypeReportItem> items;
    private Set<String> months;
    private TreeItem<MonthTypeReportItem> root;

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
        MonthTypeReportService service = new MonthTypeReportService();
        items = service.getItems();
        months = new HashSet<>(items.stream()
                .map(MonthTypeReportItem::getMonth)
                .toList());

        createAssignmentTree();

        monthCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getValue().getMonth()));
        typeCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getValue().getType()));
        totalCol.setCellValueFactory(cellData ->
                new SimpleObjectProperty<>(cellData.getValue().getValue().getTotal()));

        treeTable.setRoot(root);

        closeBtn.setOnAction(Navigator::closeWindow);
    }

    private void createAssignmentTree() {
        root = new TreeItem<>(
                new MonthTypeReportItem("All Items", null, null));
        root.setExpanded(true);
        months.forEach(month -> {
                    int monthTotal = items.stream()
                            .filter(item -> month.equals(item.getMonth()))
                            .mapToInt(MonthTypeReportItem::getTotal).sum();
                    TreeItem<MonthTypeReportItem> monthTitleItem = new TreeItem<>(
                            new MonthTypeReportItem(month, null, monthTotal));
                    root.getChildren().add(monthTitleItem);
                    monthTitleItem.setExpanded(true);
                    items.stream()
                            .filter(item -> month.equals(item.getMonth()))
                            .forEach(item -> {
                                TreeItem<MonthTypeReportItem> lineItem = new TreeItem<>(
                                        new MonthTypeReportItem(null, item.getType(), item.getTotal()));
                                monthTitleItem.getChildren().add(lineItem);
                            });
                });
    }

}
