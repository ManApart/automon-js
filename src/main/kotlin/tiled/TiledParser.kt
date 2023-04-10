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
        polymorphic(RawTiledLayer::class) {
            subclass(RawTileLayer::class)
            subclass(RawObjectLayer::class)
        }
        polymorphic(RawTiledProperty::class) {
            subclass(RawStringProperty::class)
            subclass(RawIntProperty::class)
        }
    }
}

suspend fun parseMap(mapName: String): RawTiledMap {
    println("parsing $mapName")
    val json = promise { loadJson("./assets/$mapName") }

    val rawMap = jsonMapper.decodeFromString<RawTiledMap>(JSON.stringify(json))
    val tileSets = rawMap.tilesets.associate { tilesetReference ->
        val fileName ="assets/" + tilesetReference.source.replace("tsx", "json")
        val tilesetJson = promise { loadJson(fileName) }
        val rawTileset = jsonMapper.decodeFromString<RawTileset>(JSON.stringify(tilesetJson))
        tilesetReference.firstgid to rawTileset
    }
    println(tileSets)
    return rawMap
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