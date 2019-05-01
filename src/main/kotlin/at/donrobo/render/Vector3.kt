package at.donrobo.render

import kotlin.math.sqrt

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

operator fun Float.times(vec: Vector3): Vector3 = vec * this

fun reflect(dir: Vector3, normal: Vector3): Vector3 {
    return dir - 2 * (dir dot normal) * normal
}
