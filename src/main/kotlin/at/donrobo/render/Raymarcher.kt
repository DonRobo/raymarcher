package at.donrobo.render

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import java.awt.Color
import java.awt.image.BufferedImage
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

const val epsilon: Float = 0.001f

data class Vector3D(val x: Float, val y: Float, val z: Float) {
    constructor(value: Float) : this(value, value, value)

    val normalized: Vector3D get() = this / length

    val length: Float by lazy {
        sqrt(x * x + y * y + z * z)
    }

    fun distanceTo(vec: Vector3D): Float {
        return (this - vec).length //TODO optimize
    }

    operator fun minus(vec: Vector3D): Vector3D {
        return Vector3D(x - vec.x, y - vec.y, z - vec.z)
    }

    operator fun times(scalar: Float): Vector3D {
        return Vector3D(x * scalar, y * scalar, z * scalar)
    }

    operator fun div(scalar: Float): Vector3D {
        return Vector3D(x / scalar, y / scalar, z / scalar)
    }

    operator fun plus(vec: Vector3D): Vector3D {
        return Vector3D(x + vec.x, y + vec.y, z + vec.z)
    }

    infix fun cross(vec: Vector3D): Vector3D {
        return Vector3D(
            this.y * vec.z - this.z * vec.y,
            this.z * vec.x - this.x * vec.z,
            this.x * vec.y - this.y * vec.x
        )
    }

    fun translateInto(origin: Vector3D, w: Vector3D, u: Vector3D, v: Vector3D): Vector3D {
        return origin + u * x + v * y + w * z
    }
}

data class Ray(val origin: Vector3D, val direction: Vector3D)

data class TraceResult(val hitSomething: Boolean, val closestApproach: Float)

class World {

    //TODO do for real
    private val sphereOrigin = Vector3D(0.0f, 0.0f, 20.0f)
    private val sphereRadius = 5f

    fun distanceToWorld(point: Vector3D): Float {
        return max(sphereOrigin.distanceTo(point) - sphereRadius, 0f)
    }

    fun hitWorld(ray: Ray, renderDistance: Float = 1000f): TraceResult {
        var closest = Float.POSITIVE_INFINITY

        var moved = 0f
        do {
            val currentPoint = ray.origin + ray.direction * moved
            val distance = distanceToWorld(currentPoint)
            closest = min(distance, closest)
            moved += distance

            if (distance < epsilon) {
                return TraceResult(true, closest)
            }

        } while (distance > epsilon && moved <= renderDistance)

        return TraceResult(false, closest)
    }
}

data class Camera(val origin: Vector3D, val lookAt: Vector3D, val up: Vector3D, val f: Float, val ws: Float) {
    val w: Vector3D = (origin - lookAt) / (origin - lookAt).length
    val u = (up cross w) / (up cross w).length
    val v = w cross u
}

object Raymarcher {

    fun render(camera: Camera, world: World, width: Int, height: Int): BufferedImage {
        val image = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)

        image.clear(Color(0, 0, 0, 0))

        val a = width.toFloat() / height.toFloat()
        val hs = a * camera.ws
        val pixelSize = camera.ws / width

        val rgbArray = IntArray(width * height)

        val jobs = //ArrayList<Job>(width * height)

            (0 until rgbArray.size).map { n ->
                GlobalScope.async {
                    val x = n % width
                    val y = n / width
                    val rayTarget =
                        Vector3D(x * pixelSize - camera.ws / 2f, y * pixelSize - hs / 2f, -camera.f).translateInto(
                            camera.origin,
                            camera.w,
                            camera.u,
                            camera.v
                        )
                    val direction = (rayTarget - Vector3D(0f)).normalized //TODO translate into other coordinate system
                    val ray = Ray(camera.origin, direction)

                    val result = world.hitWorld(ray)
                    val color: Int =
                        if (result.hitSomething) {
                            val intensity = 1f
                            Color(intensity, intensity, intensity, 1f).rgb
                        } else {
                            val intensity = 1 - (result.closestApproach / 2f)
                            Color(1f, 0.9f, 0.4f, max(0f, intensity)).rgb
                        }
//                image.setRGB(x, y, color)
                    color
                }
            }

        runBlocking {
            jobs.forEachIndexed { i, job ->
                rgbArray[i] = job.await()
            }
        }
        image.setRGB(0, 0, width, height, rgbArray, 0, width)

        return image
    }

}

private fun BufferedImage.clear(color: Color) {
    val graphics2D = createGraphics()
    graphics2D.background = color
    graphics2D.clearRect(0, 0, width, height)
}
