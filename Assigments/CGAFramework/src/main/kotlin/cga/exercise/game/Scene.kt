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
    //blending
    private val blendingShader : ShaderProgram = ShaderProgram("assets/shaders/blending_vert.glsl", "assets/shaders/blending_frag.glsl")

    //Objects
    private val ground: Renderable
    private val dragon: Renderable
    private val tower: Renderable
    private val tower1: Renderable
    private val ring0: Renderable
    private val ring1: Renderable
    private val ring2: Renderable
    private val ring3: Renderable
    private val ring4: Renderable
    private val cake: Renderable

    //Collider
    private val colliderArrray: Array<CollisionCircle>
    private val dragonCollider: CollisionCircle
    private val towerCollider0: CollisionCircle
    private val towerCollider1: CollisionCircle
    private val towerCollider2: CollisionCircle
    private val tower1Collider0: CollisionCircle
    private val tower1Collider1: CollisionCircle
    private val tower1Collider2: CollisionCircle
    private val ringCollider0: CollisionCircle
    private val ringCollider1: CollisionCircle
    private val ringCollider2: CollisionCircle
    private val ringCollider3: CollisionCircle
    private val ringCollider4: CollisionCircle

    //SKYBOX
    private var skyBox: Mesh? = null
    private var skyBoxTexture: Int? = null

    //Material
    private var groundMaterial: Material? = null
    private var groundDiff2: Texture2D? = null
    private var blendMap: Texture2D? = null
    private val defaultEmmitTex: Texture2D
    private val defaultSpecTex: Texture2D
    private val groundColor: Vector3f

    //hepler
    private val stride = 8 * 4
    val spawn: Vector3f = Vector3f(2.0f, 4.0f, 5.0f)
    val ringArray: Array<Renderable>

    //Lights
    private val pointLightList = mutableListOf<PointLight>()
    private val spotLightList = mutableListOf<SpotLight>()

    //camera
    private var camCount = 0
    private var localCam: TronCamera? =null
    private val camera: TronCamera
    private val camTwo: TronCamera
    private var oldMouseX = 0.0
    private var oldMouseY = 0.0
    private var firstMouseMove = true

    //scene setup^^
    init {
        //load textures
        defaultEmmitTex = Texture2D("assets/textures/defaultEmmitTex.png",true)
        defaultEmmitTex.setTexParams(GL_CLAMP_TO_EDGE, GL_CLAMP_TO_EDGE, GL_LINEAR_MIPMAP_LINEAR, GL_LINEAR)
        defaultSpecTex = Texture2D("assets/textures/defaultSpecTex.png",true)
        defaultSpecTex.setTexParams(GL_CLAMP_TO_EDGE, GL_CLAMP_TO_EDGE, GL_LINEAR_MIPMAP_LINEAR, GL_LINEAR)

        //__loade Ground__
        ground = loadGround()
        ground.scale(Vector3f(60.0f,60.0f,60.0f))

        //___loade Skybox___
            loadSkyBox()

        //___loade dragon obj___
        dragon = loadDragon()
        dragon.translate(Vector3f(2.0f, 4.0f, 8.0f))
        dragon.scale(Vector3f(0.5f,0.5f,0.5f))
        dragon.rotate(0.0f, Math.toRadians(90.0f),0.0f)
        
        //___loade Tower obj___
        tower = loadTower()
        tower.scale(Vector3f(3.0f,3.0f,3.0f))
        tower.translate(Vector3f(0.0f,0.0f,10.0f))

        tower1 = loadTower()
        tower1.scale(Vector3f(3.0f,3.0f,3.0f))
        tower1.translate(Vector3f(2.0f,0.0f,-10.0f))

        //___Load Cake___
        cake = loadeCake()
        cake.translate(Vector3f(0.0f,20.0f,0.0f))

        //___loade Ring obj___
        //+Z hinten -X rechts
        ring0 = loadRing()
        ring0.translate(tower.getPosition())
        ring0.translate(Vector3f(-9.0f,6.0f,0.0f))
        ring0.rotate(0.0f,Math.toRadians(0.0f),0.0f)
        ring0.scale(Vector3f(0.5f))

        ring1 = loadRing()
        ring1.translate(tower.getPosition())
        ring1.translate(Vector3f(-7.0f,12.0f,5.0f))
        ring1.scale(Vector3f(0.5f))
        ring1.rotate(0.0f,Math.toRadians(45.0f),0.0f)

        ring2 = loadRing()
        ring2.translate(tower.getPosition())
        ring2.translate(Vector3f(0.0f,18.0f,8.0f))
        ring2.scale(Vector3f(0.5f))
        ring2.rotate(0.0f,Math.toRadians(90.0f),0.0f)

        ring3 = loadRing()
        ring3.translate(tower.getPosition())
        ring3.translate(Vector3f(9.0f,16.0f,4.0f))
        ring3.scale(Vector3f(0.5f))
        ring3.rotate(0.0f,Math.toRadians(-45.0f),0.0f)

        ring4 = loadRing()
        ring4.translate(tower1.getPosition())
        ring4.translate(Vector3f(9.0f,15.5f,4.0f))
        ring4.scale(Vector3f(0.5f))
        ring4.rotate(Math.toRadians(90.0f),Math.toRadians(0.0f),0.0f)

        ringArray = arrayOf(ring0, ring1, ring2, ring3, ring4)


        //loade Collider
        dragonCollider = CollisionCircle(dragon,0.5f,2.8f)
        dragonCollider.setBIsAllowedToCollide(true)
        dragon.setCollider(dragonCollider)
        dragon.setCollider(dragonCollider)

        towerCollider0 = CollisionCircle(tower,0.0f,1.0f, Vector3f(0.0f,0.0f,0.0f))
        towerCollider1 = CollisionCircle(tower,0.0f, 1.0f, Vector3f(0.0f,6.0f,0.0f))
        towerCollider2 = CollisionCircle(tower,0.0f, 1.0f, Vector3f(0.0f,8.0f,0.0f))

        tower1Collider0 = CollisionCircle(tower1,0.0f,1.0f, Vector3f(0.0f,0.0f,0.0f))
        tower1Collider1 = CollisionCircle(tower1,0.0f, 1.0f, Vector3f(0.0f,6.0f,0.0f))
        tower1Collider2 = CollisionCircle(tower1,0.0f, 1.0f, Vector3f(0.0f,8.0f,0.0f))

        val ringScale = 0.5f
        val ringRadius = 2.5f
        ringCollider0 = CollisionCircle(ring0, ringScale, ringRadius)
        ringCollider0.setBIsAllowedToCollide(true)
        ring0.setCollider(ringCollider0)

        ringCollider1 = CollisionCircle(ring1, ringScale, ringRadius)
        ringCollider1.setBIsAllowedToCollide(true)
        ring1.setCollider(ringCollider1)

        ringCollider2 = CollisionCircle(ring2, ringScale, ringRadius)
        ringCollider2.setBIsAllowedToCollide(true)
        ring2.setCollider(ringCollider2)

        ringCollider3 = CollisionCircle(ring3, ringScale, ringRadius)
        ringCollider3.setBIsAllowedToCollide(true)
        ring3.setCollider(ringCollider3)

        ringCollider4 = CollisionCircle(ring4, ringScale, ringRadius)
        ringCollider4.setBIsAllowedToCollide(true)
        ring4.setCollider(ringCollider4)

        colliderArrray = arrayOf(dragonCollider, towerCollider0, towerCollider1, towerCollider2, tower1Collider0, tower1Collider1, tower1Collider2, ringCollider0, ringCollider1, ringCollider2, ringCollider3, ringCollider4)


        //setup camera
        camera = TronCamera(
                custom(window.framebufferWidth, window.framebufferHeight),
                Math.toRadians(90.0f),
                0.1f,
                100.0f
        )
        camTwo = TronCamera(
                custom(window.framebufferWidth, window.framebufferHeight),
                Math.toRadians(90.0f),
                0.1f,
                100.0f
        )
        camTwo.parent = dragon
        camTwo.rotate(Math.toRadians(0.0f), Math.toRadians(180.0f), 0.0f)
        camTwo.translate(Vector3f(0.0f, 6.0f, -5.0f))
        camera.parent = dragon
        camera.rotate(Math.toRadians(0.0f), Math.toRadians(180.0f), 0.0f)
        camera.translate(Vector3f(0.0f, 8.0f, 0.0f))

        groundColor = Vector3f(1.0f,1.0f,1.0f)

        // additional lights in the scene
        pointLightList.add(PointLight("pointLight[${pointLightList.size}]",Vector3f(30.0f,30.0f,30.0f), Vector3f(0.0f,40.0f,0.0f)))
        pointLightList.add(PointLight("pointLight[${pointLightList.size}]", Vector3f(30.0f,30.0f,30.0f), tower.getPosition().add(Vector3f(0.0f,40.0f,0.0f))))
        pointLightList.add(PointLight("pointLight[${pointLightList.size}]", Vector3f(30.0f,30.0f,30.0f), tower.getPosition().add(Vector3f(10.0f,40.0f,0.0f))))

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

        localCam = if(camCount == 0){
            camera
        }else{
            camTwo
        }
        //SKYBOX
        if(skyBox != null && skyBoxTexture != null){
            cubeMapShader.use()
            localCam!!.bind(cubeMapShader)
            glDepthFunc(GL_LEQUAL) //GLError.checkThrow()
            glDepthMask(false)
            glBindTexture(GL_TEXTURE_CUBE_MAP, skyBoxTexture!!)
            cubeMapShader.setUniform("skyBox", skyBoxTexture!!)
            skyBox!!.render(cubeMapShader)
            glDepthMask(true)

            glDepthFunc(GL_LESS) //GLError.checkThrow()
            staticShader.use()
            localCam!!.bind(staticShader)
        }

        blendingShader.use()

        //localCam
        localCam!!.bind(blendingShader)
        // bind lights
        for (pointLight in pointLightList) {
            pointLight.bind(blendingShader)
        }
        blendingShader.setUniform("numPointLights", pointLightList.size)
        for (spotLight in spotLightList) {
            spotLight.bind(blendingShader, localCam!!.calculateViewMatrix())
        }
        blendingShader.setUniform("numSpotLights", spotLightList.size)

        if(groundMaterial != null){
            groundDiff2!!.bind(3)
            blendingShader.setUniform("secondTexture", 3)
            blendMap!!.bind(4)
            blendingShader.setUniform("blendMap", 4)
            ground.render(blendingShader)
        }
        staticShader.use()

        // bind lights
        for (pointLight in pointLightList) {
            pointLight.bind(staticShader)
        }
        staticShader.setUniform("numPointLights", pointLightList.size)
        for (spotLight in spotLightList) {
            spotLight.bind(staticShader, localCam!!.calculateViewMatrix())
        }
        staticShader.setUniform("numSpotLights", spotLightList.size)

        // render objects
            //dragon
        staticShader.setUniform("shadingColor", Vector3f(1.0f,1.0f,1.0f))
        dragon.render(staticShader)
        staticShader.setUniform("shadingColor", Vector3f(1.0f,0.0f,0.0f))
            //tower
        staticShader.setUniform("shadingColor", Vector3f(1.0f,1.0f,1.0f))
        tower.render(staticShader)
        tower1.render(staticShader)
            //cake
        if (checkRings()){
            staticShader.setUniform("shadingColor", Vector3f(1.0f,1.0f,1.0f))
            cake.render(staticShader)
        }
            //Ring
        val x = Math.abs(Math.sin(t/2))
        val y = x
        val ringColor = Vector3f(x,y,0.0f)
        staticShader.setUniform("shadingColor", ringColor)
        for (elem in ringArray){
            if(!elem.getCollider().getBCollided()){
                elem.render(staticShader)
            }
        }
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
    fun checkRings(): Boolean{
        var ringsCollided = 0
        for (ring in ringArray){
            if (ring.getCollider().getBCollided() == true){
                ringsCollided++
            }
        }
        return ringArray.size == ringsCollided
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
        if (window.getKeyState(GLFW_KEY_Q)) {
            dragon.rotate(0.0f,0.0f,-dt * rotateMul)
        }
        if (window.getKeyState(GLFW_KEY_E)) {
            dragon.rotate(0.0f, 0.0f, dt * rotateMul)
        }
        if (window.getKeyState(GLFW_KEY_SPACE)) {
            dragon.translate(Vector3f(0.0f,dt * moveMul+(-gravity),0.0f))
        }
        if (window.getKeyState(GLFW_KEY_LEFT_SHIFT)) {
            dragon.translate(Vector3f(0.0f,-dt * moveMul,0.0f))
        }
        if(!window.getKeyState(GLFW_KEY_W) &&  !window.getKeyState(GLFW_KEY_SPACE) &&  !window.getKeyState(GLFW_KEY_S)){
            if(dragon.getPosition().y >= ground.getPosition().y){
                dragon.translate(Vector3f(0.0f,gravity,0.0f))
            }
        }
        if(window.getKeyState(GLFW_KEY_1)){
            camCount = 0
        }
        if(window.getKeyState(GLFW_KEY_2)){
            camCount = 1
        }
        if(window.getKeyState(GLFW_KEY_3)){
            if (localCam != null) {
                localCam!!.fov = Math.toRadians(25.0f)
            }
        }
        if(window.getKeyState(GLFW_KEY_4)){
            if (localCam != null){
                localCam!!.fov = Math.toRadians(90.0f)
            }
        }

        //collision detection
        checkCollision(colliderArrray)

        //animation
        if (checkRings()){
            cake.rotate(0.0f,0.01f,0.0f)
        }
        ring4.rotateAroundPoint(0.0f,0.01f,0.0f,tower1.getPosition())

        for (elem in ringArray){
            if(!elem.getCollider().getBCollided()){
                elem.rotate(0.0f,0.0f, Math.toRadians(1.0f))
            }
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
                localCam!!.rotateAroundPoint(0.0f, yawAngle, 0.0f, Vector3f(0.0f, 0.0f, 0.0f))
            }
        } else firstMouseMove = false
        oldMouseX = xpos
        oldMouseY = ypos
    }
    fun onMouseScroll(xoffset: Double, yoffset: Double){
        /*
        if(localCam != null){
            localCam!!.fov -= Math.toRadians(yoffset).toFloat()
        }
        if (localCam!!.fov < 25.0f){
            localCam!!.fov = 25.0f;
        }
        if (localCam!!.fov > 100.0f){
            localCam!!.fov = 100.0f;
        }
        */
    }
    private fun loadGround(): Renderable{
        val groundDiff = Texture2D("assets/textures/ground/NatureGroundTexture.png", true) //rot.png
        groundDiff.setTexParams(GL_REPEAT, GL_REPEAT, GL_LINEAR_MIPMAP_LINEAR, GL_LINEAR)

        groundDiff2 = Texture2D("assets/textures/ground/groundMountainTexture.png", true) //grün.png
        groundDiff2!!.setTexParams(GL_REPEAT, GL_REPEAT, GL_LINEAR_MIPMAP_LINEAR, GL_LINEAR)

        blendMap = Texture2D("assets/textures/ground/blendMap.png", false)
        blendMap!!.setTexParams(GL_REPEAT, GL_REPEAT, GL_LINEAR, GL_LINEAR)

        groundMaterial = Material(groundDiff, defaultEmmitTex, defaultSpecTex, 60f, Vector2f(64.0f, 64.0f))

        //load an object and create a mesh
        val gres = loadOBJ("assets/models/NewGround.obj")
        //Create the mesh
        val atr1 = VertexAttribute(3, GL_FLOAT, stride, 0)     //position attribute
        val atr2 = VertexAttribute(2, GL_FLOAT, stride, 3 * 4) //texture coordinate attribute
        val atr3 = VertexAttribute(3, GL_FLOAT, stride, 5 * 4) //normal attribute
        val vertexAttributes = arrayOf(atr1, atr2, atr3)
        //Create renderable
        val render = Renderable()
        for (m in gres.objects[0].meshes) {
            val mesh = Mesh(m.vertexData, m.indexData, vertexAttributes, groundMaterial)
            render.meshes.add(mesh)
        }
        return render
    }
    private fun loadRing(): Renderable{
        val ringOBJ = loadOBJ("assets/models/ring.obj")

        val ringTex = Texture2D("assets/textures/ring.png", true)
            ringTex.setTexParams(GL_REPEAT, GL_REPEAT, GL_LINEAR_MIPMAP_LINEAR, GL_LINEAR)
        val ringEmmit = Texture2D("assets/textures/ringEmmit2.png", true)
            ringEmmit.setTexParams(GL_REPEAT, GL_REPEAT, GL_LINEAR_MIPMAP_LINEAR, GL_LINEAR)

        val ringMat = Material(ringTex, ringEmmit, defaultSpecTex)

        val atr1 = VertexAttribute(3, GL_FLOAT, stride, 0)     //position attribute //38505
        val atr2 = VertexAttribute(2, GL_FLOAT, stride, 3 * 4) //texture coordinate attribute
        val atr3 = VertexAttribute(3, GL_FLOAT, stride, 5 * 4) //normal attribute

        val vertexAttribute = arrayOf(atr1, atr2, atr3)
        val render = Renderable()
        for (m in ringOBJ.objects[0].meshes) {
            val mesh = Mesh(m.vertexData, m.indexData, vertexAttribute, ringMat)
            render.meshes.add(mesh)
        }
        return render
    }

    private fun loadeCake(): Renderable{
        val ringOBJ = loadOBJ("assets/models/cake.obj")

        val cakeDiff = Texture2D("assets/textures/cake/cake_diff.png", true)
        cakeDiff.setTexParams(GL_REPEAT, GL_REPEAT, GL_LINEAR_MIPMAP_LINEAR, GL_LINEAR)
        val cakeSpec = Texture2D("assets/textures/cake/cake_spec.png", true)
        cakeSpec.setTexParams(GL_REPEAT, GL_REPEAT, GL_LINEAR_MIPMAP_LINEAR, GL_LINEAR)

        val cakeMat = Material(cakeDiff, defaultEmmitTex, cakeSpec)

        val atr1 = VertexAttribute(3, GL_FLOAT, stride, 0)     //position attribute //38505
        val atr2 = VertexAttribute(2, GL_FLOAT, stride, 3 * 4) //texture coordinate attribute
        val atr3 = VertexAttribute(3, GL_FLOAT, stride, 5 * 4) //normal attribute

        val vertexAttribute = arrayOf(atr1, atr2, atr3)
        val render = Renderable()
        for (m in ringOBJ.objects[0].meshes) {
            val mesh = Mesh(m.vertexData, m.indexData, vertexAttribute, cakeMat)
            render.meshes.add(mesh)
        }
        return render
    }
    private fun loadDragon(): Renderable{
        val dragonOBJ = loadOBJ("assets/models/dragonCentered.obj")

        val dragonTex = Texture2D("assets/textures/dragon.png", true)
        dragonTex.setTexParams(GL_REPEAT, GL_REPEAT, GL_LINEAR_MIPMAP_LINEAR, GL_LINEAR)

        val dragonMat = Material(dragonTex,defaultEmmitTex,defaultSpecTex)

        val atr1 = VertexAttribute(3, GL_FLOAT, stride, 0)     //position attribute //38505
        val atr2 = VertexAttribute(2, GL_FLOAT, stride, 3 * 4) //texture coordinate attribute
        val atr3 = VertexAttribute(3, GL_FLOAT, stride, 5 * 4) //normal attribute
        val vertexAttributes = arrayOf(atr1, atr2, atr3)

        val render = Renderable()
        for (m in dragonOBJ.objects[0].meshes) {
            val mesh = Mesh(m.vertexData, m.indexData, vertexAttributes, dragonMat)
            render.meshes.add(mesh)
        }
        return render
    }
    private fun loadTower(): Renderable {
        val towerOBJ = loadOBJ("assets/models/towerNeu.obj")

        val towerDiff = Texture2D("assets/textures/TowerTextures/tower_square_7_Base_Color.png",  true)
        towerDiff.setTexParams(GL_REPEAT, GL_REPEAT, GL_LINEAR_MIPMAP_LINEAR, GL_LINEAR)
        val towerSpec = Texture2D("assets/textures/specDark.png", true)
        towerSpec.setTexParams(GL_REPEAT, GL_REPEAT, GL_LINEAR_MIPMAP_LINEAR, GL_LINEAR)

        val towerMat = Material(towerDiff,defaultEmmitTex,towerSpec)

        val atr1 = VertexAttribute(3, GL_FLOAT, stride, 0)     //position attribute //38505
        val atr2 = VertexAttribute(2, GL_FLOAT, stride, 3 * 4) //texture coordinate attribute
        val atr3 = VertexAttribute(3, GL_FLOAT, stride, 5 * 4) //normal attribute
        val vertexAttributes = arrayOf(atr1, atr2, atr3)

        val render = Renderable()
        for (m in towerOBJ.objects[0].meshes) {
            val mesh = Mesh(m.vertexData, m.indexData, vertexAttributes, towerMat)
            render.meshes.add(mesh)
        }
        return render
    }
    fun loadSkyBox(){
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
    fun checkCollision(colliderArray : Array<CollisionCircle>) {
        var bAllowedToCollide: Boolean

        for ((index, elem) in colliderArray.withIndex()) {
            for ((index2, element) in colliderArray.withIndex()) {
                if (index + index2 +1>= colliderArray.size) {
                    break
                }
                val elem2 = colliderArray[index + index2 +1]
                val distance = elem.getOwnerPosition().distance(elem2.getOwnerPosition())
                val totalRadius = elem.getRadius() + elem2.getRadius()
                if (distance < totalRadius) {
                    if ((elem.getOwner() == dragon) || (elem2.getOwner() == dragon)) {
                        bAllowedToCollide = (elem.getBIsAllowedToCollide() && elem2.getBIsAllowedToCollide())
                        if (!bAllowedToCollide) {
                            dragonToSpawn()
                        } else {
                            for (ringElem in ringArray) {
                                if ((elem.getOwner() == ringElem)) {
                                    elem.setBCollided(true)
                                }else{
                                    elem2.setBCollided(true)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun dragonToSpawn() {
        System.out.println("Touched the tower")
        dragon.translate(spawn.sub(dragon.getPosition()))
    }
    fun cleanup() {}
}