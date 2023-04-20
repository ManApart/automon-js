package ui

import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.Image

data class Sprite(val tileSheet: Image, val frameWidth: Int, val frameHeight: Int, val framesPerChange: Int, val animations: Map<String, Animation>) {
    var x: Double = 0.0
    var y: Double = 0.0
    private val fw = frameWidth.toDouble()
    private val fh = frameHeight.toDouble()
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

    fun draw(ctx: CanvasRenderingContext2D) {
        val (sx, sy) = animation.current(fw, fh)
        ctx.drawImage(tileSheet, sx, sy, fw, fh, x, y, fw, fh)
    }
}

data class FramePosition(val col: Int, val row: Int)
infix fun Int.by(row: Int) = FramePosition(this, row)

data class Animation(val name: String, val steps: List<FramePosition>, var currentStep: Int = 0) {

    fun current(frameWidth: Double, frameHeight: Double): Pair<Double, Double> {
        return steps[currentStep].let { (x, y) ->
            x*frameWidth to y * frameHeight
        }
    }
}