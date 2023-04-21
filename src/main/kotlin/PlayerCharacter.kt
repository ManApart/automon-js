import ui.Sprite
import ui.anim
import ui.sprite
import ui.x

class PlayerCharacter {
    lateinit var sprite: Sprite

    suspend fun initialize() {
        sprite = setupSprite()
    }

    private suspend fun setupSprite() = sprite(
        "assets/character.png", 16, 20, 10,
        anim("idle", 0 x 0),
        anim("walk-down", 0 x 0, 1 x 0, 2 x 0, 3 x 0),
        anim("walk-up", 0 x 1, 1 x 1, 2 x 1, 3 x 1),
        anim("walk-left", 0 x 2, 1 x 2, 2 x 2, 3 x 2),
        anim("walk-right", 0 x 3, 1 x 3, 2 x 3, 3 x 3),
    )

}