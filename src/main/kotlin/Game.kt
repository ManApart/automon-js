object Game {
    val controller = Controller()
    val player = PlayerCharacter()
    var level: Level? = null

    suspend fun initialize() {
        player.initialize()
    }

    suspend fun tick(tickRate: Int) {
        player.tick(tickRate.toDouble())
        level?.tick(tickRate.toDouble())
    }
}