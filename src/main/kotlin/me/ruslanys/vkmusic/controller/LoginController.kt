package me.ruslanys.vkmusic.controller

import javafx.application.Platform
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.concurrent.Worker
import javafx.fxml.FXML
import javafx.scene.Scene
import javafx.scene.image.ImageView
import javafx.scene.layout.BorderPane
import javafx.scene.layout.Pane
import javafx.scene.web.WebView
import javafx.stage.Stage
import me.ruslanys.vkmusic.annotation.FxmlController
import me.ruslanys.vkmusic.component.VkClient
import me.ruslanys.vkmusic.property.VkCookies
import me.ruslanys.vkmusic.service.PropertyService
import me.ruslanys.vkmusic.util.IconUtils
import java.net.CookieManager
import java.net.URI
import java.util.concurrent.CompletableFuture

@FxmlController(view = "views/login.fxml")
class LoginController(
        private val propertyService: PropertyService,
        private val vkClient: VkClient,
        private val mainController: MainController) : ChangeListener<Worker.State>, BaseController() {

    @FXML private lateinit var view: Pane
    @FXML private lateinit var loadingView: BorderPane
    @FXML private lateinit var loadingImageView: ImageView

    private lateinit var webView: WebView

    @FXML
    fun initialize() {
        initCookieManager()
        initLoading()
        Platform.runLater(this::initWebView)
    }

    /**
     * <p>It isn't possible to follow
     * <a href="https://docs.oracle.com/javase/tutorial/deployment/doingMoreWithRIA/accessingCookies.html">Oracle Accessing Cookies Tutorial</a>
     * via default {@link CookieManager} with {@link java.net.InMemoryCookieStore} under the hood.
     *
     * <p>{@link CookieManager} is the implementation of <a href="http://www.ietf.org/rfc/rfc2965.txt">RFC 2965</a>.
     * But the website needs to implement <a href="http://www.ietf.org/rfc/rfc6265.txt">RFC 6265</a>.
     *
     * <p>RFC 2965: "x.y.com domain-matches .Y.com but not Y.com."<br/>
     * RFC 6265: "The domain string is a suffix of the string. The last character of the string that is not included in the domain string is a "." character." <br/>
     * Take a look at <a href="https://github.com/square/okhttp/issues/991">OkHttp #991</a>.
     *
     * <p>So, private {@link com.sun.webkit.network.CookieManager} is implementing RFC 6265, that's why it's using.
     */
    private fun initCookieManager() {
        CookieManager.setDefault(com.sun.webkit.network.CookieManager())
    }

    private fun initLoading() {
        loadingImageView.image = IconUtils.getLoadingIcon()
    }

    private fun initWebView() {
        webView = WebView()
        val engine = webView.engine
        engine.isJavaScriptEnabled = true
        engine.userAgent = USER_AGENT

        webView.engine.loadWorker.stateProperty().addListener(this)

        //webView.isVisible = false
        view.children.add(webView)

        webView.engine.load(LOGIN_PATH)
        propertyService.get(VkCookies::class.java)?.let {
            setSessionId(it.sessionId)
        }
    }

    override fun changed(observable: ObservableValue<out Worker.State>?, oldValue: Worker.State?, newValue: Worker.State?) {
        when (newValue) {
            Worker.State.SUCCEEDED -> {
                webView.isVisible = true
                loadingView.isVisible = false

                val sessionId = fetchSessionId()
                sessionId?.let { setSessionId(it) }
            }
            Worker.State.SCHEDULED -> {
                webView.isVisible = false
                loadingView.isVisible = true
            }
        }
    }

    private fun fetchSessionId(): String? {
        val headers = CookieManager.getDefault().get(COOKIE_DOMAIN_URI, mutableMapOf())
        val values = headers["Cookie"] ?: return null

        return values[0]
                .split(";")
                .map { it.split("=") }
                .flatMap { listOf(it[0] to it[1]) }
                .associateBy { it.first.trim() }
                .mapValues { it.value.second }[SESSION_ID_KEY]
    }

    private fun setSessionId(sessionId: String) {
        CompletableFuture.runAsync {
            vkClient.setCookies(mapOf(SESSION_ID_KEY to sessionId))
            vkClient.fetchUserId()
            propertyService.set(VkCookies(sessionId))
        }.thenRun {
            Platform.runLater(this::openMainStage)
        }.exceptionally {
            Platform.runLater { webView.engine.load(LOGIN_PATH) }
            null
        }
    }

    private fun openMainStage() {
        mainController.refreshTable()

        val stage = view.sceneProperty().get().window as Stage
        stage.title = "VKMusic"

        stage.scene = Scene(mainController.rootView)

        stage.width = 640.0
        stage.height = 480.0

        stage.isResizable = true
        stage.centerOnScreen()
        stage.show()
    }

    companion object {
        private const val USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/68.0.3440.106 Safari/537.36"
        private const val SESSION_ID_KEY = "remixsid"
        private const val LOGIN_PATH = "https://vk.com/login"

        private val COOKIE_DOMAIN_URI = URI.create("https://vk.com/")
    }

}