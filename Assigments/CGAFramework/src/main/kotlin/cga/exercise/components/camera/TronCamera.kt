package cga.exercise.components.camera

import cga.exercise.components.geometry.Transformable
import cga.exercise.components.shader.ShaderProgram
import org.joml.Matrix4f

/***
 * fieldOfView = vertikaler Oeffnungswinkel der Kamera [90 Grad in Radiant, Angabe in Float]
 * Seitenverhältnis (aspect ratio) --- (indirekt) horizontaler Oeffnungswinkel [16.0f/9.0f]
 * Near Plane --- Entfernung der Near Plane zur Kamera [0.1f, quasi direkt vor der Linse]
Far Plane --- Entfernung der Far Plane zur Kamera [100.0f]

 */
class TronCamera(var fieldOfView: Float = org.joml.Math.toRadians(90.0f), var seitenverhaeltnis: Float = 16.0f, var nearPlane: Float = 0.1f, var farPlan: Float= 100.0f) : ICamera, Transformable() {

    /*
     * Calculate the ViewMatrix according the lecture
     * values needed:
     *  - eye –> the position of the camera
     *  - center –> the point in space to look at
     *  - up –> the direction of 'up'
     */
    override fun getCalculateViewMatrix(): Matrix4f {
        TODO("Not yet implemented")
    }

    /*
     * Calculate the ProjectionMatrix according the lecture
     * values needed:
     *  - fov – the vertical field of view in radians (must be greater than zero and less than PI)
     *  - aspect – the aspect ratio (i.e. width / height; must be greater than zero)
     *  - zNear – near clipping plane distance
     *  - zFar – far clipping plane distance
     */
    override fun getCalculateProjectionMatrix(): Matrix4f {
        TODO("Not yet implemented")
    }

    override fun bind(shader: ShaderProgram) {
        TODO("Not yet implemented")
    }
}