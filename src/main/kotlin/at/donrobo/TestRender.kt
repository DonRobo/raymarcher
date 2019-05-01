package at.donrobo

import at.donrobo.render.Camera
import at.donrobo.render.Raymarcher
import at.donrobo.render.Vector3D
import at.donrobo.render.World
import java.io.File
import javax.imageio.ImageIO

fun main() {
    val output = File("output.png")
    val cam = Camera(Vector3D(0f), Vector3D(0.0f, 0.0f, 20.0f), Vector3D(0f, 1f, 0f), 1f, 1f)
    val world = World()
    val image = Raymarcher.render(cam, world, 500, 500)
    ImageIO.write(image, "PNG", output)
}

