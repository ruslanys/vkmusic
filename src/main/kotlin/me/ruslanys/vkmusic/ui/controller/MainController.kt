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
import javafx.stage.DirectoryChooser
import me.ruslanys.vkmusic.annotation.FxmlController
import me.ruslanys.vkmusic.component.VkClient
import me.ruslanys.vkmusic.domain.Audio
import me.ruslanys.vkmusic.domain.DownloadStatus
import me.ruslanys.vkmusic.event.DownloadEvent
import me.ruslanys.vkmusic.event.DownloadFailEvent
import me.ruslanys.vkmusic.event.DownloadInProgressEvent
import me.ruslanys.vkmusic.event.DownloadSuccessEvent
import me.ruslanys.vkmusic.service.DownloadService
import me.ruslanys.vkmusic.util.IconUtils
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationListener
import java.util.concurrent.CompletableFuture

@FxmlController(view = "views/main.fxml")
class MainController(
        private val vkClient: VkClient,
        private val downloadService: DownloadService) : ApplicationListener<DownloadEvent>, BaseController() {

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

    @FXML
    fun downloadSelected() {
        val directoryChooser = DirectoryChooser()
        directoryChooser.title = "Укажите папку назначения"
        val selectedDirectory = directoryChooser.showDialog(rootView!!.scene.window) ?: return


        tableView.selectionModel.selectedItems.apply {
            forEach { it.status = DownloadStatus.QUEUED }
            tableView.refresh()

            downloadService.download(selectedDirectory, this.toList())
        }
    }

    override fun onApplicationEvent(event: DownloadEvent) {
        log.info("Event $event")

        when (event) {
            is DownloadSuccessEvent -> event.audio.status = DownloadStatus.SUCCESS
            is DownloadFailEvent -> event.audio.status = DownloadStatus.FAIL
            is DownloadInProgressEvent -> event.audio.status = DownloadStatus.IN_PROGRESS
        }

        tableView.refresh()
    }

    companion object {
        private val log = LoggerFactory.getLogger(MainController::class.java)
    }

}