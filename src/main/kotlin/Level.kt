import tiled.*
import ui.mapView

class Level(val map: TiledMap, val tileWidth: Int, val tiles: Map<Int, Map<Int, Tile>>, val objects: Map<Pair<Int, Int>, Object>, val music: String?, val backgroundColor: String?) {
    var playerPreviousTilePos = Pair(0, 0)

    fun getTile(posX: Double, posY: Double): TileInstance? {
        val x = (posX / tileWidth).toInt()
        val y = (posY / tileWidth).toInt()
        val tile = tiles[y]?.get(x)
//        if (tile == null) println("$x,$y is null!")
        return tile?.let { TileInstance(x, y, it) }
    }

    suspend fun tick(timePassed: Double) {
        processObjects()
    }

    private suspend fun processObjects() {
        val playerTilePos = Game.player.getTile()?.pos
        if (playerTilePos != null && playerTilePos != playerPreviousTilePos) {
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
    val tileLayer = (layers.first { it is TileLayer } as TileLayer)
    val tiles = tileLayer.tiles
    val music = tileLayer.properties.strings["music"]
    val backgroundColor = tileLayer.properties.strings["color"]
    val objects = layers.filterIsInstance<ObjectLayer>().flatMap { it.objects }.associateBy { Pair((it.x / tileWidth).toInt(), (it.y / tileWidth).toInt()) }
    return Level(this, tileWidth, tiles, objects, music, backgroundColor)
}
