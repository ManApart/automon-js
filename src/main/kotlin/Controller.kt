import kotlinx.browser.document
import org.w3c.dom.events.Event
import org.w3c.dom.events.KeyboardEvent

class ButtonSubscription(val key: String, val action: () -> Unit)

class Controller {
    private var interactedWith = false

    init {
        document.addEventListener("keydown", ::keyDown)
        document.addEventListener("keyup", ::keyUp)
    }

    private val subscriptions = mutableMapOf<String, MutableMap<String, ButtonSubscription>>()

    fun subscribe(owner: String, subscription: ButtonSubscription) {
        subscriptions.getOrPut(owner) { mutableMapOf() }[subscription.key] = subscription
    }

    fun unSubscribe(owner: String, key: String) {
        subscriptions[owner]?.remove(key)
    }

    var up: Boolean = false
    var down: Boolean = false
    var left: Boolean = false
    var right: Boolean = false

    fun keyDown(event: Event) {
        if (event.defaultPrevented) return
        if (event !is KeyboardEvent) return

        if (!interactedWith) {
            interactedWith = true
            Game.firstPlayerInteraction()
        }

        var keyCaptured = true
        when (event.key) {
            "ArrowUp" -> up = true
            "ArrowDown" -> down = true
            "ArrowLeft" -> left = true
            "ArrowRight" -> right = true
            else -> {
                keyCaptured = false
                println("Pressed ${event.key}")
            }
        }
        if (keyCaptured) event.preventDefault()
    }

    fun keyUp(event: Event) {
        if (event.defaultPrevented) return
        if (event !is KeyboardEvent) return

        var keyCaptured = true
        when (event.key) {
            "ArrowUp" -> up = false
            "ArrowDown" -> down = false
            "ArrowLeft" -> left = false
            "ArrowRight" -> right = false
            else -> {
                keyCaptured = false
                println("Released ${event.key}")
            }
        }

        val matchingSubscriptions = subscriptions.values.flatMap { it.values }.filter { it.key == event.key }
        keyCaptured = keyCaptured || matchingSubscriptions.isNotEmpty()
        matchingSubscriptions.forEach { it.action() }

        if (keyCaptured) event.preventDefault()
    }
}