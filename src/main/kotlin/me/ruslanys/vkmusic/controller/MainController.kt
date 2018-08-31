package me.ruslanys.vkmusic.controller

import javafx.collections.FXCollections
import javafx.collections.ListChangeListener
import javafx.collections.ObservableList
import javafx.fxml.FXML
import javafx.scene.control.MenuItem
import javafx.scene.control.SelectionMode
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.scene.control.cell.PropertyValueFactory
import javafx.scene.image.Image
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
import me.ruslanys.vkmusic.util.DesktopUtils
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
    @FXML private lateinit var openFolderMenuItem: MenuItem

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
            it.cellValueFactory = PropertyValueFactory<Audio, String>("id")
            it.maxWidth = 100.0
            it.minWidth = 60.0
            it.isVisible = false
        }

        val artistColumn = TableColumn<Audio, String>("Исполнитель").also {
            it.cellValueFactory = PropertyValueFactory<Audio, String>("artist")
            it.minWidth = 100.0
        }

        val titleColumn = TableColumn<Audio, String>("Наименование").also {
            it.cellValueFactory = PropertyValueFactory<Audio, String>("title")
            it.minWidth = 100.0
        }

        val durationColumn = TableColumn<Audio, String>("Продолжительность").also {
            it.cellValueFactory = PropertyValueFactory<Audio, String>("duration")
            it.style = "-fx-alignment: CENTER;"
            it.maxWidth = 80.0
            it.minWidth = 80.0
        }

        val statusColumn = TableColumn<Audio, Image>("Статус").also {
            it.cellValueFactory = PropertyValueFactory<Audio, Image>("statusIcon")
            it.style = "-fx-alignment: CENTER;"
            it.minWidth = 55.0
            it.maxWidth = 55.0
        }

        // --
        tableView.columns.setAll(idColumn, artistColumn, titleColumn, durationColumn, statusColumn)
        tableView.selectionModel.selectionMode = SelectionMode.MULTIPLE
        tableView.selectionModel.selectedItems.addListener(ListChangeListener {
            openFolderMenuItem.isDisable = !(it.list.size == 1 && it.list[0].file != null)
        })

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

    @FXML
    fun openFolder() {
        val file = tableView.selectionModel.selectedItem.file
        file?.let { DesktopUtils.open(file.parentFile) }
    }

    override fun onApplicationEvent(event: DownloadEvent) {
        log.info("Event $event")

        when (event) {
            is DownloadSuccessEvent -> {
                event.audio.status = DownloadStatus.SUCCESS
                event.audio.file = event.file
            }
            is DownloadFailEvent -> event.audio.status = DownloadStatus.FAIL
            is DownloadInProgressEvent -> event.audio.status = DownloadStatus.IN_PROGRESS
        }

        tableView.refresh()
    }

    companion object {
        private val log = LoggerFactory.getLogger(MainController::class.java)
    }

}