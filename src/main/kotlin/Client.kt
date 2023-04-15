import kotlinx.html.div
import kotlinx.html.dom.append
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.coroutines.delay
import org.w3c.dom.HTMLElement
import org.w3c.dom.Image
import org.w3c.xhr.JSON
import org.w3c.xhr.XMLHttpRequest
import org.w3c.xhr.XMLHttpRequestResponseType
import tiled.parseMap
import ui.overlandView
import kotlin.js.Json
import kotlin.js.Promise

const val tickRate = 300
var uiTicker: () -> Unit = {}

suspend fun main() {
    val container = document.getElementById("root") ?: error("Couldn't find container!")
    container.append {
        div {
            +"Hello from JS"
        }
    }
    window.setInterval(::tick, tickRate)
    overlandView()
}

private fun tick() {
    Game.tick()
    uiTicker()
}


@Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE")
suspend fun loadJson(url: String): kotlin.js.Json {
    return promise {
        Promise { resolve, _ ->
            XMLHttpRequest().apply {
                open("GET", url)
                responseType = XMLHttpRequestResponseType.JSON
                onerror = { throw IllegalStateException("Failed to get Json") }
                onload = {
                    resolve(response as Json)
                }
                send()
            }
        }
    }!!
}

suspend fun <T> promise(lambda: () -> Promise<T?>): T? {
    var done = false
    var result: T? = null
    lambda().then { res ->
        result = res
        done = true
    }
    while (!done) {
        delay(100)
    }

    return result
}

suspend fun loadImage(path: String): Image{
    var loaded = false
    val image = Image()
    println("Loading")
    image.onload = {
        println("loaded")
        loaded = true
        Unit
    }
    image.src = path
    while (!loaded) {
        delay(10)
    }
    return image
}

fun clearSections() {
    el<HTMLElement>("root").innerHTML = ""
    uiTicker = {}
}

fun <T> el(id: String) = document.getElementById(id) as T