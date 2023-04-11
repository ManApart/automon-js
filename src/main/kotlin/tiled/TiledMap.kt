package tiled

interface TiledLayer

data class Properties(val strings: Map<String, String> = mapOf(), val ints: Map<String, Int> = mapOf())

data class TiledMap(val name: String, val width: Int, val height: Int, val layers: List<TiledLayer>)

data class TileLayer(val name: String, val x: Int, val y: Int, val width: Int, val height: Int, val tiles: Map<Int, Map<Int, Tile>>, val properties: Properties = Properties()) : TiledLayer

//TODO - instead of x,y,z,width etc jsut give the cropped image
data class Tile(val id: Int, val image: String, val imageX: Int, val imageY: Int, val width: Int, val height: Int, val properties: Properties = Properties())

data class ObjectLayer(val name: String, val x: Int, val y: Int, val objects: List<Object>): TiledLayer

data class Object(val id: Int, val name: String, val x: Float, val y: Float, val rotation: Int, val properties: Properties = Properties())