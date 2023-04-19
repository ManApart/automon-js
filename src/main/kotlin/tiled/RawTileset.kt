package tiled

import kotlinx.browser.document
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import loadImage
import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.HTMLCanvasElement

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
    suspend fun parse(): Map<Int, Tile> {
        val width = tilewidth.toDouble()
        val height = tileheight.toDouble()

        val tileSheetCanvas = (document.createElement("canvas") as HTMLCanvasElement)
        val tileSheetCtx = tileSheetCanvas.getContext("2d", js("{ willReadFrequently: true }")) as CanvasRenderingContext2D

        val tileSheet = loadImage("assets/$image")
        tileSheetCanvas.width = tileSheet.width
        tileSheetCanvas.height = tileSheet.height
        tileSheetCtx.drawImage(tileSheet, 0.0, 0.0)


        val rawById = tiles.associateBy { it.id }
        return (0..tilecount).chunked(columns).mapIndexed { y, row ->
            row.mapIndexed { x, tileId ->
                val raw = rawById[tileId] ?: RawTile(tileId)

                val data = tileSheetCtx.getImageData(x * width, y * height, width, height)

                Tile(raw.id, data, tilewidth, tileheight, raw.properties.parseProperties())
            }
        }.flatten().associateBy { it.id }.also { tiles ->
            parseAnimations(tiles, rawById)
        }
    }

    private fun parseAnimations(tiles: Map<Int, Tile>, rawById: Map<Int, RawTile>) {
        tiles.values.forEach { tile ->
            val raw = rawById[tile.id]
            if (raw != null && raw.animation.isNotEmpty()) {
                tile.animation = Animation(raw.animation.map { tiles[it.tileid]!! to it.duration })
            }
        }
    }
}

@Serializable
data class RawTile(val id: Int, val animation: List<RawAnimation> = listOf(), val properties: List<RawTiledProperty> = listOf())

@Serializable
@SerialName("tilelayer")
data class RawAnimation(val tileid: Int, val duration: Int)