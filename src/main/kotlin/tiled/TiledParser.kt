package tiled

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import loadJson

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

suspend fun parseMap(mapName: String): TiledMap {
    println("Parsing $mapName")
    val json = loadJson("./assets/$mapName")

    val rawMap = jsonMapper.decodeFromString<RawTiledMap>(JSON.stringify(json))
    val tileSets = rawMap.tilesets.associate { tilesetReference ->
        val fileName = "assets/" + tilesetReference.source.replace("tsx", "json")
        val tilesetJson = loadJson(fileName)
        val rawTileset = jsonMapper.decodeFromString<RawTileset>(JSON.stringify(tilesetJson))
        tilesetReference.firstgid to rawTileset
    }
    return rawMap.parse(mapName, tileSets)
}