import kotlinx.browser.document
import org.w3c.dom.events.Event
import org.w3c.dom.events.KeyboardEvent

class Controller {
    init {
        document.addEventListener("keydown", ::keyDown)
        document.addEventListener("keyup", ::keyUp)
    }

    var up: Boolean = false
    var down: Boolean = false
    var left: Boolean = false
    var right: Boolean = false

    fun keyDown(event: Event) {
        if (event.defaultPrevented) return
        if (event !is KeyboardEvent) return
        when (event.key) {
            "ArrowUp" -> up = true
            "ArrowDown" -> down = true
            "ArrowLeft" -> left = true
            "ArrowRight" -> right = true
            else -> println("Pressed ${event.key}")
        }

        event.preventDefault()
    }

    fun keyUp(event: Event) {
        if (event.defaultPrevented) return
        if (event !is KeyboardEvent) return
        when (event.key) {
            "ArrowUp" -> up = false
            "ArrowDown" -> down = false
            "ArrowLeft" -> left = false
            "ArrowRight" -> right = false
            else -> println("Released ${event.key}")
        }

        event.preventDefault()
    }
}