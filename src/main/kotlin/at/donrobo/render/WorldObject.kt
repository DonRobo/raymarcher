package at.donrobo.render

import kotlin.math.max
import kotlin.math.min
import kotlin.math.sign
import kotlin.math.sqrt

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

data class Triangle(
    val v1: Vector3, val v2: Vector3, val v3: Vector3,
    override val color: Vector3,
    override val specular: Vector3,
    override val m: Float
) : WorldObject {
    override fun distanceTo(point: Vector3): Float {
        fun dot(vec1: Vector3, vec2: Vector3): Float = vec1 dot vec2
        fun dot2(vec: Vector3): Float = vec dot vec
        fun cross(vec1: Vector3, vec2: Vector3): Vector3 = vec1 cross vec2
        fun clamp(value: Float, minV: Float, maxV: Float): Float = max(min(value, maxV), minV)

        val ba = v2 - v1
        val pa = point - v1
        val cb = v3 - v2
        val pb = point - v2
        val ac = v1 - v3
        val pc = point - v3
        val nor = cross(ba, ac)

        return sqrt(
            if (sign(dot(cross(ba, nor), pa)) +
                sign(dot(cross(cb, nor), pb)) +
                sign(dot(cross(ac, nor), pc)) < 2.0
            )
                min(
                    min(
                        dot2(ba * clamp(dot(ba, pa) / dot2(ba), 0.0f, 1.0f) - pa),
                        dot2(cb * clamp(dot(cb, pb) / dot2(cb), 0.0f, 1.0f) - pb)
                    ),
                    dot2(ac * clamp(dot(ac, pc) / dot2(ac), 0.0f, 1.0f) - pc)
                )
            else
                dot(nor, pa) * dot(nor, pa) / dot2(nor)
        )
    }

}