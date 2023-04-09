package tiled

import kotlinx.coroutines.delay
import kotlinx.serialization.decodeFromString
import org.w3c.xhr.JSON
import org.w3c.xhr.XMLHttpRequest
import org.w3c.xhr.XMLHttpRequestResponseType
import kotlin.js.Promise

val jsonMapper = kotlinx.serialization.json.Json { ignoreUnknownKeys = true }

suspend fun parseMap(mapPath: String): RawTiledMap {
    println("parsing $mapPath")
    val json = promise { loadJson(mapPath) }
    println("parsing ${JSON.stringify(json)}")

    return jsonMapper.decodeFromString(JSON.stringify(json))
}

private fun loadJson(url: String): Promise<kotlin.js.Json> {
    return Promise { resolve, _ ->
        XMLHttpRequest().apply {
            open("GET", url)
            responseType = XMLHttpRequestResponseType.JSON
            onerror = { println("Failed to get Json") }
            onload = {
                resolve(response as kotlin.js.Json)
            }
            send()
        }
    }
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