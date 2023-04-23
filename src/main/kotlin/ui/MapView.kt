package ui

import Game
import clearSections
import el
import kotlinx.browser.document
import kotlinx.html.canvas
import kotlinx.html.div
import kotlinx.html.dom.append
import kotlinx.html.id
import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.HTMLElement
import playMusic
import tiled.ObjectLayer
import tiled.Tile
import tiled.TileLayer
import tiled.parseMap
import toLevel
import uiTicker

private val animatedTiles = mutableMapOf<Tile, MutableSet<Pair<Int, Int>>>()
private lateinit var backgroundCtx: CanvasRenderingContext2D
private lateinit var spriteCtx: CanvasRenderingContext2D

suspend fun mapView(mapName: String = "map", startTileX: Int = 0, startTileY: Int = 0) {
    val map = parseMap(mapName)
    Game.level = map.toLevel()
    val tileWidth = Game.level!!.tileWidth.toDouble()
    Game.player.x = startTileX * tileWidth + (tileWidth /2)
    Game.player.y = startTileY * tileWidth + (tileWidth /2)
    Game.level?.playerPreviousTilePos = Pair(startTileX, startTileY)
    val section = el<HTMLElement>("root")
    clearSections()
    uiTicker = ::updateUI
    section.append {
        div {
            id = "map-wrapper"
            canvas("overland-canvas") {
                id = "map-background-canvas"
            }
            canvas("overland-canvas") {
                id = "map-sprite-canvas"
            }
        }
    }
    animatedTiles.clear()

    val backgroundCanvas = el<HTMLCanvasElement>("map-background-canvas")
    backgroundCanvas.width = map.tileWidth * map.width
    backgroundCanvas.height = map.tileHeight * map.height
    backgroundCtx = backgroundCanvas.getContext("2d") as CanvasRenderingContext2D
    val spriteCanvas = el<HTMLCanvasElement>("map-sprite-canvas")
    spriteCanvas.width = backgroundCanvas.width
    spriteCanvas.height = backgroundCanvas.height
    spriteCtx = spriteCanvas.getContext("2d") as CanvasRenderingContext2D
    spriteCtx.imageSmoothingEnabled = false

    map.layers.forEach { layer ->
        when (layer) {
            is TileLayer -> backgroundCtx.drawLayer(layer)
            is ObjectLayer -> backgroundCtx.drawLayer(layer)
        }
    }
    Game.player.initialize()
    Game.level?.music?.let { playMusic(it) }
    Game.level?.backgroundColor?.let { document.body?.style?.backgroundColor = it }
}

fun CanvasRenderingContext2D.drawLayer(layer: TileLayer) {
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
                backgroundCtx.putImageData(animation.getData(), x.toDouble() * tile.width, y.toDouble() * tile.height)
            }
        }
    }
    spriteCtx.clearRect(0.0, 0.0, spriteCtx.canvas.width.toDouble(), spriteCtx.canvas.height.toDouble())
    with(Game.player) {
        sprite.advanceAnimation()
        sprite.draw(spriteCtx, x, y)
    }
}