package tiled

import org.w3c.dom.ImageData

interface TiledLayer

data class Properties(val strings: Map<String, String> = mapOf(), val ints: Map<String, Int> = mapOf())

data class TiledMap(val name: String, val width: Int, val height: Int, val tileWidth: Int, val tileHeight: Int, val layers: List<TiledLayer>)

data class TileLayer(val name: String, val x: Int, val y: Int, val width: Int, val height: Int, val tiles: Map<Int, Map<Int, Tile>>, val properties: Properties = Properties()) : TiledLayer

data class Tile(val id: Int, val image: ImageData, val width: Int, val height: Int, val properties: Properties = Properties()) {
    var animation: Animation = Animation()
}

data class Animation(val steps: List<Pair<Tile, Int>> = listOf(), var currentStep: Int = 0, var timeLeft: Double = 0.0) {
    fun shouldStep(timePassed: Double): Boolean {
        timeLeft -= timePassed
        return timeLeft < 0
    }

    fun step() {
        currentStep++
        if (currentStep >= steps.size) currentStep = 0
        timeLeft = steps[currentStep].second.toDouble()
    }

    fun getData(): ImageData {
        return steps[currentStep].first.image
    }
}

data class ObjectLayer(val name: String, val x: Int, val y: Int, val objects: List<Object>) : TiledLayer

data class Object(val id: Int, val name: String, val x: Float, val y: Float, val rotation: Int, val properties: Properties = Properties())