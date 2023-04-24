import kotlinx.browser.document
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.w3c.dom.events.Event
import org.w3c.dom.events.KeyboardEvent

class ButtonSubscription(val key: String, val action: suspend () -> Unit)

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

    fun unSubscribe() {
        subscriptions.clear()
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
            else -> keyCaptured = false
        }

        val matchingSubscriptions = subscriptions.values.flatMap { it.values }.filter { it.key == event.key }
        keyCaptured = keyCaptured || matchingSubscriptions.isNotEmpty()

        if (keyCaptured) {
            event.preventDefault()
        } else {
            println("Pressed ${event.key}")
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun keyUp(event: Event) {
        if (event.defaultPrevented) return
        if (event !is KeyboardEvent) return
        GlobalScope.launch {
            var keyCaptured = true
            when (event.key) {
                "ArrowUp" -> up = false
                "ArrowDown" -> down = false
                "ArrowLeft" -> left = false
                "ArrowRight" -> right = false
                else -> keyCaptured = false
            }

            val matchingSubscriptions = subscriptions.values.flatMap { it.values }.filter { it.key == event.key }
            keyCaptured = keyCaptured || matchingSubscriptions.isNotEmpty()
            matchingSubscriptions.forEach { it.action() }
            if (keyCaptured) {
                event.preventDefault()
            } else {
                println("Released ${event.key}")
            }
        }
    }
}

class SubscriptionBuilder(private val owner: String) {
    private var subs = mutableListOf<ButtonSubscription>()


    infix fun String.to( handler: suspend () -> Unit){
        subs.add(ButtonSubscription(this, handler))
    }

    fun applyTo(controller: Controller) {
        subs.forEach { sub ->
            controller.subscribe(owner, sub)
        }
    }
}

fun sub(owner: String, customizer: SubscriptionBuilder.() -> Unit) {
    SubscriptionBuilder(owner).apply(customizer).applyTo(Game.controller)
}