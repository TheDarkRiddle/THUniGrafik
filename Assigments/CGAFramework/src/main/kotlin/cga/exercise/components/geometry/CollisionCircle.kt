package cga.exercise.components.geometry

import org.joml.Vector3f
import kotlin.math.round

class CollisionCircle(private val owner: Renderable, val ownerScaleValue: Float) {
    //atribs
    private var bIsAllowedToCollide = false
    private val trueRadius = getOBJRadius()
    private var weitesterPunkt = getWeitesterPunkt()
    private var avergPos = getAvavergPose()

    //Getter
    fun getOwnerPosition(): Vector3f {
        return owner.getPosition()
    }

    fun getRadius(): Float {
        return trueRadius
    }

    fun getWeitesterPunkt():Vector3f{
        var highestX = 0.0f
        var highestY = 0.0f
        var highestZ = 0.0f
        val vertexData = owner.meshes[0].getVertexData()

        var helper = 0
        for ((index, e) in vertexData.withIndex()) {
            if((index + helper) < vertexData.size){
                val X = vertexData[index + helper]
                helper++
                val Y = vertexData[index + helper]
                helper++
                val Z = vertexData[index + helper]
                helper++

                if (highestX < X) {
                    highestX = X
                }
                if (highestY < Y) {
                    highestY = Y
                }
                if (highestZ < Z) {
                    highestZ = Z
                }
            }else{break}
        }
        System.out.println("HighestX:" + highestX)
        System.out.println("HighestY:" + highestY)
        System.out.println("HighestZ:" + highestZ)
        return Vector3f(highestX,highestY,highestZ)
    }

    fun getAvavergPose(): Vector3f{
        var highestX = 0.0f
        var highestY = 0.0f
        var highestZ = 0.0f
        val vertexData = owner.meshes[0].getVertexData()

        var helper = 0
        for ((index, e) in vertexData.withIndex()) {
            if((index + helper) < vertexData.size){
                val X = vertexData[index + helper]
                helper++
                val Y = vertexData[index + helper]
                helper++
                val Z = vertexData[index + helper]
                helper++

                if (highestX < X) {
                    highestX = X
                }
                if (highestY < Y) {
                    highestY = Y
                }
                if (highestZ < Z) {
                    highestZ = Z
                }
            }else{break}
        }
        System.out.println("AvergMiddle:" + Vector3f(highestX/vertexData.size,highestY/vertexData.size,highestZ/vertexData.size))
        return Vector3f(highestX/vertexData.size,highestY/vertexData.size,highestZ/vertexData.size)
    }
    fun getBIsAllowedToCollide(): Boolean {
        return bIsAllowedToCollide
    }

    //Stetter
    fun setColliderStatus(niceCollider: Boolean) {
        bIsAllowedToCollide = niceCollider
    }

    //Functions
        fun getOBJRadius(): Float {
            var highestX = 0.0f
            var highestY = 0.0f
            var highestZ = 0.0f
            val vertexData = owner.meshes[0].getVertexData()

            var helper = 0
            for ((index, e) in vertexData.withIndex()) {
                if((index + helper) < vertexData.size){
                    val X = vertexData[index + helper]
                    helper++
                    val Y = vertexData[index + helper]
                    helper++
                    val Z = vertexData[index + helper]
                    helper++

                    if (highestX < X) {
                        highestX = X
                    }
                    if (highestY < Y) {
                        highestY = Y
                    }
                    if (highestZ < Z) {
                        highestZ = Z
                    }
                }else{break}
            }
            return getOwnerPosition().distance(Vector3f(highestX,highestY,highestZ)) * ownerScaleValue
        }
}