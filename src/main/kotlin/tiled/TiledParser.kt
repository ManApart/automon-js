package tiled

import kotlinx.coroutines.delay
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import org.w3c.xhr.JSON
import org.w3c.xhr.XMLHttpRequest
import org.w3c.xhr.XMLHttpRequestResponseType
import kotlin.js.Promise

val jsonMapper = kotlinx.serialization.json.Json {
    ignoreUnknownKeys = true
    serializersModule = SerializersModule {
        polymorphic(RawTiledLayer::class){
            subclass(RawTileLayer::class)
            subclass(RawObjectLayer::class)
        }
        polymorphic(RawTiledProperty::class){
            subclass(RawStringProperty::class)
            subclass(RawIntProperty::class)
        }
    }
}

suspend fun parseMap(mapPath: String): RawTiledMap {
    println("parsing $mapPath")
    val json = promise { loadJson(mapPath) }
    println("parsing ${JSON.stringify(json)}")

    return jsonMapper.decodeFromString(JSON.stringify(json))
}

@Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE")
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