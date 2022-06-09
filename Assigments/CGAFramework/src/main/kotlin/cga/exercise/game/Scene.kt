package cga.exercise.game

import cga.exercise.components.camera.TronCamera
import cga.exercise.components.geometry.Mesh
import cga.exercise.components.geometry.Renderable
import cga.exercise.components.geometry.VertexAttribute
import cga.exercise.components.shader.ShaderProgram
import cga.framework.GLError
import cga.framework.GameWindow
import cga.framework.OBJLoader
import org.joml.Vector3f
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.opengl.GL20.*
import javax.swing.WindowConstants


/**
 * Created by Fabian on 16.09.2017.
 */
class Scene(private val window: GameWindow) {
    private val staticShader: ShaderProgram
    private var objectMesh:Mesh;
    private var objectMesh1:Mesh;
    private var planeOB: Renderable;
    private var sphereOB: Renderable;
    private val tronCam: TronCamera;
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

        //____ init Cam____
        tronCam = TronCamera();
        tronCam.rotate(org.joml.Math.toRadians(-20.0f),0.0f,0.0f);
        tronCam.translate(Vector3f(0.0f,0.0f,10.0f))



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
        //planeOB.rotate(org.joml.Math.toRadians(90.0f),0.0f,0.0f )
        //planeOB.scale(Vector3f(0.03f))

        sphereOB = Renderable(mutableListOf<Mesh>(objectMesh1));
        tronCam.parent = sphereOB;
        //sphereOB.scale(Vector3f(0.5f))


        //Normalise Vertecis
        // transpose -> inverse
    }

    fun render(dt: Float, t: Float) {
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
        staticShader.use()
        tronCam.bind(staticShader);
        planeOB.render(staticShader);
        sphereOB.render(staticShader);
    }

    fun update(dt: Float, t: Float) {
        if(window.getKeyState(GLFW_KEY_W)){
            sphereOB.translate(Vector3f(0.0f,0.0f,-dt*8))
        }
        else if(window.getKeyState(GLFW_KEY_S)){
            sphereOB.translate(Vector3f(0.0f,0.0f,dt*8))
        }

        if(window.getKeyState(GLFW_KEY_A)){
            sphereOB.rotate(0.0f,dt*2,0.0f)
        }
        else if(window.getKeyState(GLFW_KEY_D)){
            sphereOB.rotate(0.0f,-dt*2,0.0f)
        }
    }

    fun onKey(key: Int, scancode: Int, action: Int, mode: Int) {}

    fun onMouseMove(xpos: Double, ypos: Double) {}


    fun cleanup() {}
}
