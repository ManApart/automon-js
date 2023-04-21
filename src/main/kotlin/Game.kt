object Game {
    val controller = Controller()
    val player = PlayerCharacter()

    suspend fun initialize() {
        player.initialize()
    }

    suspend fun tick(tickRate: Int) {
        player.tick(tickRate.toDouble())
    }
}