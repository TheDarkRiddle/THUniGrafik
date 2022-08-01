package cga.exercise.game

import cga.exercise.components.camera.Aspectratio.Companion.custom
import cga.exercise.components.camera.TronCamera
import cga.exercise.components.geometry.*
import cga.exercise.components.light.PointLight
import cga.exercise.components.light.SpotLight
import cga.exercise.components.shader.ShaderProgram
import cga.exercise.components.texture.Texture2D
import cga.framework.GLError
import cga.framework.GameWindow
import cga.framework.ModelLoader.loadModel
import cga.framework.OBJLoader.loadOBJ
import org.joml.Math
import org.joml.Vector2f
import org.joml.Vector3f
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.opengl.GL11.*

/**
 * Created by Fabian on 16.09.2017.
 */
class Scene(private val window: GameWindow) {
    private val staticShader: ShaderProgram = ShaderProgram("Assigments/CGAFramework/assets/shaders/tron_vert.glsl", "Assigments/CGAFramework/assets/shaders/tron_frag.glsl")

    private val ground: Renderable
    private val bike: Renderable
    private val dragon: Renderable;

    private val dragonMat: Material
    private val groundMaterial: Material
    private val groundColor: Vector3f



    //Lights
    private val bikePointLight: PointLight
    private val pointLightList = mutableListOf<PointLight>()

    private val bikeSpotLight: SpotLight
    private val spotLightList = mutableListOf<SpotLight>()

    //camera
    private val camera: TronCamera
    private var oldMouseX = 0.0
    private var oldMouseY = 0.0
    private var firstMouseMove = true

    //scene setup^^
    init {
        //load textures
        val groundDiff = Texture2D("Assigments/CGAFramework/assets/textures/ground_diff.png", true)
        groundDiff.setTexParams(GL_REPEAT, GL_REPEAT, GL_LINEAR_MIPMAP_LINEAR, GL_LINEAR)
        val groundSpecular = Texture2D("Assigments/CGAFramework/assets/textures/ground_spec.png", true)
        groundSpecular.setTexParams(GL_REPEAT, GL_REPEAT, GL_LINEAR_MIPMAP_LINEAR, GL_LINEAR)
        val groundEmit = Texture2D("Assigments/CGAFramework/assets/textures/ground_emit.png", true)
        groundEmit.setTexParams(GL_REPEAT, GL_REPEAT, GL_LINEAR_MIPMAP_LINEAR, GL_LINEAR)
        groundMaterial = Material(groundDiff, groundEmit, groundSpecular, 60f, Vector2f(64.0f, 64.0f))

        //load an object and create a mesh
        val gres = loadOBJ("Assigments/CGAFramework/assets/models/ground.obj")
        //Create the mesh
        val stride = 8 * 4
        val atr1 = VertexAttribute(3, GL_FLOAT, stride, 0)     //position attribute
        val atr2 = VertexAttribute(2, GL_FLOAT, stride, 3 * 4) //texture coordinate attribute
        val atr3 = VertexAttribute(3, GL_FLOAT, stride, 5 * 4) //normal attribute
        val vertexAttributes = arrayOf(atr1, atr2, atr3)
        //Create renderable
        ground = Renderable()
        for (m in gres.objects[0].meshes) {
            val mesh = Mesh(m.vertexData, m.indexData, vertexAttributes, groundMaterial)
            ground.meshes.add(mesh)
        }
        bike = loadModel("Assigments/CGAFramework/assets/Light Cycle/Light Cycle/HQ_Movie cycle.obj", Math.toRadians(-90.0f), Math.toRadians(90.0f), 0.0f) ?: throw IllegalArgumentException("Could not load the model")
        bike.scale(Vector3f(0.8f, 0.8f, 0.8f))

        //loade obj

        val dragonTex = Texture2D("Assigments/CGAFramework/assets/textures/dragon.BMP", true);
        dragonTex.setTexParams(GL_REPEAT, GL_REPEAT, GL_LINEAR_MIPMAP_LINEAR, GL_LINEAR);
        dragonMat = Material(dragonTex,dragonTex,dragonTex, 60f,Vector2f(64.0f, 64.0f));
        val dragonOBJ = loadOBJ("Assigments/CGAFramework/assets/models/dragon.obj");

        val d_atr1 = VertexAttribute(3, GL_FLOAT, stride, 0)     //position attribute //38505
        val d_atr2 = VertexAttribute(2, GL_FLOAT, stride, 3 * 38505) //texture coordinate attribute
        val d_atr3 = VertexAttribute(3, GL_FLOAT, stride, 5 * 78222) //normal attribute
        val d_vertexAttributes = arrayOf(atr1, atr2, atr3)

        dragon = Renderable()
        for (m in dragonOBJ.objects[0].meshes) {
            val mesh = Mesh(m.vertexData, m.indexData, d_vertexAttributes, dragonMat)
            dragon.meshes.add(mesh)
        }
        //dragon = loadModel("Assigments/CGAFramework/assets/models/dragon.obj",0.0f,180.0f,0.0f) ?: throw IllegalArgumentException("Could not load the model")
        dragon.scale(Vector3f(0.5f,0.5f,0.5f));
        dragon.rotate(0.0f, Math.toRadians(90.0f),0.0f);
        //setup camera
        camera = TronCamera(
                custom(window.framebufferWidth, window.framebufferHeight),
                Math.toRadians(90.0f),
                0.1f,
                100.0f
        )
        camera.parent = dragon
        camera.rotate(Math.toRadians(0.0f), Math.toRadians(180.0f), 0.0f)
        camera.translate(Vector3f(0.0f, 8.0f, 0.0f))

        groundColor = Vector3f(0.0f, 1.0f, 0.0f)

        //bike point light
        bikePointLight = PointLight("pointLight[${pointLightList.size}]", Vector3f(0.0f, 2.0f, 0.0f), Vector3f(0.0f, 0.5f, 0.0f))
        bikePointLight.parent = bike
        pointLightList.add(bikePointLight)

        //bike spot light
        bikeSpotLight = SpotLight("spotLight[${spotLightList.size}]", Vector3f(3.0f, 3.0f, 3.0f), Vector3f(0.0f, 1.0f, -2.0f), Math.toRadians(20.0f), Math.toRadians(30.0f))
        bikeSpotLight.rotate(Math.toRadians(-10.0f), 0.0f, 0.0f)
        bikeSpotLight.parent = bike
        spotLightList.add(bikeSpotLight)

        // additional lights in the scene
        pointLightList.add(PointLight("pointLight[${pointLightList.size}]", Vector3f(0.0f, 2.0f, 2.0f), Vector3f(-10.0f, 2.0f, -10.0f)))
        pointLightList.add(PointLight("pointLight[${pointLightList.size}]", Vector3f(2.0f, 0.0f, 0.0f), Vector3f(10.0f, 2.0f, 10.0f)))
        spotLightList.add(SpotLight("spotLight[${spotLightList.size}]", Vector3f(10.0f, 20.0f, 20.0f), Vector3f(6.0f, 0.5f, 4.0f), Math.toRadians(20.0f), Math.toRadians(30.0f)))
        spotLightList.last().rotate(Math.toRadians(20f), Math.toRadians(60.0f), 0f)

        //initial opengl state
        glClearColor(0.0f, 0.0f, 0.0f, 1.0f); GLError.checkThrow()
        glEnable(GL_CULL_FACE); GLError.checkThrow()
        glFrontFace(GL_CCW); GLError.checkThrow()
        glCullFace(GL_BACK); GLError.checkThrow()
        glEnable(GL_DEPTH_TEST); GLError.checkThrow()
        glDepthFunc(GL_LESS); GLError.checkThrow()
    }

    fun render(dt: Float, t: Float) {
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

        staticShader.use()
        camera.bind(staticShader)

        // TODO 4.5 Verstehen: Wie führen die hier verwendeten Funktionen zu dem Regenbogen-Effekt über die Zeit?
        val changingColor = Vector3f(Math.abs(Math.sin(t)), 0f, Math.abs(Math.cos(t)))
        bikePointLight.lightColor = changingColor

        // bind lights
        for (pointLight in pointLightList) {
            pointLight.bind(staticShader)
        }
        staticShader.setUniform("numPointLights", pointLightList.size)
        for (spotLight in spotLightList) {
            spotLight.bind(staticShader, camera.calculateViewMatrix())
        }
        staticShader.setUniform("numSpotLights", spotLightList.size)

        // render objects
        staticShader.setUniform("shadingColor", groundColor)
        ground.render(staticShader)
        staticShader.setUniform("shadingColor", changingColor)
        bike.render(staticShader)
        //staticShader.setUniform("shadingColor", Vector3f(0.0f,1.0f,0.0f));
        dragon.render(staticShader);
    }

    fun update(dt: Float, t: Float) {
        val moveMul = 9.0f
        val rotateMul = 0.5f * Math.PI.toFloat()
        val gravity = -0.05f;
        if (window.getKeyState(GLFW_KEY_W)) {
            dragon.translate(Vector3f(0.0f, 0.0f, dt * moveMul))
        }
        if (window.getKeyState(GLFW_KEY_S)) {
            dragon.translate(Vector3f(0.0f, 0.0f, -dt * moveMul))
        }
        if (window.getKeyState(GLFW_KEY_A) and window.getKeyState(GLFW_KEY_W)) {
            dragon.rotate(0.0f,0.0f,-dt * rotateMul)
        }
        if (window.getKeyState(GLFW_KEY_D) and window.getKeyState(GLFW_KEY_W)) {
            dragon.rotate(0.0f, 0.0f, dt * rotateMul)
        }
        if (window.getKeyState(GLFW_KEY_SPACE)) {
            dragon.translate(Vector3f(0.0f,dt * moveMul+(-gravity),0.0f));
        }
        if (window.getKeyState(GLFW_KEY_LEFT_SHIFT)) {
            //bikeSpotLight.rotate(Math.PI.toFloat() * dt, 0.0f, 0.0f)
            dragon.translate(Vector3f(0.0f,-dt * moveMul,0.0f));
        }
        if(dragon.getPosition().y > ground.getPosition().y){
             dragon.translate(Vector3f(0.0f,gravity,0.0f));
        }

    }

    fun onKey(key: Int, scancode: Int, action: Int, mode: Int) {}

    fun onMouseMove(xpos: Double, ypos: Double) {
        if (!firstMouseMove) {
            val yawAngle = (xpos - oldMouseX).toFloat() * 0.002f
            val pitchAngle = (ypos - oldMouseY).toFloat() * 0.0005f
            if (!window.getKeyState(GLFW_KEY_LEFT_ALT)) {
                dragon.rotate(pitchAngle, -yawAngle, 0.0f)
            }
            else{
                camera.rotateAroundPoint(0.0f, yawAngle, 0.0f, Vector3f(0.0f, 0.0f, 0.0f))
            }
        } else firstMouseMove = false
        oldMouseX = xpos
        oldMouseY = ypos
    }

    fun cleanup() {}
}