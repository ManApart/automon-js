package ui

import Game
import clearSections
import core.Bot
import core.Terrain
import el
import kotlinx.browser.document
import kotlinx.html.div
import kotlinx.html.dom.append
import kotlinx.html.id
import kotlinx.html.img
import kotlinx.html.style
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLImageElement
import playMusic
import sub
import uiTicker

private lateinit var upButton: HTMLDivElement
private lateinit var downButton: HTMLDivElement
private lateinit var leftButton: HTMLDivElement
private lateinit var rightButton: HTMLDivElement
private lateinit var buttons: List<HTMLDivElement>
private var selectedButton: HTMLDivElement? = null
private var previousMenu: (suspend () -> Unit)? = null
private var cleanup: (suspend () -> Unit)? = null

private val actions = mutableMapOf<HTMLDivElement, suspend () -> Unit>()

suspend fun battleView(terrain: Terrain, player: Bot, enemy: Bot) {
    Game.enemyBot = enemy
    val section = el<HTMLElement>("root")
    clearSections()
    uiTicker = ::updateUI
    section.append {
        div {
            id = "battle-wrapper"
            div {
                id = "battle-wrapper-children"
            }
            img("battle background") {
                src = "assets/battleBackgrounds/${terrain.battleName}.png"
                id = "battle-background"
            }
            img(classes = "battle-bot-image") {
                id = "player-bot"
                src = "assets/character.png"
                style = "object-position: 0px -60px; left: 20%"
            }
            img(classes = "battle-bot-image") {
                id = "enemy-bot"
                src = "assets/character.png"
                style = "object-position: 0px -40px; left: 70%;"
            }
            div {
                id = "battle-buttons"
                div("battle-button") {
                    id = "up-button"
                }
                div {
                    id = "battle-arms-section"
                    div("battle-button") {
                        id = "left-button"
                    }
                    div("battle-button") {
                        id = "right-button"
                    }
                }
                div("battle-button") {
                    id = "down-button"
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
            previousMenu?.let { it() }
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
    mainOptions()
}

private fun selectButton(button: HTMLDivElement) {
    selectedButton = button
    buttons.forEach { btn -> btn.classList.remove("selected-button") }
    button.classList.add("selected-button")
}

private suspend fun useSelected() {
    selectedButton?.let {
        actions[it]?.invoke()?.also {
            buttons.forEach { btn -> btn.classList.remove("selected-button") }
        }
    }
}

private fun updateUI(timePassed: Double) {

}

private suspend fun clearActions() {
    actions.clear()
    buttons.forEach { it.textContent = "-" }
    cleanup?.let { it() }
}

private suspend fun mainOptions() {
    clearActions()
    upButton.textContent = "Inspect"
    actions[upButton] = ::inspect
    leftButton.textContent = "Flee"
    actions[leftButton] = ::flee
    rightButton.textContent = "Action"
}

private suspend fun flee() {
    if (Game.level != null) {
        val start = Game.player.getTile()
        mapView(Game.level!!.map, start?.x ?: 0, start?.y ?: 0)
    } else mapView()
}

private suspend fun inspect() {
    clearActions()
    previousMenu = ::mainOptions
    leftButton.textContent = "Me"
    actions[leftButton] = { inspectBot(true) }
    rightButton.textContent = "Them"
    actions[rightButton] = { inspectBot(false) }
}

private suspend fun inspectBot(isPlayer: Boolean) {
    clearActions()
    val bot = if (isPlayer) Game.player.bot else Game.enemyBot!!
    val otherDiv: HTMLImageElement = if (isPlayer) el("enemy-bot") else el("player-bot")
    otherDiv.classList.add("hidden")
    previousMenu = ::inspect
    with(bot) {
        upButton.textContent = "Head: ${head.health}/${head.totalHealth}"
        downButton.textContent = "Core: ${core.health}/${core.totalHealth}"
        leftButton.textContent = "Left Arm: ${armLeft.health}/${armLeft.totalHealth}"
        rightButton.textContent = "Right Arm: ${armRight.health}/${armRight.totalHealth}"
    }

    val wrapper: HTMLDivElement = el("battle-wrapper-children")
    wrapper.append {
        with(bot) {
            div("battle-inspect-info") {
                div("battle-button") {
                    +"AP: ${head.ap}/${head.totalAP}"
                }
                div("battle-button") {
                    +"MP: ${mp}/${mp}"
                }
            }
        }
    }

    cleanup = {
        otherDiv.classList.remove("hidden")
        wrapper.innerHTML = ""
    }
}