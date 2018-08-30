package me.ruslanys.vkmusic.ui.controller

import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.fxml.FXML
import javafx.scene.control.SelectionMode
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.scene.control.cell.PropertyValueFactory
import javafx.scene.image.ImageView
import javafx.scene.layout.Pane
import me.ruslanys.vkmusic.annotation.FxmlController
import me.ruslanys.vkmusic.component.VkClient
import me.ruslanys.vkmusic.domain.Audio
import me.ruslanys.vkmusic.util.IconUtils
import java.util.concurrent.CompletableFuture

@FxmlController(view = "views/main.fxml")
class MainController(private val vkClient: VkClient) : BaseController() {

    @FXML private lateinit var loadingView: Pane
    @FXML private lateinit var loadingImageView: ImageView
    @FXML private lateinit var tableView: TableView<Audio>

    private lateinit var data: ObservableList<Audio>

    @FXML
    fun initialize() {
        initLoading()
        initTable()
    }

    private fun initLoading() {
        loadingImageView.image = IconUtils.getLoadingIcon()
    }

    private fun initTable() {
        data = FXCollections.observableArrayList(arrayListOf())

        // Columns
        val idColumn = TableColumn<Audio, String>("ID").also {
            it.setCellValueFactory(PropertyValueFactory<Audio, String>("id"))
        }

        val artistColumn = TableColumn<Audio, String>("Исполнитель").also {
            it.setCellValueFactory(PropertyValueFactory<Audio, String>("artist"))
        }

        val titleColumn = TableColumn<Audio, String>("Наименование").also {
            it.setCellValueFactory(PropertyValueFactory<Audio, String>("title"))
        }

        val durationColumn = TableColumn<Audio, String>("Продолжительность").also {
            it.setCellValueFactory(PropertyValueFactory<Audio, String>("duration"))
        }

        val statusColumn = TableColumn<Audio, String>("Статус").also {
            it.setCellValueFactory(PropertyValueFactory<Audio, String>("status"))
        }

        // --
        tableView.columns.setAll(idColumn, artistColumn, titleColumn, durationColumn, statusColumn)
        tableView.selectionModel.selectionMode = SelectionMode.MULTIPLE

        // Data
        tableView.items = data
    }

    @FXML
    fun selectAll() {
        tableView.selectionModel.selectAll()
    }

    @FXML
    fun refreshTable() {
        tableView.isVisible = false

        CompletableFuture.supplyAsync {
            vkClient.getAudio()
        }.thenAccept {
            data.clear()
            data.addAll(it)
            tableView.isVisible = true
        }
    }

}