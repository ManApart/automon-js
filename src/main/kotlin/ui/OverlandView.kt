package ui

import clearSections
import el
import kotlinx.html.*
import kotlinx.html.dom.append
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
            map.layers.forEach { layer ->
                when (layer) {
                    is TileLayer -> drawLayer(layer)
                    is ObjectLayer -> drawLayer(layer)
                }
            }
        }
    }
}

fun TagConsumer<HTMLElement>.drawLayer(layer: TileLayer) {
    div("tile-layer") {
        layer.tiles.entries.forEach { (y, row) ->
            div("tile-row") {
                row.entries.forEach { (x, tile) ->
                    div("tile"){
                        img(tile.id.toString()) {
                            classes = setOf("tile-image")
                            src = tile.image.src
                        }
                    }
                }
            }
        }
    }
}

fun TagConsumer<HTMLElement>.drawLayer(layer: ObjectLayer) {

}
