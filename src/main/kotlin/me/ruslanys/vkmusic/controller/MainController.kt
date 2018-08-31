package me.ruslanys.vkmusic.controller

import javafx.animation.PauseTransition
import javafx.collections.FXCollections
import javafx.fxml.FXML
import javafx.scene.control.*
import javafx.scene.control.cell.PropertyValueFactory
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.Pane
import javafx.stage.DirectoryChooser
import javafx.util.Duration
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
    @FXML private lateinit var mainView: Pane

    @FXML private lateinit var loadingImageView: ImageView
    @FXML private lateinit var tableView: TableView<Audio>
    @FXML private lateinit var openFolderMenuItem: MenuItem
    @FXML private lateinit var searchField: TextField

//    private val data: ObservableList<Audio> = FXCollections.observableArrayList(arrayListOf())
    private val data = mutableListOf<Audio>()

    @FXML
    fun initialize() {
        initLoading()
        initTable()
        initSearch()
    }

    private fun initLoading() {
        loadingImageView.image = IconUtils.getLoadingIcon()
    }

    private fun initTable() {
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
        tableView.selectionModel.selectedItemProperty().addListener { _, _, _ -> adjustMenuAvailability() }

        // Data
        tableView.items = FXCollections.observableArrayList(data)
    }

    private fun initSearch() {
        val pauseTransition = PauseTransition(Duration.millis(300.0)) // debounce mechanism
        pauseTransition.setOnFinished { _ ->
            val argument = searchField.text.toLowerCase()

            val found = mutableListOf<Audio>()
            data.forEach {
                val artist = it.artist.toLowerCase()
                val title = it.title.toLowerCase()

                if (artist.contains(argument) || title.contains(argument)) {
                    found.add(it)
                }
            }

            setItems(found)
            tableView.refresh()
        }

        searchField.textProperty().addListener { _, _, _ -> pauseTransition.playFromStart() }
    }

    @FXML
    fun selectAll() {
        tableView.selectionModel.selectAll()
    }

    @FXML
    fun refreshTable() {
        mainView.isVisible = false

        CompletableFuture.supplyAsync {
            vkClient.getAudio()
        }.thenAccept {
            data.clear()
            data.addAll(it)

            setItems(data)
            mainView.isVisible = true
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

    private fun adjustMenuAvailability() {
        val selectedItems = tableView.selectionModel.selectedItems
        openFolderMenuItem.isDisable = !(selectedItems.size == 1 && selectedItems[0].file != null)
    }

    private fun setItems(list: List<Audio>) {
        tableView.items.clear()
        tableView.items.addAll(list)
    }

    override fun onApplicationEvent(event: DownloadEvent) {
        log.info("Event $event")

        when (event) {
            is DownloadSuccessEvent -> {
                event.audio.status = DownloadStatus.SUCCESS
                event.audio.file = event.file
                adjustMenuAvailability()
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