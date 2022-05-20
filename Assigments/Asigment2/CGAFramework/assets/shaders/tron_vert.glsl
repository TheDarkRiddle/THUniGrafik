#version 330 core

layout(location = 0) in vec3 position;

//uniforms
// translation object to world
uniform mat4 model_matrix;

out struct VertexData
{
    vec3 position;
} vertexData;

//
void main(){
    vec4 pos = model_matrix * vec4(position, 1.0f);

    gl_Position = vec4(pos.xy, -pos.z, 1.0f);
    vertexData.position = pos.xyz;
}
