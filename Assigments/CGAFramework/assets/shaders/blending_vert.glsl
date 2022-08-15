#version 330 core
layout(location = 1) in vec3 textureCordinates;

uniform mat4 view_matrix;
uniform mat4 proj_matrix;

out struct VertexData{
    vec3 textureCoordinate;
}vertexData;

void main() {

    vertexData.textureCoordinate = textureCordinates;
}
