package at.donrobo.render

import at.donrobo.clear
import kotlinx.coroutines.*
import java.awt.Color
import java.awt.image.BufferedImage
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow

const val epsilon: Float = 0.001f


data class Ray(val origin: Vector3, val direction: Vector3)

data class Camera(val origin: Vector3, val lookAt: Vector3, val up: Vector3, val f: Float, val ws: Float) {
    fun move(moveBy: Vector3): Camera = Camera(origin + moveBy, lookAt + moveBy, up, f, ws)

    val w: Vector3 = (origin - lookAt) / (origin - lookAt).length
    val u = ((up * -1f) cross w) / ((up * -1f) cross w).length
    val v = w cross u
}

@ObsoleteCoroutinesApi
@ExperimentalCoroutinesApi
object Raymarcher {

    private fun shade(
        world: World,
        ray: Ray,
        hit: TraceResult
    ): Int {
        if (hit.position != null && hit.worldObject != null && hit.hitSomething) {
            val normal = world.estimateNormal(hit.position)

            var color = world.ambientLight * hit.worldObject.color * (1 - world.calculateAmbientOcclusion(
                hit.position,
                normal,
                5
            ))

            world.lights.forEach { light ->
                val lightDirection = (light.position - hit.position).normalized
                val lightDistance = (light.position - hit.position).length
                val hitByLight: Float = 1 -
                        world.calculateOcclusion(
                            Ray(hit.position, lightDirection),
                            light.size,
                            0.1f,
                            lightDistance
                        )
//                 val hitByLight=1f

                if (hitByLight > 0 && !hitByLight.isNaN()) {
                    val cosAngle = lightDirection dot normal
                    val cd = hit.worldObject.color * max(cosAngle, 0f)
                    val cs =
                        if (cosAngle >= 0) {
                            val r = reflect(lightDirection * -1f, normal)
                            val cosAngleR = (ray.direction * -1f) dot r

                            val ks = hit.worldObject.specular
                            val m = hit.worldObject.m
                            (max(cosAngleR, 0f).pow(m)) * ks
                        } else {
                            Vector3(0f)
                        }


                    if (hitByLight in 0.0..1.0 && !hitByLight.isNaN()) {
                        color += cd * light.color * hitByLight
                        color += cs * light.color * hitByLight
                    }
                }
            }
            return color.color.rgb

        }
        return Vector3(0f).color.rgb
    }

    fun render(camera: Camera, world: World, width: Int, height: Int): BufferedImage {
        val image = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)

        image.clear(Color(0, 0, 0, 0))

        val a = width.toFloat() / height.toFloat()
        val hs = a * camera.ws
        val pixelSize = camera.ws / width

        val rgbArray = IntArray(width * height)

        newSingleThreadContext("Render thread").use { dispatcher ->

            val jobs =
                (0 until rgbArray.size).map { n ->
                    GlobalScope.async(dispatcher) {
                        val x = n % width
                        val y = n / width
                        val rayTarget =
                            Vector3(x * pixelSize - camera.ws / 2f, y * pixelSize - hs / 2f, -camera.f).translateInto(
                                camera.origin,
                                camera.w,
                                camera.u,
                                camera.v
                            )
                        val direction = (rayTarget - camera.origin).normalized
                        val ray = Ray(camera.origin, direction)

                        val result = world.hitWorld(ray, camera.f)
                        val color: Int =
                            if (result.hitSomething) {
                                shade(world, ray, result)
                            } else {
                                Color(0, 0, 0, 0).rgb
                            }

                        color
                    }
                }

            runBlocking {
                jobs.forEachIndexed { i, job ->
                    rgbArray[i] = job.await()
                }
            }
            image.setRGB(0, 0, width, height, rgbArray, 0, width)
        }
        return image
    }

}

private val Vector3.color: Color
    get() = Color(max(0f, min(1f, x)), max(0f, min(1f, y)), max(0f, min(1f, z)))

