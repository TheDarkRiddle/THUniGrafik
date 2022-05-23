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
    float x = vertexData.position.x;
    float y = vertexData.position.y;
    float z = vertexData.position.z;
    vertexData.position.x = x*model_matrix[0,0] + x*model_matrix[0,1] + x*model_matrix[0,2] + x*model_matrix[0,3];
    vertexData.position.y = y*model_matrix[1,0] + y*model_matrix[1,1] + x*model_matrix[1,2] + y*model_matrix[1,3];
    vertexData.position.z = y*model_matrix[2,0] + z*model_matrix[2,1] + z*model_matrix[2,2] + z*model_matrix[2,3];
}