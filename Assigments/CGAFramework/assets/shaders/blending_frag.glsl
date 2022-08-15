#version 330 core
    uniform sampler2D materialDiff;
    uniform sampler2D secondTexture;
    uniform sampler2D blendMap;

in struct VertexData {
    vec3 textureCoordinate;
} vertexData;
out vec4 color;

void main() {
    vec4 blendMapColor = texture(blendMap, VertexData.textureCoordinate);
    float mainTextureAmount = 1 - (blendMapColor.r + blendMapColor.g + blendMapColor.b);
    vec2 tiledTexCords = VertexData.textureCoordinate * 40.0f;

    vec4 mainTextureColor = texture( diffTexture , tiledTexCords) * mainTextureAmount;
    vec4 secondTextureColor = texture(secondTexture, tiledTexCords) * mainTextureAmount;
    color = mainTextureColor + secondTextureColor;
}
