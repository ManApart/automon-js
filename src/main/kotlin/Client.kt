import kotlinx.html.div
import kotlinx.html.dom.append
import org.w3c.dom.Node
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.coroutines.delay
import org.w3c.xhr.JSON
import org.w3c.xhr.XMLHttpRequest
import org.w3c.xhr.XMLHttpRequestResponseType
import tiled.parseMap
import kotlin.js.Json
import kotlin.js.Promise

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