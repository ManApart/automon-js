package ui

import clearSections
import el
import kotlinx.html.*
import kotlinx.html.dom.append
import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.HTMLElement
import tiled.ObjectLayer
import tiled.TileLayer
import tiled.parseMap
import uiTicker

suspend fun overlandView() {
    val map = parseMap("map.json")
    val section = el<HTMLElement>("root")
    clearSections()
    uiTicker = {}
    section.append {
        div {
            id = "map-wrapper"
            canvas {
                id = "map-canvas"
            }

        }
    }
    val canvas = el<HTMLCanvasElement>("map-canvas")
    canvas.width = map.tileWidth * map.width
    canvas.height = map.tileHeight * map.height
    val ctx = canvas.getContext("2d") as CanvasRenderingContext2D
    map.layers.forEach { layer ->
        when (layer) {
            is TileLayer -> ctx.drawLayer(layer)
            is ObjectLayer -> ctx.drawLayer(layer)
        }
    }
}

fun CanvasRenderingContext2D.drawLayer(layer: TileLayer) {
    val tiles = layer.tiles.flatMap { it.value.values }.count()
    val row = layer.tiles.values.size
    val col = layer.tiles.values.first().values.size
    println("Drawing Layer ${layer.width}x${layer.height}, $tiles, ($row, $col)")
    fillStyle = "blue"
    val first = layer.tiles.values.first().values.first()
    fillRect(0.0, 0.0, layer.width.toDouble() * first.width, layer.height.toDouble() * first.width)
    layer.tiles.entries.forEach { (y, row) ->
        row.entries.forEach { (x, tile) ->
            putImageData(tile.image, x.toDouble() * tile.width, y.toDouble() * tile.height)
        }
    }
}

fun CanvasRenderingContext2D.drawLayer(layer: ObjectLayer) {

}
