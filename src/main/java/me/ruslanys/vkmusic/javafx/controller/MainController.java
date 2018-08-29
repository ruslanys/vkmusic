package me.ruslanys.vkmusic.javafx.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import me.ruslanys.vkmusic.annotation.FxmlController;
import me.ruslanys.vkmusic.component.VkClient;
import me.ruslanys.vkmusic.domain.Audio;
import me.ruslanys.vkmusic.util.IconUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

@FxmlController(view = "views/fxml/main.fxml")
public class MainController {

    @FXML public Pane loadingView;
    @FXML public ImageView loadingImageView;
    @FXML private TableView<Audio> tableView;

    @Autowired
    private VkClient vkClient;


    private ObservableList<Audio> data;


    @FXML
    public void initialize() {
        initLoadingIcon();
        initTableView();
    }

    private void initLoadingIcon() {
        loadingImageView.setImage(IconUtils.INSTANCE.getLoadingIcon());
    }

    private void initTableView() {
        data = FXCollections.observableArrayList(new ArrayList<>());

        // Столбцы таблицы
        TableColumn<Audio, String> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Audio, String> artistColumn = new TableColumn<>("Исполнитель");
        artistColumn.setCellValueFactory(new PropertyValueFactory<>("artist"));

        TableColumn<Audio, String> titleColumn = new TableColumn<>("Наименование");
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));

        TableColumn<Audio, Integer> durationColumn = new TableColumn<>("Продолжительность");
        durationColumn.setCellValueFactory(new PropertyValueFactory<>("duration"));

        TableColumn<Audio, String> statusColumn = new TableColumn<>("Статус");
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        tableView.getColumns()
                .setAll(idColumn, artistColumn, titleColumn, durationColumn, statusColumn);

        tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        // Данные таблицы
        tableView.setItems(data);
    }

    @FXML
    public void selectAll() {
        tableView.getSelectionModel().selectAll();
    }

    @FXML
    public void refreshTable() {
        tableView.setVisible(false);

        CompletableFuture
                .supplyAsync(() -> vkClient.getAudio(vkClient.fetchUserId()))
                .thenAccept(audio -> {
                    data.clear();
                    data.addAll(audio);
                    tableView.setVisible(true);
                });
    }

}
