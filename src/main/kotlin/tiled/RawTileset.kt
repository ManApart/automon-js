package tiled

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class RawTileset(
    val name: String,
    val image: String,
    val imageheight: Int,
    val imagewidth: Int,
    val columns: Int,
    val tilecount: Int,
    val tilewidth: Int,
    val tileheight: Int,
    val tiles: List<RawTile>
) {
    fun parse(): Map<Int, Tile> {
        val rawById = tiles.associateBy { it.id }
        return (0..tilecount).chunked(columns).mapIndexed { y, row ->
            row.mapIndexed { x, tileId ->
                val raw = rawById[tileId] ?: RawTile(tileId)
                Tile(raw.id, image, x * tilewidth, y * tileheight, tilewidth, tileheight, raw.properties.parseProperties())
            }
        }.flatten().associateBy { it.id }
    }
}

@Serializable
data class RawTile(val id: Int, val animation: List<RawAnimation> = listOf(), val properties: List<RawTiledProperty> = listOf())

@Serializable
@SerialName("tilelayer")
data class RawAnimation(val tileid: Int, val duration: Int)