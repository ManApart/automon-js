import tiled.*
import ui.mapView

class Level(val tileWidth: Int, val tiles: Map<Int, Map<Int, Tile>>, val objects: Map<Pair<Int, Int>, Object>, val music: String?) {
    var playerPreviousTilePos = Pair(0, 0)

    fun getTile(posX: Double, posY: Double): TileInstance {
        val x = (posX / tileWidth).toInt()
        val y = (posY / tileWidth).toInt()
        val tile = tiles[y]!![x]!!
        return TileInstance(x, y, tile)
    }

    suspend fun tick(timePassed: Double) {
        processObjects()
    }

    private suspend fun processObjects() {
        val playerTilePos = Game.player.getTile().pos
        if (playerTilePos != playerPreviousTilePos) {
            playerPreviousTilePos = playerTilePos
            objects.entries.filter { (coords, _) -> coords == playerTilePos }.forEach { (_, item) ->
                when {
                    item.hasProps(listOf("level"), listOf("x", "y")) -> loadDoor(item)
                    else -> println("Unknown object $item")
                }
            }
        }
    }

    private suspend fun loadDoor(door: Object) {
        mapView(door.stringProp("level")!!, door.intProp("x")!!, door.intProp("y")!!)
    }
}


fun TiledMap.toLevel(): Level {
    val tileLayer =(layers.first { it is TileLayer } as TileLayer)
    val tiles = tileLayer.tiles
    val music = tileLayer.properties.strings["music"]
    val objects = layers.filterIsInstance<ObjectLayer>().flatMap { it.objects }.associateBy { Pair((it.x / tileWidth).toInt(), (it.y / tileWidth).toInt()) }
    return Level(tileWidth, tiles, objects, music)
}