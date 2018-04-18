package me.ruslanys.vkmusic.javafx.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import me.ruslanys.vkmusic.entity.Audio;
import me.ruslanys.vkmusic.service.AudioService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * Date: 27.08.15
 * Time: 11:10
 *
 * @author Ruslan Molchanov (ruslanys@gmail.com)
 * @author http://mruslan.com
 */
@SuppressWarnings("SpringJavaAutowiringInspection")
public class MainController {

    // Инъекции Spring
    @Autowired
    private AudioService audioService;

    // Инъекции JavaFX
    @FXML private TableView<Audio> table;

    // Variables
    private ObservableList<Audio> data;

    /**
     * Инициализация контроллера от JavaFX.
     * Метод вызывается после того как FXML загрузчик произвел инъекции полей.
     *
     * Обратите внимание, что имя метода <b>обязательно</b> должно быть "initialize",
     * в противном случае, метод не вызовется.
     *
     * Также на этом этапе еще отсутствуют бины спринга
     * и для инициализации лучше использовать метод,
     * описанный аннотацией @PostConstruct,
     * который вызовется спрингом, после того, как им будут произведены все инъекции.
     * {@link MainController#init()}
     */
    @FXML
    public void initialize() {
        // Этап инициализации JavaFX
    }

    /**
     * На этом этапе уже произведены все возможные инъекции.
     */
    @SuppressWarnings("unchecked")
    @PostConstruct
    public void init() {
        List<Audio> audio = audioService.findAll();
        data = FXCollections.observableArrayList(audio);

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

        table.getColumns()
                .setAll(idColumn, artistColumn, titleColumn, durationColumn, statusColumn);

        // Данные таблицы
        table.setItems(data);
    }

    /**
     * Метод, вызываемый при нажатии на кнопку "Добавить".
     * Привязан к кнопке в FXML файле представления.
     */
    @FXML
    public void addContact() {
    }
}
