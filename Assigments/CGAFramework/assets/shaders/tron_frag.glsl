#version 330 core

//input from vertex shader
in struct VertexData
{
    vec3 position;
    vec3 normalsofV;
} vertexData;

//fragment shader output
out vec4 color;


void main(){

    color = vec4(0, (0.5f + abs(vertexData.position.z)), 0, 1.0f);
    //color = vec4(normalize(vertexData.normalsofV).x, normalize(vertexData.normalsofV).y, normalize(vertexData.normalsofV).z, 1.0f);

}