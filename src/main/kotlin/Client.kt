import kotlinx.html.div
import kotlinx.html.dom.append
import org.w3c.dom.Node
import kotlinx.browser.document
import kotlinx.browser.window
import tiled.parseMap

suspend fun main() {
    val container = document.getElementById("root") ?: error("Couldn't find container!")
    container.append {
        div {
            +"Hello from JS"
        }
    }
    val map = parseMap("map.json")
    println("parsed ${JSON.stringify(map)}")
}