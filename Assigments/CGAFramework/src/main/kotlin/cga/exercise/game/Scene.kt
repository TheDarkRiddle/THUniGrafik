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
import org.lwjgl.BufferUtils
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE
import org.lwjgl.opengl.GL12.GL_TEXTURE_WRAP_R
import org.lwjgl.opengl.GL13.GL_TEXTURE_CUBE_MAP
import org.lwjgl.opengl.GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_X
import org.lwjgl.stb.STBImage

/**
 * Created by Fabian on 16.09.2017.
 */
class Scene(private val window: GameWindow) {
    //Shader
    private val staticShader: ShaderProgram = ShaderProgram("assets/shaders/tron_vert.glsl", "assets/shaders/tron_frag.glsl")
    //SKYBOX
    private val cubeMapShader : ShaderProgram = ShaderProgram("assets/shaders/cubeMap_vert.glsl", "assets/shaders/cubeMap_frag.glsl")

    //Objects
    private val ground: Renderable
    private val bike: Renderable
    private val dragon: Renderable
    private val tower: Renderable

    //Collider
    private val dragonCollider: Renderable

    //SKYBOX
    private var skyBox: Mesh? = null
    private var skyBoxTexture: Int? = null

    //Material
    private val dragonMat: Material
    private val towerMat: Material
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
        val defaultEmmitTex = Texture2D("assets/textures/defaultEmmitTex.png",true)
            defaultEmmitTex.setTexParams(GL_CLAMP, GL_CLAMP, GL_LINEAR_MIPMAP_LINEAR, GL_LINEAR)
        val defaultSpecTex = Texture2D("assets/textures/defaultSpecTex.png",true)
            defaultSpecTex.setTexParams(GL_CLAMP, GL_CLAMP, GL_LINEAR_MIPMAP_LINEAR, GL_LINEAR)

        val groundDiff = Texture2D("assets/textures/NatureGroundTexture.png", true)
        groundDiff.setTexParams(GL_REPEAT, GL_REPEAT, GL_LINEAR_MIPMAP_LINEAR, GL_LINEAR)

        //__loade Ground__
        groundMaterial = Material(groundDiff, defaultEmmitTex, defaultSpecTex, 60f, Vector2f(64.0f, 64.0f))

        //load an object and create a mesh
        val gres = loadOBJ("assets/models/ground.obj")
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
        ground.scale(Vector3f(3.0f,1.0f,3.0f))
        bike = loadModel("assets/Light Cycle/Light Cycle/HQ_Movie cycle.obj", Math.toRadians(-90.0f), Math.toRadians(90.0f), 0.0f) ?: throw IllegalArgumentException("Could not load the model")
        bike.scale(Vector3f(0.8f, 0.8f, 0.8f))

        //___loade Skybox___
            loadeSkyBox()


        //___loade dragon obj___
        val dragonOBJ = loadOBJ("assets/models/dragonNeu.obj")

        val dragonTex = Texture2D("assets/textures/dragon.png", true)
        dragonTex.setTexParams(GL_REPEAT, GL_REPEAT, GL_LINEAR_MIPMAP_LINEAR, GL_LINEAR)

        dragonMat = Material(dragonTex,defaultEmmitTex,defaultSpecTex)

        val d_atr1 = VertexAttribute(3, GL_FLOAT, stride, 0)     //position attribute //38505
        val d_atr2 = VertexAttribute(2, GL_FLOAT, stride, 3 * 4) //texture coordinate attribute
        val d_atr3 = VertexAttribute(3, GL_FLOAT, stride, 5 * 4) //normal attribute
        val d_vertexAttributes = arrayOf(d_atr1, d_atr2, d_atr3)

        dragon = Renderable()
        for (m in dragonOBJ.objects[0].meshes) {
            val mesh = Mesh(m.vertexData, m.indexData, d_vertexAttributes, dragonMat)
            dragon.meshes.add(mesh)
        }
        dragon.scale(Vector3f(0.5f,0.5f,0.5f))
        dragon.rotate(0.0f, Math.toRadians(90.0f),0.0f)

        //loade dragon Collider
        dragonCollider = genBoxCollider()
        dragonCollider.parent = dragon
        dragonCollider.translate(dragon.getWorldPosition())
        dragonCollider.scale(Vector3f(4.0f,4.0f,4.0f))
        //___loade Tower obj___
        val towerOBJ = loadOBJ("assets/models/towerNeu.obj")

        val towerDiff = Texture2D("assets/textures/TowerTextures/tower_square_7_Base_Color.png",  true)
            towerDiff.setTexParams(GL_REPEAT, GL_REPEAT, GL_LINEAR_MIPMAP_LINEAR, GL_LINEAR)
        val towerSpec = Texture2D("assets/textures/TowerTextures/tower_square_7_Mixed_AO.png", true)
            towerSpec.setTexParams(GL_REPEAT, GL_REPEAT, GL_LINEAR_MIPMAP_LINEAR, GL_LINEAR)

        towerMat = Material(towerDiff,defaultEmmitTex,towerSpec)

        val t_atr1 = VertexAttribute(3, GL_FLOAT, stride, 0)     //position attribute //38505
        val t_atr2 = VertexAttribute(2, GL_FLOAT, stride, 3 * 4) //texture coordinate attribute
        val t_atr3 = VertexAttribute(3, GL_FLOAT, stride, 5 * 4) //normal attribute
        val t_vertexAttributes = arrayOf(t_atr1, t_atr2, t_atr3)

        tower = Renderable()
        for (m in towerOBJ.objects[0].meshes) {
            val mesh = Mesh(m.vertexData, m.indexData, t_vertexAttributes, towerMat)
            tower.meshes.add(mesh)
        }
        tower.translate(Vector3f(0.0f,0.0f,10.0f))
        tower.scale(Vector3f(3.0f,3.0f,3.0f))

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

        groundColor = Vector3f(1.0f,1.0f,1.0f)

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
        glDisable(GL_CULL_FACE); GLError.checkThrow()
        glFrontFace(GL_CCW); GLError.checkThrow()
        glCullFace(GL_BACK); GLError.checkThrow()
        glEnable(GL_DEPTH_TEST); GLError.checkThrow()
        //glDepthFunc(GL_LESS) GLError.checkThrow()
    }

    fun render(dt: Float, t: Float) {
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

        //SKYBOX
        if(skyBox != null && skyBoxTexture != null){
        cubeMapShader.use()
        camera.bind(cubeMapShader)
        glDepthFunc(GL_LEQUAL); GLError.checkThrow()
        glDepthMask(false)
        glBindTexture(GL_TEXTURE_CUBE_MAP, skyBoxTexture!!)
        cubeMapShader.setUniform("skyBox", skyBoxTexture!!)
        skyBox!!.render(cubeMapShader)
        glDepthMask(true)

        glDepthFunc(GL_LESS); GLError.checkThrow()
        staticShader.use()
        camera.bind(staticShader)
        }

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
            //ground
        staticShader.setUniform("shadingColor", groundColor)
        ground.render(staticShader)
            //bike
        staticShader.setUniform("shadingColor", changingColor)
        bike.render(staticShader)
            //dragon
        staticShader.setUniform("shadingColor", Vector3f(1.0f,1.0f,1.0f))
        dragon.render(staticShader)
        staticShader.setUniform("shadingColor", Vector3f(1.0f,0.0f,0.0f))
        dragonCollider.render(staticShader)
            //tower
        staticShader.setUniform("shadingColor", Vector3f(1.0f,1.0f,1.0f))
        tower.render(staticShader)

    }
    fun genCubeMap(paths: Array<String>): Int {
        val CubeMapTextureID = glGenTextures()
        glBindTexture(GL_TEXTURE_CUBE_MAP, CubeMapTextureID)

        var i = 0
        for ( tex in paths){
            val x = BufferUtils.createIntBuffer(1)
            val y = BufferUtils.createIntBuffer(1)
            val readChannels = BufferUtils.createIntBuffer(1)
            //flip y coordinate to make OpenGL happy
            STBImage.stbi_set_flip_vertically_on_load(true)
            val imageData = STBImage.stbi_load(tex, x, y, readChannels, 4)

            glTexImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_X+i,0, GL_RGBA8, 256, 256, 0, GL_RGBA, GL_UNSIGNED_BYTE, imageData)
            i++
        }

        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MAG_FILTER, GL_LINEAR)
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MIN_FILTER, GL_LINEAR)
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE)
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE)
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_R, GL_CLAMP_TO_EDGE)

        return CubeMapTextureID
    }
    fun update(dt: Float, t: Float) {
        var moveMul = 11.0f
        val rotateMul = 0.5f * Math.PI.toFloat()
        val gravity = -0.06f
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
            dragon.translate(Vector3f(0.0f,dt * moveMul+(-gravity),0.0f))
        }
        if (window.getKeyState(GLFW_KEY_LEFT_SHIFT)) {
            dragon.translate(Vector3f(0.0f,-dt * moveMul,0.0f))
        }
        if(dragon.getPosition().y > ground.getPosition().y){
             dragon.translate(Vector3f(0.0f,gravity,0.0f))
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
    fun onMouseScroll(xoffset: Double, yoffset: Double) {
        //val multyplier = 0.25f
       //camera.fov = camera.fov * xoffset.toFloat()*multyplier
    }
    fun loadeSkyBox(){
        //SKYBOX
        val skyBoxVertices = floatArrayOf(
            -1.0f, -1.0f,  1.0f,
            1.0f, -1.0f,  1.0f,
            1.0f, -1.0f, -1.0f,
            -1.0f, -1.0f, -1.0f,
            -1.0f,  1.0f,  1.0f,
            1.0f,  1.0f,  1.0f,
            1.0f,  1.0f, -1.0f,
            -1.0f,  1.0f, -1.0f
        )
        val skyBoxIndices = intArrayOf(
            1, 2, 6,
            6, 5, 1,
            0, 4, 7,
            7, 3, 0,
            4, 5, 6,
            6, 7, 4,
            0, 1, 5,
            5, 4, 0,
            3, 7, 6,
            6, 2, 3,
            3 ,2, 1,
            3, 0, 1
        )

        val skyBoxTex = arrayOf(
            "assets/textures/skybox/skyBoxRIGHT.png",
            "assets/textures/skybox/skyBoxLEFT.png",
            "assets/textures/skybox/skyBoxBOTTOM.png",
            "assets/textures/skybox/skyBoxTOP.png",
            "assets/textures/skybox/skyBoxMIDDLE.png",
            "assets/textures/skybox/skyBoxBACK.png"
        )
        // falls Schwarze Ränder: Clamp To Edge //https://www.khronos.org/opengl/wiki/Cubemap_Texture#Seamless_cubemap
        //SKYBOX
        skyBoxTexture = genCubeMap(skyBoxTex)

        val skyBoxVAO = VertexAttribute(3, GL_FLOAT, 3 * 4, 0)     //position attribute //38505
        val skyBoxVertexAttributes = arrayOf(skyBoxVAO)
        skyBox = Mesh(skyBoxVertices,skyBoxIndices, skyBoxVertexAttributes)

    }

    fun genBoxCollider(): Renderable{
        val BoxVertices = floatArrayOf(
            -1.0f, -1.0f,  1.0f,
            1.0f, -1.0f,  1.0f,
            1.0f, -1.0f, -1.0f,
            -1.0f, -1.0f, -1.0f,
            -1.0f,  1.0f,  1.0f,
            1.0f,  1.0f,  1.0f,
            1.0f,  1.0f, -1.0f,
            -1.0f,  1.0f, -1.0f
        )
        val BoxIndices = intArrayOf(
            1, 2, 6,
            6, 5, 1,
            0, 4, 7,
            7, 3, 0,
            4, 5, 6,
            6, 7, 4,
            0, 1, 5,
            5, 4, 0,
            3, 7, 6,
            6, 2, 3,
            3 ,2, 1,
            3, 0, 1
        )

        val BoxVAO = VertexAttribute(3, GL_FLOAT, 3 * 4, 0)
        val BoxVertexAtributes = arrayOf(BoxVAO)

        val sampleMat = Material(Texture2D("assets/textures/default.png",true), Texture2D("assets/textures/defaultEmmitTex.png",true), Texture2D("assets/textures/defaultSpecTEx.png",true))
        return  Renderable(
                    mutableListOf(
                        Mesh(BoxVertices, BoxIndices, BoxVertexAtributes, sampleMat)
                                )
                        )
    }
    fun cleanup() {}
}