package at.donrobo

import at.donrobo.render.*
import java.io.File

fun main() {
//    val output = File("output.png")
    val cam = Camera(Vector3(233f, 51f, 50f), Vector3(233f, 51f, 4f), Vector3(0f, 1f, 0f), 1f, 1f)
    val subtractionBox = Rounded(Box(Vector3(-1f, -3f, 16f), Vector3(5f), Vector3(0f, 1f, 0f), Vector3(0f), 0f), .7f)
//    val objects = listOf(
//        Subtraction(
//            Sphere(Vector3(0f, 0f, 20f), 5f, Vector3(1f, 1f, 1f), Vector3(0f), .5f),
//            subtractionBox
//        ),
//        Subtraction(
//            Sphere(Vector3(4f, 0f, 18f), 4f, Vector3(0f, 0f, 1f), Vector3(.5f), .5f),
//            subtractionBox
//        ),
//        Repeat(Sphere(Vector3(10f), 5f, Vector3(1f, .3f, .3f), Vector3(.1f), .5f), Vector3(50f))
//    )
    val objects = StlReader.readStl(File("test.stl"), Vector3(.9f), Vector3(.1f), .3f)
    val world = World(
        objects,
        listOf(
            Light(Vector3(10f, 5f, -10f), Vector3(1f), 5f)
        ),
        Vector3(0.7f)
    )
//    val image = Raymarcher.render(cam, world, 3000, 3000)
//    ImageIO.write(image, "PNG", output)
    val viewer = Viewer(world, 50, 50, cam)
    viewer.startRendering()
}

