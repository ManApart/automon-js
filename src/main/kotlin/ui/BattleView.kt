package ui

import ButtonSubscription
import Game
import clearSections
import core.Bot
import core.Terrain
import el
import kotlinx.html.div
import kotlinx.html.dom.append
import kotlinx.html.id
import kotlinx.html.img
import org.w3c.dom.HTMLElement
import playMusic
import uiTicker

suspend fun battleView(terrain: Terrain,  player: Bot, enemy: Bot) {
    println("Do battle!")
    val section = el<HTMLElement>("root")
    clearSections()
    uiTicker = ::updateUI
    section.append {
        div {
            id = "battle-wrapper"
            img("battle background") {
                src = "assets/battleBackgrounds/${terrain.battleName}.png"
                id = "battle-background"
            }
        }
    }
    Game.level?.music?.let { playMusic("battle/$it") }
//    Game.level?.backgroundColor?.let { document.body?.style?.backgroundColor = it }

    Game.controller.subscribe("pc", ButtonSubscription("z") {
        val start = Game.player.getTile()!!
        mapView(Game.level!!.map, start.x, start.y)
    })
}

private fun updateUI(timePassed: Double) {

}