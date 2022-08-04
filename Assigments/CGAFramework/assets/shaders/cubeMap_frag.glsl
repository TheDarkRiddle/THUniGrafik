#version 330 core

in struct VertexData {
    vec3 textureCoordinate;
} vertexData;
out vec4 color;

uniform saplerCube skybox;

void main() {
    color = texture(skybox, vertexData.textureCoordinate);
}
