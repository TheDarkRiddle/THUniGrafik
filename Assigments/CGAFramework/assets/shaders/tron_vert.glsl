#version 330 core

layout(location = 0) in vec3 position;
layout(location = 2) in vec3 normalsofV;
//uniforms
uniform mat4 model_matrix;
// translation object to world
out struct VertexData
{
    vec3 position;
    vec3 normalsofV;
} vertexData;

//
void main(){
    vec4 pos = model_matrix * vec4(position, 1.0f);

    gl_Position = vec4(pos.xy, -pos.z, 1.0f);
    vertexData.position = pos.xyz;
}
