package at.donrobo

import at.donrobo.render.*
import java.io.File
import javax.imageio.ImageIO

fun main() {
    val output = File("output.png")
    val cam = Camera(Vector3(0f), Vector3(0.0f, 0.0f, 20.0f), Vector3(0f, 1f, 0f), 1f, 1f)
    val world = World(
        listOf(
            Sphere(Vector3(0f, 0f, 20f), 5f, Vector3(1f, 1f, 1f), Vector3(0f), .5f),
            Sphere(Vector3(4f, 0f, 18f), 4f, Vector3(0f, 0f, 1f), Vector3(.5f), .5f),
            Sphere(Vector3(-13f, 14f, 45f), 5f, Vector3(1f, .3f, .3f), Vector3(.1f), .5f)
        ),
        listOf(
            Light(Vector3(50f, 10f, 0f), Vector3(1f), 5f)
        ),
        Vector3(0.1f)
    )
    val image = Raymarcher.render(cam, world, 500, 500)
    ImageIO.write(image, "PNG", output)
}

