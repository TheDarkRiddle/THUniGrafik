package cga.exercise.components.geometry

import org.joml.Vector3f
import kotlin.math.round

class CollisionCircle(private val owner: Renderable, val ownerScaleValue: Float, val setedRadius: Float, private val shiftValue: Vector3f? = null) {
    //atribs
    private var bIsAllowedToCollide = false
    private val trueRadius = setedRadius //setedRadius getOBJRadius()
    private var weitesterPunkt = getWeitesterPunkt()
    private var bCollieded = false
    //private var avergPos = getAvavergPose()

    //Getter
    fun getOwnerPosition(): Vector3f {
        if (shiftValue == null){
            return owner.getWorldPosition()
        }else{
            return owner.getWorldPosition().add(shiftValue)
        }
    }
    fun getOwner(): Renderable{return owner}
    fun getRadius(): Float {
        return trueRadius
    }
    fun getBCollided(): Boolean{return bCollieded}
    fun setBCollided(collided:Boolean){ bCollieded = collided}
    fun getWeitesterPunkt():Vector3f{
        //VertexPoints
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
        return Vector3f(highestX,highestY,highestZ)
    }

    /*
    * fun getAvavergPose(): Vector3f{
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
    }*/
    fun getBIsAllowedToCollide(): Boolean {
        return bIsAllowedToCollide
    }

    //Stetter
    fun setBIsAllowedToCollide(niceCollider: Boolean) {
        bIsAllowedToCollide = niceCollider
    }

    //Functions
        fun getOBJRadius(): Float {
            if (shiftValue == null){
                return getOwnerPosition().distance(weitesterPunkt) * ownerScaleValue
            }else{
                return getOwnerPosition().add(shiftValue).distance(weitesterPunkt) * ownerScaleValue
            }
        }
}