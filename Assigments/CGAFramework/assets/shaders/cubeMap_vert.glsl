#version 330 core
layout(location = 0) in vec3 position;

uniform mat4 view_matrix;
uniform mat4 proj_matrix;

out struct VertexData{
    vec3 textureCoordinate;
}vertexData;

void main() {

    vertexData.textureCoordinate = vec3(position.x, -position.y, position.z);
    vec4 tempPosition = proj_matrix * view_matrix * vec4(position, 0.0f);
    gl_Position = tempPosition.xyww;
}
