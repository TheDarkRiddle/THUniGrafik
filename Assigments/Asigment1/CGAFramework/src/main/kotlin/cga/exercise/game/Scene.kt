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
    //private var house:Mesh;
    //private var initials:Mesh;
    private var objectMesh:Mesh;
    private var matrix = Matrix4f()
    //scene setup
    init {
        staticShader = ShaderProgram("F:\\SSD_Programirung\\THUniGrafik\\THUniGrafik\\Assigments\\Asigment2\\CGAFramework\\assets\\shaders\\tron_vert.glsl", "F:\\SSD_Programirung\\THUniGrafik\\THUniGrafik\\Assigments\\Asigment2\\CGAFramework\\assets\\shaders\\tron_frag.glsl")

        //initial opengl state
        glClearColor(0.0f, 0.0f, 0.0f, 1.0f); GLError.checkThrow()
        glEnable(GL_CULL_FACE); GLError.checkThrow()
        glFrontFace(GL_CCW); GLError.checkThrow()
        glCullFace(GL_BACK); GLError.checkThrow()
        glEnable(GL_DEPTH_TEST); GLError.checkThrow()
        glDepthFunc(GL_LESS); GLError.checkThrow()

        //____House Mesh____
        /*
        var vertexPosOfHouse = floatArrayOf(
            -0.5f,-0.5f,0.0f,0.0f,0.0f,1.0f,
            0.5f,-0.5f,0.0f,0.0f,0.0f,1.0f,
            0.5f, 0.5f,0.0f,0.0f,1.0f,0.0f,
            0.0f, 1.0f,0.0f,1.0f,0.0f,0.0f,
            -0.5f, 0.5f,0.0f,0.0f,1.0f,0.0f
        )
        var indicesOfHouse = intArrayOf(
            0,1,2,
            0,2,4,
            4,2,3
        )
        var atributesOfHouse = arrayOf(
            VertexAttribute(3, GL_FLOAT,24,0),
            VertexAttribute(3, GL_FLOAT,24,12)
        )
        house = Mesh(vertexPosOfHouse, indicesOfHouse, atributesOfHouse)
        */

        //____Initials Mesh____
       /*
       var vertexPosOfInitials = floatArrayOf(
           -0.4f,0.4f,0.0f,0.0f,0.0f,1.0f,
           -0.4f,-0.1f,0.0f,0.0f,0.0f,1.0f,
           -0.3f,0.4f,0.0f,0.0f,0.0f,1.0f,
           -0.3f,-0.1f,0.0f,0.0f,0.0f,1.0f,
           0.0f,0.0f,0.0f,0.0f,0.0f,1.0f,
           0.0f,0.1f,0.0f,0.0f,0.0f,1.0f,
           0.3f,0.4f,0.0f,0.0f,0.0f,1.0f,
           0.3f,-0.1f,0.0f,0.0f,0.0f,1.0f,
           0.4f,0.4f,0.0f,0.0f,0.0f,1.0f,
           0.4f,-0.1f,0.0f,0.0f,0.0f,1.0f
       )
       var indicesOfInitials = intArrayOf(
           0,1,2,
           2,1,3,
           2,4,5,
           5,4,6,
           6,7,8,
           8,7,9
       )
       var atributesOfInitials = arrayOf(
           VertexAttribute(3, GL_FLOAT,24,0),
           VertexAttribute(3, GL_FLOAT,24,12)
       )
       initials = Mesh(vertexPosOfInitials,indicesOfInitials,atributesOfInitials);
       */

        //____Object Loader____
        var OBJLoaderResult = OBJLoader.loadOBJ("F:\\SSD_Programirung\\THUniGrafik\\THUniGrafik\\Assigments\\Asigment2\\CGAFramework\\assets\\models\\ground.obj")

        var tempVer = OBJLoaderResult.objects[0].meshes[0].vertexData
        var tempInd = OBJLoaderResult.objects[0].meshes[0].indexData
        var atributesOfObject = arrayOf(
            VertexAttribute(3, GL_FLOAT,32,0),
            VertexAttribute(2, GL_FLOAT,32,12),
            VertexAttribute(3,GL_FLOAT,32,20)
        )
        objectMesh = Mesh(tempVer,tempInd,atributesOfObject);
    }

    fun toFloatBuffer(v: FloatArray): FloatBuffer? {
        val buf: ByteBuffer = ByteBuffer.allocateDirect(v.size * 4)
        buf.order(ByteOrder.nativeOrder())
        val buffer: FloatBuffer = buf.asFloatBuffer()
        buffer.put(v)
        buffer.position(0)
        return buffer
    }
    fun render(dt: Float, t: Float) {
        var matrixFloate = floatArrayOf(
            1.0f,0.0f,0.0f,0.0f,
            0.0f,cos(90.0f),-sin(90.0f),0.0f,
            0.0f,sin(90.0f),cos(90.0f),0.0f,
            0.0f,0.0f,0.0f,1.0f)
        matrix = Matrix4f(
            1.0f,0.0f,0.0f,0.0f,
            0.0f,cos(90.0f),-sin(90.0f),0.0f,
            0.0f,sin(90.0f),cos(90.0f),0.0f,
            0.0f,0.0f,0.0f,1.0f)

        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
        staticShader.use()
        var uniformLocation = glGetUniformLocation(0,"model_matrix")
        objectMesh.render()
        glUniformMatrix4fv(uniformLocation, false,toFloatBuffer(matrixFloate))
        //house.render();
        //initials.render()
    }

    fun update(dt: Float, t: Float) {}

    fun onKey(key: Int, scancode: Int, action: Int, mode: Int) {}

    fun onMouseMove(xpos: Double, ypos: Double) {}


    fun cleanup() {}
}
