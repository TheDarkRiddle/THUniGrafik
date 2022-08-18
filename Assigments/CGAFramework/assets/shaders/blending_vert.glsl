#version 330 core

#define MAX_POINT_LIGHTS 10
#define MAX_SPOT_LIGHTS 10

layout(location = 0) in vec3 position;
layout(location = 1) in vec2 textureCoordinate;
layout(location = 2) in vec3 normal;

// uniforms
uniform mat4 model_matrix;
uniform mat4 view_matrix;
uniform mat4 proj_matrix;

uniform vec2 tcMultiplier;

// lights
struct PointLight
{
    vec3 Color;
    vec3 Position;
};
struct SpotLight
{
    vec3 Color;
    vec3 Position;
    vec2 Cone;
    vec3 Direction;
};
uniform PointLight pointLight[MAX_POINT_LIGHTS];
uniform int numPointLights;
uniform SpotLight spotLight[MAX_SPOT_LIGHTS];
uniform int numSpotLights;

// out data
out struct VertexData
{
    vec2 textureCoordinate;
    vec2 blendMapCord;
    vec3 normal;
    bool istSchraege;
    vec3 rawNormal;
//light stuff
    vec3 toCamera;
    vec3 toPointLight[MAX_POINT_LIGHTS];
    vec3 toSpotLight[MAX_SPOT_LIGHTS];
} vertexData;

void main(){
    mat4 modelview = view_matrix * model_matrix;
    vec4 viewpos = modelview * vec4(position, 1.0f);// vertex position in viewspace

    // TODO 4.2.3 Vektor von Vertex Position zur Camera Position (im Viewspace)
    vertexData.toCamera = -viewpos.xyz;

    for (int i = 0; i < numPointLights; i++){
        // TODO 4.2.3 Vektor von Vertex Position zur Punktlicht Position (im Viewspace)
        vertexData.toPointLight[i] = (view_matrix * vec4(pointLight[i].Position, 1.0f) - viewpos).xyz;
    }
    for (int i = 0; i < numSpotLights; i++) {
        // TODO 4.3.3 Vektor von Vertex Position zur Scheinwerfer Position (im Viewspace)
        vertexData.toSpotLight[i] = (view_matrix * vec4(spotLight[i].Position, 1.0f) - viewpos).xyz;
    }
    gl_Position = proj_matrix * viewpos;

    /*float bigestNormal = normal.s;
    if(bigestNormal < normal.r){
        bigestNormal = normal.r;
    }
    if(bigestNormal < normal.x){
        bigestNormal = normal.x;
    }
    if(bigestNormal != normal.r){
        vertexData.istSchraege = true;
    }*/
    vertexData.rawNormal = normal;
    vertexData.normal = (inverse(transpose(modelview)) * vec4(normal, 0.0f)).xyz;
    vertexData.textureCoordinate = textureCoordinate* tcMultiplier;
    vertexData.blendMapCord = textureCoordinate;
}