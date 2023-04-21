object Game {
    val player = PlayerCharacter()

    suspend fun initialize(){
        player.initialize()
    }
    suspend fun tick(tickRate: Int) {

    }
}