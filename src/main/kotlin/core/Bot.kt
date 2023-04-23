package core

import core.actions.Action
import core.actions.ActionResult
import data.arms
import data.cores
import data.heads

class Bot(
    var head: Head = heads["Standard"]!!,
    var core: Core = cores["Standard"]!!,
    var armRight: Arm = arms["Standard Right"]!!,
    var armLeft: Arm = arms["Standard Left"]!!
) {
    var mp = 0

    fun getPart(direction: Direction): Part {
        return when (direction) {
            Direction.UP -> head
            Direction.RIGHT -> armRight
            Direction.LEFT -> armLeft
            Direction.DOWN -> core
        }
    }

    fun takeAction(action: Action, target: Part, battle: Battle): ActionResult {
        if (action.cost > head.ap) {
            return ActionResult.LOW_AP
        }

        println("Player does ${action.name}")
        head.ap -= action.cost

        if (!action.range.contains(battle.distance)) {
            return ActionResult.MISS
        }

        action.use(this, target, battle)

        return action.type
    }
}