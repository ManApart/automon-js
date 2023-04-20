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

private val animatedTiles = mutableMapOf<Tile, MutableSet<Pair<Int, Int>>>()
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
            if (tile.animation.steps.isNotEmpty()) {
                animatedTiles.getOrPut(tile) { mutableSetOf() }.add(x to y)
            }
        }
    }
}

fun CanvasRenderingContext2D.drawLayer(layer: ObjectLayer) {}

private fun updateUI(timePassed: Double) {
    animatedTiles.entries.forEach { (tile, instances) ->
        val animation = tile.animation
        if (animation.shouldStep(timePassed)) {
            animation.step()
            instances.forEach { (x, y) ->
                ctx.putImageData(animation.getData(), x.toDouble() * tile.width, y.toDouble() * tile.height)
            }
        }
    }
}