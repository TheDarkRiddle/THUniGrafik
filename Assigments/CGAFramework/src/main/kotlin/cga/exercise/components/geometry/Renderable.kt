package cga.exercise.components.geometry

import cga.exercise.components.shader.ShaderProgram


class Renderable(var meshes: MutableList<Mesh> ) : IRenderable, Transformable() {
    override fun render(shaderProgram: ShaderProgram) {

        shaderProgram.setUniform("model_matrix",getWorldModelMatrix(),false)
        for(m in meshes)
        {
            m.render();
        }
    }
}