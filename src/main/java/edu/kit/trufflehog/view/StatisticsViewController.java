package edu.kit.trufflehog.view;

import edu.kit.trufflehog.Main;
import edu.kit.trufflehog.service.packetdataprocessor.IPacketData;
import edu.kit.trufflehog.view.controllers.BorderPaneController;
import edu.kit.trufflehog.viewmodel.StatisticsViewModel;
import javafx.beans.property.ListProperty;
import javafx.beans.property.Property;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Popup;

/**
 * <p>
 *     The StatisticsViewController provides GUI components and functionality for every statistic view.
 * </p>
 */
public class StatisticsViewController extends BorderPaneController {

    @FXML
    private TreeTableView<StatisticsViewModel.IEntry<StringProperty, ? extends Property>> infoTable;

    private final StatisticsViewModel statViewModel;

    @FXML
    private TreeTableColumn<StatisticsViewModel.IEntry<StringProperty, ? extends Property>, String> keyColumn;

    @FXML
    private TreeTableColumn<StatisticsViewModel.IEntry<StringProperty, ? extends Property>, Object> valueColumn;

    /**
     * <p>
     *     Creates a new StatisticsViewController with the given fxmlFileName. The fxml file has to be in the same
     *     namespace as the StatisticsViewController.
     * </p>
     *
     */
    public StatisticsViewController(StatisticsViewModel statModel) {

        super("selected_statistics_view.fxml");

        this.statViewModel = statModel;

        //keyColumn = new TreeTableColumn<>("Key");
        //valueColumn = new TreeTableColumn<>("Value");

        //keyColumn.setMinWidth(100);
        keyColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<StatisticsViewModel.IEntry<StringProperty, ? extends Property>, String> param) ->
                param.getValue().getValue().getKeyProperty());

        //valueColumn.setMinWidth(100);
        valueColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<StatisticsViewModel.IEntry<StringProperty, ? extends Property>, Object> param) ->
                param.getValue().getValue().getValueProperty());

        this.visibleProperty().bind(statViewModel.getInfoListProperty().emptyProperty().not());

        infoTable.setRoot(statViewModel.getRootItem());
        infoTable.getColumns().setAll(keyColumn, valueColumn);
        infoTable.setShowRoot(false);

        infoTable.setOnMouseClicked(mouseEvent ->  {

            if(mouseEvent.getClickCount() == 2)
            {

                final TreeItem<StatisticsViewModel.IEntry<StringProperty, ? extends Property>> item = infoTable.getSelectionModel().getSelectedItem();

                if (item.getValue().getValueProperty() instanceof ListProperty) {

                    final ListProperty<IPacketData> data = (ListProperty<IPacketData>) item.getValue().getValueProperty();

                    final ListView<IPacketData> listView = new ListView<>(data);

                    final Popup popup = new Popup();
                    popup.setX(mouseEvent.getScreenX());
                    popup.setY(mouseEvent.getScreenY());
                    popup.getContent().addAll(listView);
                    popup.setAutoHide(true);
                    popup.setHideOnEscape(true);

                    popup.show(Main.pr);
                }
            }

        });
        //infoTable.setEditable(true);
    }
}
