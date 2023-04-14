package tiled

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString

interface RawTiledLayer {
    fun parse(rawTileSets: Map<Int, RawTileset>): TiledLayer
}

interface RawTiledProperty

@Serializable
data class RawTiledMap(val tilewidth: Int, val tileheight: Int, val tilesets: List<TilesetReference>, val layers: List<RawTiledLayer>) {

    fun parse(name: String, rawTileSets: Map<Int, RawTileset>): TiledMap {
        val layers = layers.map { it.parse(rawTileSets) }
        return TiledMap(name, tilewidth, tileheight, layers)
    }
}

@Serializable
@SerialName("tilelayer")
data class RawTileLayer(val name: String, val width: Int, val height: Int, val data: List<Int>, val x: Int, val y: Int, val properties: List<RawTiledProperty>) : RawTiledLayer {
    override fun parse(rawTileSets: Map<Int, RawTileset>): TiledLayer {
        val tiles = parseTiles(data, rawTileSets.values.first())
        val properties = properties.parseProperties()
        return TileLayer(name, x, y, width, height, tiles, properties)
    }
}

private fun parseTiles(data: List<Int>, tileset: RawTileset): Map<Int, Map<Int, Tile>> {
    val rawTiles = tileset.parse()
    return data.chunked(tileset.columns).mapIndexed { y, row ->
        y to row.mapIndexed { x, tileId ->
            x to rawTiles[tileId]!!
        }.toMap()
    }.toMap()
}

@Serializable
@SerialName("objectgroup")
data class RawObjectLayer(val name: String, val objects: List<RawObject>, val x: Int, val y: Int) : RawTiledLayer {
    override fun parse(rawTileSets: Map<Int, RawTileset>): TiledLayer {
        return ObjectLayer(name, x, y, objects.map { it.parse() })
    }
}

@Serializable
data class RawObject(val name: String, val id: Int, val properties: List<RawTiledProperty>, val rotation: Int, val x: Float, val y: Float) {
    fun parse(): Object {
        return Object(id, name, x, y, rotation, properties.parseProperties())
    }
}

@Serializable
data class TilesetReference(val firstgid: Int, val source: String)

@Serializable
@SerialName("string")
data class RawStringProperty(val name: String, val value: String) : RawTiledProperty

@Serializable
@SerialName("int")
data class RawIntProperty(val name: String, val value: Int) : RawTiledProperty


fun List<RawTiledProperty>.parseProperties(): Properties {
    val strings = mutableMapOf<String, String>()
    val ints = mutableMapOf<String, Int>()
    forEach { prop ->
        when (prop) {
            is RawStringProperty -> strings[prop.name] = prop.value
            is RawIntProperty -> ints[prop.name] = prop.value
        }
    }
    return Properties(strings, ints)
}