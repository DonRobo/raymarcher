package at.donrobo.render

import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

data class ObjectDistance(val obj: WorldObject?, val distance: Float)

data class TraceResult(
    val hitSomething: Boolean,
    val closestApproach: Float,
    val worldObject: WorldObject?,
    val position: Vector3?
)

data class Light(val position: Vector3, val color: Vector3, val size: Float)

class World(val objects: List<WorldObject>, val lights: List<Light>, val ambientLight: Vector3) {

    fun estimateNormal(point: Vector3): Vector3 =
        Vector3(
            distanceToWorld(Vector3(point.x + epsilon, point.y, point.z)).distance -
                    distanceToWorld(
                        Vector3(
                            point.x - epsilon,
                            point.y,
                            point.z
                        )
                    ).distance,
            distanceToWorld(Vector3(point.x, point.y + epsilon, point.z)).distance -
                    distanceToWorld(
                        Vector3(
                            point.x,
                            point.y - epsilon,
                            point.z
                        )
                    ).distance,
            distanceToWorld(Vector3(point.x, point.y, point.z + epsilon)).distance -
                    distanceToWorld(
                        Vector3(
                            point.x,
                            point.y,
                            point.z - epsilon
                        )
                    ).distance
        ).normalized

    fun distanceToWorld(point: Vector3): ObjectDistance {
        return objects.map {
            ObjectDistance(
                it,
                it.distanceTo(point)
            )
        }.minBy { it.distance } ?: ObjectDistance(
            null,
            Float.POSITIVE_INFINITY
        )
    }

    fun hitWorld(ray: Ray, minT: Float, maxT: Float = 1000f): TraceResult {
        var closest = Float.POSITIVE_INFINITY
        var closestObject: WorldObject? = null
        var closestPosition: Vector3? = null

        var t = minT
        do {
            val currentPoint = ray.origin + ray.direction * t
            val objDistance = distanceToWorld(currentPoint)
            val distance = objDistance.distance
            if (closest > distance) {
                closest = distance
                closestObject = objDistance.obj
                closestPosition = currentPoint
            }
            t += distance

            if (distance < epsilon) {
                return TraceResult(true, closest, objDistance.obj, currentPoint)
            }

        } while (t <= maxT)

        return TraceResult(false, closest, closestObject, closestPosition)
    }

    fun calculateOcclusion(ray: Ray, k: Float, minT: Float, maxT: Float): Float {
        var visibility = 1.0f
        var prevDist = Float.POSITIVE_INFINITY

        var t = minT
        var i = 0
        do {
            val currentPoint = ray.origin + ray.direction * t
            val objDistance = distanceToWorld(currentPoint)
            val distance = objDistance.distance

            val y = distance * distance / (2f * prevDist)
            val d = sqrt(distance * distance - y * y)
            visibility = min(visibility, k * d / max(0f, t - y))

            prevDist = distance
            t += distance

        } while (visibility > epsilon && t <= maxT && i++ < 32)

        return 1f - min(1f, max(0f, visibility))
    }

    fun calculateAmbientOcclusion(point: Vector3, normal: Vector3, samples: Int): Float {
        var occ = 0f
        var sca = .4f
        for (i in 1..samples) {
            val h = i * 0.3f
            val d = distanceToWorld(point + normal * h).distance
            occ += (h - d) * sca
            sca *= 0.95f
        }
        return min(1f, max(0f, occ))
    }
}