#version 330 core

//input from vertex shader
in struct VertexData
{
    vec3 position;
    vec3 normals;
} vertexData;

//fragment shader output
out vec4 color;


void main(){

    color = vec4(0, (0.5f + abs(vertexData.position.z)), 0, 1.0f);
    //color = vec4(normalize(vertexData.normals).x, normalize(vertexData.normals).y, normalize(vertexData.normals).z, 1.0f);

}