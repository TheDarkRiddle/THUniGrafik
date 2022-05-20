#version 330 core

//input from vertex shader
in struct VertexData
{
    vec3 position;
} vertexData;

//fragment shader output
out vec4 color;


void main(){

    color = vec4(0, (0.5f + abs(vertexData.position.z)), 0, 1.0f);

}