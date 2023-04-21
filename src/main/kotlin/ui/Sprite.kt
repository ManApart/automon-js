package ui

import loadImage
import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.Image

fun anim(name: String, vararg steps: FramePosition): Animation {
    return Animation(name, steps.toList())
}

suspend fun sprite(tileSheet: String, frameWidth: Int, frameHeight: Int, framesPerChange: Int, vararg animations: Animation): Sprite{
    return Sprite(loadImage(tileSheet), frameWidth.toDouble(), frameHeight.toDouble(),framesPerChange, animations.associateBy { it.name })
}

data class Sprite(val tileSheet: Image, val frameWidth: Double, val frameHeight: Double, val framesPerChange: Int, val animations: Map<String, Animation>) {

    private var animation = animations.values.first()
    private var frameCount = 0

    fun setAnimation(name: String) {
        animation = animations[name]!!
        frameCount = 0
    }

    fun advanceAnimation() {
        frameCount++
        if (frameCount >= framesPerChange){
            frameCount = 0
            animation.currentStep++
            if (animation.currentStep >= animation.steps.size){
                animation.currentStep = 0
            }
        }
    }

    fun draw(ctx: CanvasRenderingContext2D, x: Double, y: Double) {
        val (sx, sy) = animation.current(frameWidth, frameHeight)
        ctx.drawImage(tileSheet, sx, sy, frameWidth, frameHeight, x, y, frameWidth, frameHeight)
    }
}

data class FramePosition(val col: Int, val row: Int)
infix fun Int.x(row: Int) = FramePosition(this, row)

data class Animation(val name: String, val steps: List<FramePosition>, var currentStep: Int = 0) {

    fun current(frameWidth: Double, frameHeight: Double): Pair<Double, Double> {
        return steps[currentStep].let { (x, y) ->
            x*frameWidth to y * frameHeight
        }
    }
}