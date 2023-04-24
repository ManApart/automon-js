package ui

import ButtonSubscription
import Game
import clearSections
import core.Bot
import core.Terrain
import el
import kotlinx.browser.document
import kotlinx.html.*
import kotlinx.html.dom.append
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLElement
import playMusic
import sub
import uiTicker

private lateinit var upButton: HTMLDivElement
private lateinit var downButton: HTMLDivElement
private lateinit var leftButton: HTMLDivElement
private lateinit var rightButton: HTMLDivElement
private lateinit var buttons: List<HTMLDivElement>
private lateinit var selectedButton: HTMLDivElement

suspend fun battleView(terrain: Terrain, player: Bot, enemy: Bot) {
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
            div {
                id = "battle-buttons"
                div("battle-button") {
                    id = "up-button"
                    +"Inspect"
                }
                div("battle-button") {
                    id = "down-button"
                    +"-"
                }
                div("battle-button") {
                    id = "right-button"
                    +"Action"
                }
                div("battle-button") {
                    id = "left-button"
                    +"Flee"
                }
            }
        }
    }
    upButton = el("up-button")
    downButton = el("down-button")
    leftButton = el("left-button")
    rightButton = el("right-button")
    buttons = listOf(upButton, downButton, leftButton, rightButton)
    Game.level?.music?.let { playMusic("battle/$it") }
    document.body?.style?.backgroundColor = "#" + terrain.color

    sub("pc") {
        "z" to {
            if (Game.level != null) {
                val start = Game.player.getTile()
                mapView(Game.level!!.map, start?.x ?: 0, start?.y ?: 0)
            } else {
                mapView()
            }
            //TODO - go back instead
        }
        "ArrowUp" to {
            selectButton(upButton)
        }
        "ArrowDown" to {
            selectButton(downButton)
        }
        "ArrowLeft" to {
            selectButton(leftButton)
        }
        "ArrowRight" to {
            selectButton(rightButton)
        }
        " " to {
            useSelected()
        }
    }
}

private fun selectButton(button: HTMLDivElement) {
    println("Selecting ${button.id}")
    buttons.forEach { btn -> btn.classList.remove("selected-button") }
    button.classList.add("selected-button")
}

private fun useSelected() {

}

private fun updateUI(timePassed: Double) {

}