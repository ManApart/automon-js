package tiled

import kotlinx.serialization.Serializable

@Serializable
class RawTileset(val name: String, val image: String, val imageheight: Int, val imageWidth: Int, val columns: Int, val tilecount: Int,  val tilewidth: Int, val tileHeight: Int, val tiles: List<RawTile>)

@Serializable
data class RawTile(val id: Int, val properties: List<RawTiledProperty>)