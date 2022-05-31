package cga.exercise.components.geometry

import org.joml.Matrix4f
import org.joml.Vector3f

open class Transformable(private var modelMatrix: Matrix4f = Matrix4f(), var parent: Transformable? = null) {
    /**
     * Returns copy of object model matrix
     * @return modelMatrix
     */
    override fun getModelMatrix(): Matrix4f {
        return var modelMatrix = getModelMatrix()
        //throw NotImplementedError()
    }

    /**
     * Returns multiplication of world and object model matrices.
     * Multiplication has to be recursive for all parents.
     * Hint: scene graph
     * @return world modelMatrix
     */
    override fun getWorldModelMatrix(): Matrix4f {
        return var tempMatrix = getModelMatrix().mul(getWorldModelMatrix())
        //throw NotImplementedError()
    }

    /**
     * Rotates object around its own origin.
     * @param pitch radiant angle around x-axis ccw
     * @param yaw radiant angle around y-axis ccw
     * @param roll radiant angle around z-axis ccw
     */
    override fun rotate(pitch: Float, yaw: Float, roll: Float) {
        var angel = AxisAngle4d(pitch,yaw,roll)
        modelMatrix.rotate(angel)
        //throw NotImplementedError()
    }

    /**
     * Rotates object around given rotation center.
     * @param pitch radiant angle around x-axis ccw
     * @param yaw radiant angle around y-axis ccw
     * @param roll radiant angle around z-axis ccw
     * @param altMidpoint rotation center
     */
    override fun rotateAroundPoint(pitch: Float, yaw: Float, roll: Float, altMidpoint: Vector3f) {
        // TODO implement
        throw NotImplementedError()
    }

    /**
     * Translates object based on its own coordinate system.
     * @param deltaPos delta positions
     */
    override fun translate(deltaPos: Vector3f) {
        // TODO implement
        throw NotImplementedError()
    }

    /**
     * Translates object based on its parent coordinate system.
     * Hint: this operation has to be left-multiplied
     * @param deltaPos delta positions (x, y, z)
     */
    override fun preTranslate(deltaPos: Vector3f) {
        // TODO implement
        throw NotImplementedError()
    }

    /**
     * Scales object related to its own origin
     * @param scale scale factor (x, y, z)
     */
    override fun scale(scale: Vector3f) {
        // TODO implement
        throw NotImplementedError()
    }

    /**
     * Returns position based on aggregated translations.
     * Hint: last column of model matrix
     * @return position
     */
    override fun getPosition(): Vector3f {
        // TODO implement
        throw NotImplementedError()
    }

    /**
     * Returns position based on aggregated translations incl. parents.
     * Hint: last column of world model matrix
     * @return position
     */
    override fun getWorldPosition(): Vector3f {
        // TODO implement
        throw NotImplementedError()
    }

    /**
     * Returns x-axis of object coordinate system
     * Hint: first normalized column of model matrix
     * @return x-axis
     */
    override fun getXAxis(): Vector3f {
        // TODO implement
        throw NotImplementedError()
    }

    /**
     * Returns y-axis of object coordinate system
     * Hint: second normalized column of model matrix
     * @return y-axis
     */
    override fun getYAxis(): Vector3f {
        // TODO implement
        throw NotImplementedError()
    }

    /**
     * Returns z-axis of object coordinate system
     * Hint: third normalized column of model matrix
     * @return z-axis
     */
    override fun getZAxis(): Vector3f {
        // TODO implement
        throw NotImplementedError()
    }

    /**
     * Returns x-axis of world coordinate system
     * Hint: first normalized column of world model matrix
     * @return x-axis
     */
    override fun getWorldXAxis(): Vector3f {
        // TODO implement
        throw NotImplementedError()
    }

    /**
     * Returns y-axis of world coordinate system
     * Hint: second normalized column of world model matrix
     * @return y-axis
     */
    override fun getWorldYAxis(): Vector3f {
        // TODO implement
        throw NotImplementedError()
    }

    /**
     * Returns z-axis of world coordinate system
     * Hint: third normalized column of world model matrix
     * @return z-axis
     */
    override fun getWorldZAxis(): Vector3f {
        // TODO implement
        throw NotImplementedError()
    }
}