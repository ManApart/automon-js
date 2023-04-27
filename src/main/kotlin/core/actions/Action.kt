package core.actions

import core.Bot
import core.Part

abstract class Action(val name: String, val type: ActionResult, val cost: Int, val range: IntRange) {

    open fun use(parent: Bot, target: Part) {}
}