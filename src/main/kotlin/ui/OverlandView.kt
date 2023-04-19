package ui

import clearSections
import el
import kotlinx.html.*
import kotlinx.html.dom.append
import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.HTMLElement
import tiled.*
import uiTicker

private val animatedTiles = mutableListOf<TileInstance>()
private lateinit var ctx: CanvasRenderingContext2D

suspend fun overlandView() {
    val map = parseMap("map.json")
    val section = el<HTMLElement>("root")
    clearSections()
    uiTicker = ::updateUI
    section.append {
        div {
            id = "map-wrapper"
            canvas {
                id = "map-canvas"
            }

        }
    }
    animatedTiles.clear()
    val canvas = el<HTMLCanvasElement>("map-canvas")
    canvas.width = map.tileWidth * map.width
    canvas.height = map.tileHeight * map.height
    ctx = canvas.getContext("2d") as CanvasRenderingContext2D
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
    layer.tiles.entries.forEach { (y, row) ->
        row.entries.forEach { (x, tile) ->
            putImageData(tile.image, x.toDouble() * tile.width, y.toDouble() * tile.height)
            if (tile.animation.steps.isNotEmpty()) animatedTiles.add(TileInstance(x, y, tile))
        }
    }
}

fun CanvasRenderingContext2D.drawLayer(layer: ObjectLayer) {

}

private fun updateUI(timePassed: Int) {
    animatedTiles.forEach { tile ->
        val animation = tile.tile.animation
        if (tile.x==1) {
            println("Animating water, ${animation.currentStep}, ${animation.timeLeft}")
        }
        if (animation.shouldStep(timePassed)) {
            animation.step()
            ctx.putImageData(animation.getData(), tile.x.toDouble() * tile.tile.width, tile.y.toDouble() * tile.tile.height)
        }
    }
}