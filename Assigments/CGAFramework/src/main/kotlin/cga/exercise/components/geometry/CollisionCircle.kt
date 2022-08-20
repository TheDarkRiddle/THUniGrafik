package cga.exercise.components.geometry

import org.joml.Vector3f

class CollisionCircle(private val owner: Renderable) {
    //atribs
    private var bIsAllowedToCollide = false
    private val trueRadius = getOBJRadius()

    //Getter
    fun getOwnerPosition(): Vector3f {
        return owner.getPosition()
    }

    fun getRadius(): Float {
        return trueRadius
    }

    fun getBIsAllowedToCollide(): Boolean {
        return bIsAllowedToCollide
    }

    //Stetter
    fun setColliderStatus(niceCollider: Boolean) {
        bIsAllowedToCollide = niceCollider
    }

    //Functions
    fun getOBJCenter(): Vector3f {

        return Vector3f(0.0f)
    }
        fun getOBJRadius(): Float {
            var radius = 0.0f;
            var highestX = 0.0f
            var highestY = 0.0f
            var highestZ = 0.0f
            val vertexData = owner.meshes[0].getVertexData()

            var helper = 0
            for ((index, e) in vertexData.withIndex()) {
                if(!((index + helper) >= vertexData.size)){
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
            return owner.getPosition().distance(Vector3f(highestX,highestY,highestZ))
        }

}

