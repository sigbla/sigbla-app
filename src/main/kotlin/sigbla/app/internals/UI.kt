package sigbla.app.internals

import javafx.scene.layout.BorderPane
import tornadofx.*

class SigblaView : View() {
    override val root = BorderPane()

    init {
        with(root) {
            left = textarea {
                text = "Text area text"
            }
            center = webview {
                engine.load("https://cfelde.com")
            }
        }
    }
}

class SigblaApp : App(SigblaView::class)