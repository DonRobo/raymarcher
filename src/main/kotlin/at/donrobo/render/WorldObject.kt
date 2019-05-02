package at.donrobo.render

import kotlin.math.max
import kotlin.math.min

interface WorldObject {
    fun distanceTo(point: Vector3): Float
    val color: Vector3
    val specular: Vector3
    val m: Float

}

data class Sphere(
    val position: Vector3, val radius: Float,
    override val color: Vector3,
    override val specular: Vector3,
    override val m: Float
) : WorldObject {
    override fun distanceTo(point: Vector3): Float = position.distanceTo(point) - radius

}

data class Box(
    val position: Vector3, val size: Vector3,
    override val color: Vector3,
    override val specular: Vector3,
    override val m: Float
) : WorldObject {
    override fun distanceTo(point: Vector3): Float {
        val d = abs(point - position) - size

        return max(d, 0f).length + min(max(d.x, max(d.y, d.z)), 0f)
    }
}

data class Subtraction(val obj1: WorldObject, val obj2: WorldObject) : WorldObject {
    override fun distanceTo(point: Vector3): Float = max(obj1.distanceTo(point), -obj2.distanceTo(point))

    override val color: Vector3
        get() = obj1.color
    override val specular: Vector3
        get() = obj1.specular
    override val m: Float
        get() = obj1.m

}

data class Rounded(val obj: WorldObject, val radius: Float) : WorldObject {
    override fun distanceTo(point: Vector3): Float = obj.distanceTo(point) - radius

    override val color: Vector3
        get() = obj.color
    override val specular: Vector3
        get() = obj.specular
    override val m: Float
        get() = obj.m

}

data class Repeat(val obj: WorldObject, val repetition: Vector3) : WorldObject {
    override fun distanceTo(point: Vector3): Float {
        val q = mod(point, repetition) - repetition * .5f

        return obj.distanceTo(q)
    }

    override val color: Vector3
        get() = obj.color
    override val specular: Vector3
        get() = obj.specular
    override val m: Float
        get() = obj.m

}