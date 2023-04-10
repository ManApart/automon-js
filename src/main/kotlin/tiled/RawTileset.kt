package tiled

import kotlinx.serialization.Serializable

@Serializable
class RawTileset(val name: String, val image: String, val imageheight: Int, val imagewidth: Int, val columns: Int, val tilecount: Int,  val tilewidth: Int, val tileheight: Int, val tiles: List<RawTile>) {
    fun parse(): Map<Int, Tile>{
        return mapOf()
    }
}

@Serializable
data class RawTile(val id: Int, val properties: List<RawTiledProperty>)