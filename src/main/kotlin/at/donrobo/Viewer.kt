package at.donrobo

import at.donrobo.render.Camera
import at.donrobo.render.Raymarcher
import at.donrobo.render.World
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.awt.Color
import java.awt.Dimension
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.event.ComponentEvent
import java.awt.event.ComponentListener
import java.awt.image.BufferedImage
import javax.swing.JComponent
import javax.swing.JFrame

class Viewer(val world: World, val renderWidth: Int, val renderHeight: Int, var camera: Camera) : JComponent() {

    private var currentFrame: BufferedImage = BufferedImage(renderWidth, renderHeight, BufferedImage.TYPE_INT_RGB).run {
        this.clear(Color(255, 0, 255))
        this
    }
    private var renderJob: Job? = null

    init {
        addComponentListener(object : ComponentListener {
            override fun componentMoved(e: ComponentEvent?) {
            }

            override fun componentResized(e: ComponentEvent?) {
                startComputation()
            }

            override fun componentHidden(e: ComponentEvent?) {
                stopComputation()
            }

            override fun componentShown(e: ComponentEvent?) {
                startComputation()
            }

        })
    }

    private fun startComputation() {
        if (renderJob?.isActive != true)
            renderJob = GlobalScope.launch {
                println("Starting to render")
                while (isActive) {
                    renderFrame()
                }
                println("Stopping to render")
            }
    }

    private fun stopComputation() {
        renderJob?.cancel()
    }

    fun startRendering() {
        val frame = JFrame("Raymarcher")
        frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        frame.add(this)
        frame.pack()
        frame.isVisible = true
        this.isVisible = true
    }

    override fun paint(g: Graphics?) {
        if (g != null && g is Graphics2D) {
            g.background = Color(80, 180, 255)
            g.clearRect(0, 0, width, height)
            g.drawImage(currentFrame, 0, 0, null)
        }
    }

    private fun renderFrame() {
        currentFrame = Raymarcher.render(camera, world, renderWidth, renderHeight)
//        camera=camera.move(Vector3(0f, 0f,3f))
        repaint()
    }

    override fun getPreferredSize(): Dimension = Dimension(renderWidth, renderHeight)

}