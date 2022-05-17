package cga.exercise.components.geometry

import cga.framework.Vertex
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL15
import org.lwjgl.opengl.GL20
import org.lwjgl.opengl.GL30


/**
 * Creates a Mesh object from vertexdata, intexdata and a given set of vertex attributes
 *
 * @param vertexdata plain float array of vertex data
 * @param indexdata  index data
 * @param attributes vertex attributes contained in vertex data
 * @throws Exception If the creation of the required OpenGL objects fails, an exception is thrown
 *
 * Created by Fabian on 16.09.2017.
 */
class Mesh(vertexdata: FloatArray, indexdata: IntArray, attributes: Array<VertexAttribute>) {
    //private data
    private var vao = 0
    private var vbo = 0
    private var ibo = 0
    private var indexcount = 0

    init {
        indexcount = indexdata.size;

        //____VAO Binding____
        vao = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(vao);

        //____VBO____
        vbo = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertexdata,GL15.GL_STATIC_DRAW);

        //____IBO____
        ibo = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, ibo);
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indexdata,GL15.GL_STATIC_DRAW);

        //____VAO____
        attributes.forEachIndexed{ index, item->
            GL20.glVertexAttribPointer(index, item.n, item.type, false, item.stride, item.offset.toLong())
            GL20.glEnableVertexAttribArray(index)
        }

        // ____Unbinde____
        GL30.glBindVertexArray(0);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0)
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0)

    }

    /**
     * renders the mesh
     */
    fun render() {
        GL30.glBindVertexArray(vao);
        GL11.glDrawElements(GL11.GL_TRIANGLES, indexcount,GL11.GL_UNSIGNED_INT,0)
        GL30.glBindVertexArray(0);
    }

    /**
     * Deletes the previously allocated OpenGL objects for this mesh
     */
    fun cleanup() {
        if (ibo != 0) GL15.glDeleteBuffers(ibo)
        if (vbo != 0) GL15.glDeleteBuffers(vbo)
        if (vao != 0) GL30.glDeleteVertexArrays(vao)
    }
}