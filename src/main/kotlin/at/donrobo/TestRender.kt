package at.donrobo

import at.donrobo.render.*
import java.io.File
import javax.imageio.ImageIO

fun main() {
    val output = File("output.png")
    val cam = Camera(Vector3(0f), Vector3(0.0f, 0.0f, 20.0f), Vector3(0f, 1f, 0f), 1f, 1f)
    val subtractionBox = Rounded(Box(Vector3(-1f, -3f, 16f), Vector3(5f), Vector3(0f, 1f, 0f), Vector3(0f), 0f), .7f)
    val world = World(
        listOf(
            Subtraction(
                Sphere(Vector3(0f, 0f, 20f), 5f, Vector3(1f, 1f, 1f), Vector3(0f), .5f),
                subtractionBox
            ),
            Subtraction(
                Sphere(Vector3(4f, 0f, 18f), 4f, Vector3(0f, 0f, 1f), Vector3(.5f), .5f),
                subtractionBox
            ),
            Repeat(Sphere(Vector3(10f), 5f, Vector3(1f, .3f, .3f), Vector3(.1f), .5f), Vector3(50f))
        ),
        listOf(
            Light(Vector3(10f, 5f, -10f), Vector3(1f), 5f)
        ),
        Vector3(0.1f)
    )
    val image = Raymarcher.render(cam, world, 500, 500)
    ImageIO.write(image, "PNG", output)
}

