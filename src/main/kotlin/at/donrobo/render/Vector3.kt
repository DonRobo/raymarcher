package at.donrobo.render

import kotlin.math.*

data class Vector3(val x: Float, val y: Float, val z: Float) {
    constructor(value: Float) : this(value, value, value)

    val normalized: Vector3 get() = this / length

    val length: Float by lazy {
        sqrt(x * x + y * y + z * z)
    }

    fun distanceTo(vec: Vector3): Float {
        return (this - vec).length //TODO optimize
    }

    operator fun minus(vec: Vector3): Vector3 {
        return Vector3(x - vec.x, y - vec.y, z - vec.z)
    }

    operator fun times(scalar: Float): Vector3 {
        return Vector3(x * scalar, y * scalar, z * scalar)
    }

    operator fun div(scalar: Float): Vector3 {
        return Vector3(x / scalar, y / scalar, z / scalar)
    }

    operator fun plus(vec: Vector3): Vector3 {
        return Vector3(x + vec.x, y + vec.y, z + vec.z)
    }

    infix fun cross(vec: Vector3): Vector3 {
        return Vector3(
            this.y * vec.z - this.z * vec.y,
            this.z * vec.x - this.x * vec.z,
            this.x * vec.y - this.y * vec.x
        )
    }

    infix fun dot(vec: Vector3): Float = this.x * vec.x + this.y * vec.y + this.z * vec.z

    fun translateInto(origin: Vector3, w: Vector3, u: Vector3, v: Vector3): Vector3 {
        return origin + u * x + v * y + w * z
    }

    operator fun times(vec: Vector3): Vector3 = Vector3(this.x * vec.x, this.y * vec.y, this.z * vec.z)

}

fun abs(vec: Vector3): Vector3 = Vector3(abs(vec.x), abs(vec.y), abs(vec.z))
fun max(vec: Vector3, scalar: Float) = Vector3(max(vec.x, scalar), max(vec.y, scalar), max(vec.z, scalar))
fun min(vec: Vector3, scalar: Float) = Vector3(min(vec.x, scalar), min(vec.y, scalar), min(vec.z, scalar))

private infix fun Float.mod(f2: Float): Float = this - f2 * floor(this / f2)

fun mod(vec1: Vector3, vec2: Vector3) = Vector3(vec1.x mod vec2.x, vec1.y mod vec2.y, vec1.z mod vec2.z)

operator fun Float.times(vec: Vector3): Vector3 = vec * this

fun reflect(dir: Vector3, normal: Vector3): Vector3 {
    return dir - 2 * (dir dot normal) * normal
}
