package cga.exercise.game

import cga.exercise.components.geometry.Mesh
import cga.exercise.components.geometry.VertexAttribute
import cga.exercise.components.shader.ShaderProgram
import cga.framework.GLError
import cga.framework.GameWindow
import cga.framework.OBJLoader
import org.joml.Math.cos
import org.joml.Math.sin
import org.joml.Matrix4f
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL20.*
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer


/**
 * Created by Fabian on 16.09.2017.
 */
class Scene(private val window: GameWindow) {
    private val staticShader: ShaderProgram
    private var objectMesh:Mesh;
    private var objectMesh1:Mesh;
    private var matrix = Matrix4f()
    //scene setup
    init {
        staticShader = ShaderProgram("Assigments/Asigment2/CGAFramework/assets/shaders/tron_vert.glsl", "Assigments/Asigment2/CGAFramework/assets/shaders/tron_frag.glsl")

        //initial opengl state
        glClearColor(0.0f, 0.0f, 0.0f, 1.0f); GLError.checkThrow()
        glEnable(GL_CULL_FACE); GLError.checkThrow()
        glFrontFace(GL_CCW); GLError.checkThrow()
        glCullFace(GL_BACK); GLError.checkThrow()
        glEnable(GL_DEPTH_TEST); GLError.checkThrow()
        glDepthFunc(GL_LESS); GLError.checkThrow()

        //____Object Loader____
        var OBJLoaderResult = OBJLoader.loadOBJ("Assigments/Asigment2/CGAFramework/assets/models/ground.obj")
        var tempVer = OBJLoaderResult.objects[0].meshes[0].vertexData
        var tempInd = OBJLoaderResult.objects[0].meshes[0].indexData
        var atributesOfObject = arrayOf(
            VertexAttribute(3, GL_FLOAT,32,0),
            VertexAttribute(2, GL_FLOAT,32,12),
            VertexAttribute(3,GL_FLOAT,32,20)
        )
        objectMesh = Mesh(tempVer,tempInd,atributesOfObject);

        var OBJLoaderResult1 = OBJLoader.loadOBJ("Assigments/Asigment1/CGAFramework/assets/models/sphere.obj")
        var tempVer1 = OBJLoaderResult.objects[0].meshes[0].vertexData
        var tempInd1 = OBJLoaderResult.objects[0].meshes[0].indexData
        var atributesOfObject1 = arrayOf(
            VertexAttribute(3, GL_FLOAT,32,0),
            VertexAttribute(2, GL_FLOAT,32,12),
            VertexAttribute(3,GL_FLOAT,32,20)
        )
        objectMesh1 =Mesh(tempVer1,tempInd1,atributesOfObject1)
    }

    fun render(dt: Float, t: Float) {
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
        staticShader.use()
        objectMesh.render()
        staticShader.setUniform("model_matrix",matrix,false)
        objectMesh1.render()
    }

    fun update(dt: Float, t: Float) {}

    fun onKey(key: Int, scancode: Int, action: Int, mode: Int) {}

    fun onMouseMove(xpos: Double, ypos: Double) {}


    fun cleanup() {}
}
