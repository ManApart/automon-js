import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.coroutines.*
import org.w3c.dom.HTMLElement
import org.w3c.dom.Image
import org.w3c.xhr.JSON
import org.w3c.xhr.XMLHttpRequest
import org.w3c.xhr.XMLHttpRequestResponseType
import ui.mapView
import kotlin.coroutines.CoroutineContext
import kotlin.js.Json
import kotlin.js.Promise

const val tickRate = 100
var uiTicker: (Double) -> Unit = {}
private var lastFrame = 0.0

suspend fun main() {
    Game.initialize()
    window.setInterval(::tick, tickRate)
    window.requestAnimationFrame(::uiTick)
    mapView()
}

@OptIn(DelicateCoroutinesApi::class)
private fun tick() {
    GlobalScope.launch {
        Game.tick(tickRate)
    }
}

private fun uiTick(timeStamp: Double) {
    val timePassed = timeStamp - lastFrame
    lastFrame = timeStamp
    uiTicker(timePassed)
    window.requestAnimationFrame(::uiTick)
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

suspend fun loadImage(path: String): Image {
    var loaded = false
    val image = Image()
    image.onload = {
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