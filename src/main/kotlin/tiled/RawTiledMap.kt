package tiled

import kotlinx.serialization.Serializable

interface RawTiledLayer
interface RawTiledProperty

@Serializable
data class RawTiledMap(val tilewidth: Int, val tileheight: Int, val tilesets: List<TilesetReference>, val layers: List<RawTiledLayer>)

@Serializable
data class RawTileLayer(val name: String, val width: Int, val height: Int, val data: List<Int>, val type: String, val x: Int, val y: Int, val properties: List<RawTiledProperty>): RawTiledLayer

@Serializable
data class RawObjectLayer(val name: String, val objects: List<Int>, val type: String, val x: Int, val y: Int, val properties: List<RawTiledProperty>): RawTiledLayer

@Serializable
data class RawObject(val name: String, val id: Int, val properties: List<RawTiledProperty>, val rotation: Int, val x: Float, val y: Float)

@Serializable
data class TilesetReference(val firstgid: Int, val source: String)

@Serializable
data class RawStringProperty(val name: String, val value: String): RawTiledProperty

@Serializable
data class RawIntProperty(val name: String, val value: Int): RawTiledProperty