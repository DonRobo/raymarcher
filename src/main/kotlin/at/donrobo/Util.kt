package at.donrobo

import java.awt.Color
import java.awt.image.BufferedImage

fun BufferedImage.clear(color: Color) {
    val graphics2D = createGraphics()
    graphics2D.background = color
    graphics2D.clearRect(0, 0, width, height)
}
