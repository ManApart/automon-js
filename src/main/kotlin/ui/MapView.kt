package ui

import clearSections
import el
import enableMusic
import kotlinx.html.*
import kotlinx.html.dom.append
import musicPlayer
import org.w3c.dom.Audio
import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.HTMLElement
import playMusic
import tiled.*
import toLevel
import uiTicker

private val animatedTiles = mutableMapOf<Tile, MutableSet<Pair<Int, Int>>>()
private lateinit var backgroundCtx: CanvasRenderingContext2D
private lateinit var spriteCtx: CanvasRenderingContext2D

suspend fun mapView(mapName: String = "map", startTileX: Int = 0, startTileY: Int = 0) {
    val map = parseMap(mapName)
    Game.level = map.toLevel()
    Game.player.x = startTileX * Game.level!!.tileWidth.toDouble()
    Game.player.y = startTileY * Game.level!!.tileWidth.toDouble()
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

    map.layers.forEach { layer ->
        when (layer) {
            is TileLayer -> backgroundCtx.drawLayer(layer)
            is ObjectLayer -> backgroundCtx.drawLayer(layer)
        }
    }
    Game.level?.music?.let { playMusic(it) }
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