package at.donrobo

import at.donrobo.render.Triangle
import at.donrobo.render.Vector3
import hall.collin.christopher.stl4j.STLParser
import hall.collin.christopher.stl4j.Vec3d
import java.io.File

object StlReader {
    //    fun readStl(
//        file: File,
//        color: Vector3,
//        specular: Vector3,
//        m: Float
//    ): List<Triangle> = Stl.fromFile(file.absolutePath).triangles()?.map { triangle ->
//        val vertices = triangle.vertices()?.map { v ->
//            Vector3(v.x(), v.y(), v.z())
//        }
//        if (vertices != null)
//            Triangle(vertices[0], vertices[1], vertices[2], color, specular, m)
//        else
//            null
//    }?.filterNotNull() ?: emptyList()
    fun readStl(
        file: File,
        color: Vector3,
        specular: Vector3,
        m: Float
    ): List<Triangle> {
        return STLParser.parseSTLFile(file.toPath()).map {
            Triangle(
                it.vertices[0].toVector3(),
                it.vertices[1].toVector3(),
                it.vertices[2].toVector3(),
                color, specular, m
            )
        }
    }
}

private fun Vec3d.toVector3(): Vector3 = Vector3(this.x.toFloat(), this.y.toFloat(), this.z.toFloat())
