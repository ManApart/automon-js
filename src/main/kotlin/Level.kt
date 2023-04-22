import tiled.*

class Level(val tileWidth: Int, val tiles: Map<Int, Map<Int, Tile>>, val objects: Map<Pair<Int, Int>, Object>) {

    fun getTile(posX: Double, posY: Double): TileInstance {
        val x = (posX / tileWidth).toInt()
        val y = (posY / tileWidth).toInt()
        val tile = tiles[y]!![x]!!
        return TileInstance(x, y, tile)
    }

    fun tick(timePassed: Double) {
        processObjects()
    }

    private fun processObjects() {
        val playerTilePos = Game.player.getTile().pos
        objects.entries.filter { (coords, _) -> coords == playerTilePos }.forEach { (_, item) ->
            when {
                item.hasProps(listOf("level"), listOf("x", "y")) -> processDoor(item)
            }
        }
    }

    private fun processDoor(door: Object) {
        println("Touching door $door")
    }
}


fun TiledMap.toLevel(): Level {
    val tiles = (layers.first { it is TileLayer } as TileLayer).tiles
    val objects = layers.filterIsInstance<ObjectLayer>().flatMap { it.objects }.associateBy { Pair((it.x/tileWidth).toInt(), (it.y/tileWidth).toInt()) }

    return Level(tileWidth, tiles, objects)
}