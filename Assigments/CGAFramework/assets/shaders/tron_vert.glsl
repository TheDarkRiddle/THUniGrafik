#version 330 core

layout(location = 0) in vec3 position;
layout(location = 2) in vec3 normals;

//uniforms
uniform mat4 model_matrix;
uniform mat4 view_matrix;
uniform mat4 projection_matrix;

//Textures
uniform int m_emit;
uniform int m_tcMultiplierX;
uniform int m_tcMultiplierY;

out struct VertexData
{
    vec3 position;
    vec3 normals;
} vertexData;

// translation object to world
void main(){
    vec4 pos = view_matrix * model_matrix * vec4(position, 1.0f);

    gl_Position = projection_matrix * pos;
    //gl_Position = vec4(pos.xy, -pos.z, 1.0f);
    vertexData.position = pos.xyz;
    //inverse(transpose dazu da damit die Normalen nach dem transform in die Richtige richtungen zeigen
    vertexData.normals = (inverse(transpose(view_matrix * model_matrix)) * vec4(normals,0.0f)).xyz;
}
