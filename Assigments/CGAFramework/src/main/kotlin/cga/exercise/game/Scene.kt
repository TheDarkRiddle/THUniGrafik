package cga.exercise.game

import cga.exercise.components.geometry.Mesh
import cga.exercise.components.geometry.Renderable
import cga.exercise.components.geometry.VertexAttribute
import cga.exercise.components.shader.ShaderProgram
import cga.framework.GLError
import cga.framework.GameWindow
import cga.framework.OBJLoader
import org.joml.Vector3f
import org.lwjgl.opengl.GL20.*


/**
 * Created by Fabian on 16.09.2017.
 */
class Scene(private val window: GameWindow) {
    private val staticShader: ShaderProgram
    private var objectMesh:Mesh;
    private var objectMesh1:Mesh;
    private var planeOB: Renderable;
    private var sphereOB: Renderable;
    //scene setup
    init {
        staticShader = ShaderProgram("assets/shaders/tron_vert.glsl", "assets/shaders/tron_frag.glsl")

        //initial opengl state
        glClearColor(0.0f, 0.0f, 0.0f, 1.0f); GLError.checkThrow()
        glEnable(GL_CULL_FACE); GLError.checkThrow()
        glFrontFace(GL_CCW); GLError.checkThrow()
        glCullFace(GL_BACK); GLError.checkThrow()
        glEnable(GL_DEPTH_TEST); GLError.checkThrow()
        glDepthFunc(GL_LESS); GLError.checkThrow()

        //____Object Loader____
        var OBJLoaderResult = OBJLoader.loadOBJ("assets/models/ground.obj")
        var tempVer = OBJLoaderResult.objects[0].meshes[0].vertexData
        var tempInd = OBJLoaderResult.objects[0].meshes[0].indexData
        var atributesOfObject = arrayOf(
            VertexAttribute(3, GL_FLOAT,32,0),
            VertexAttribute(2, GL_FLOAT,32,12),
            VertexAttribute(3,GL_FLOAT,32,20)
        )
        objectMesh = Mesh(tempVer,tempInd,atributesOfObject);

        var OBJLoaderResult1 = OBJLoader.loadOBJ("assets/models/sphere.obj")
        var tempVer1 = OBJLoaderResult1.objects[0].meshes[0].vertexData
        var tempInd1 = OBJLoaderResult1.objects[0].meshes[0].indexData
        var atributesOfObject1 = arrayOf(
            VertexAttribute(3, GL_FLOAT,32,0),
            VertexAttribute(2, GL_FLOAT,32,12),
            VertexAttribute(3,GL_FLOAT,32,20)
        )
        objectMesh1 =Mesh(tempVer1,tempInd1,atributesOfObject1)

        //Erstellung der Rendabl's und anwendung der Transformationen
        planeOB = Renderable(mutableListOf<Mesh>(objectMesh));
        planeOB.rotate(org.joml.Math.toRadians(90.0f),0.0f,0.0f )
        planeOB.scale(Vector3f(0.03f))

        sphereOB = Renderable(mutableListOf<Mesh>(objectMesh1));
        sphereOB.scale(Vector3f(0.5f))
    }

    fun render(dt: Float, t: Float) {
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
        staticShader.use()
        planeOB.render(staticShader);
        sphereOB.render(staticShader);
    }

    fun update(dt: Float, t: Float) {}

    fun onKey(key: Int, scancode: Int, action: Int, mode: Int) {}

    fun onMouseMove(xpos: Double, ypos: Double) {}


    fun cleanup() {}
}
