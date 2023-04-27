package ui

import Game
import clearSections
import core.Bot
import core.Part
import core.Terrain
import data.NOTHING
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

suspend fun battleView(terrain: Terrain, enemy: Bot) {
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
    createButton(upButton, "Inspect", ::inspect)
    createButton(leftButton, "Flee", ::flee)
    createButton(rightButton, "Action", ::action)
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
    createButton(leftButton, "Me") { inspectBot(true) }
    createButton(rightButton, "Them") { inspectBot(false) }
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

private suspend fun action() {
    clearActions()
    previousMenu = ::mainOptions
    with(Game.player.bot) {
        createActionButton(head, upButton)
        createActionButton(armRight, rightButton)
        createActionButton(armLeft, leftButton)
        createActionButton(core, downButton)
    }
}

private fun createActionButton(part: Part, button: HTMLDivElement) {
    if (part.action != NOTHING) createButton(button, part.action.name) { aim(part) }
}


private suspend fun aim(part: Part) {
    clearActions()
    previousMenu = ::action

    with(Game.enemyBot!!) {
        createButton(upButton, head.name) { useAction(part, head) }
        createButton(leftButton, armLeft.name) { useAction(part, armLeft) }
        createButton(downButton, armRight.name) { useAction(part, armRight) }
        createButton(rightButton, core.name) { useAction(part, core) }
    }
}

private suspend fun useAction(part: Part, target: Part){
    //TODO - use / prevent AP as needed
    part.action.use(Game.player.bot, target)
    mainOptions()
}

private fun createButton(button: HTMLDivElement, name: String, action: suspend () -> Unit) {
    button.textContent = name
    actions[button] = action
}