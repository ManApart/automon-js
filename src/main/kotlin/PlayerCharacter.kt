import tiled.TileInstance
import ui.Sprite
import ui.anim
import ui.sprite
import ui.x

class PlayerCharacter {
    lateinit var sprite: Sprite
    var x: Double = 0.0
    var y: Double = 0.0
    private var lastFacing = "down"

    suspend fun initialize() {
        sprite = setupSprite()
        Game.controller.subscribe("pc", ButtonSubscription(" "){
            println("Tile: ${getTile()}")
        })
    }

    private suspend fun setupSprite() = sprite(
        "assets/character.png", 16, 20, 10,
        anim("idle-down", 0 x 0),
        anim("idle-up", 0 x 1),
        anim("idle-left", 0 x 2),
        anim("idle-right", 0 x 3),
        anim("walk-down", 0 x 0, 1 x 0, 2 x 0, 3 x 0),
        anim("walk-up", 0 x 1, 1 x 1, 2 x 1, 3 x 1),
        anim("walk-left", 0 x 2, 1 x 2, 2 x 2, 3 x 2),
        anim("walk-right", 0 x 3, 1 x 3, 2 x 3, 3 x 3),
    )

    fun getTile(): TileInstance {
        return Game.level!!.getTile(x,y)
    }

    suspend fun tick(timePassed: Double) {
        var dx = 0.0
        var dy = 0.0
        val scale = if (timePassed == 0.0) 0.0 else (timePassed / 16.666666)
//        val startTile = getTile(getSpriteAnchor())!!
//        val terrainMovement = bot.core.getMovement(startTile.type.terrain) / 200.toDouble()
        //TODO - instead of 0, terrain movement
        val movement = 0.5 + 0 //Min speed of 0.5, max speed of 1 if you have 100% movement

        with(Game.controller) {
            var animation = "idle-$lastFacing"
            if (up) {
                animation = "walk-up"
                lastFacing = "up"
                dy = -movement * scale
            }
            if (down) {
                animation ="walk-down"
                lastFacing = "down"
                dy = movement * scale
            }
            if (left) {
                animation ="walk-left"
                lastFacing = "left"
                dx = -movement * scale
            }
            if (right) {
                animation ="walk-right"
                lastFacing = "right"
                dx = movement * scale
            }
            sprite.setAnimation(animation)
        }
//        tryMove(startTile, dx, dy)
        x += dx
        y += dy
    }

}