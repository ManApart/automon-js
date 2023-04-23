import core.Bot

object Game {
    val controller = Controller()
    val player = MapPlayerCharacter(Bot())
    var level: Level? = null
    val ignoreMP = true
    var enableMusic = false

    suspend fun initialize() {
        player.initialize()
    }

    suspend fun tick(tickRate: Int) {
        player.tick(tickRate.toDouble())
        level?.tick(tickRate.toDouble())
    }

    fun firstPlayerInteraction(){
        level?.music?.let { playMusic(it) }
    }
}