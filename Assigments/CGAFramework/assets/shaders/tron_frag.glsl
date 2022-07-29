#version 330 core
// --- constants
#define GAMMA 2.2f
#define INV_GAMMA (1.0f / 2.2f)
#define PI 3.14159265359f
#define INV_PI (1.0f / PI)

#define MAX_POINT_LIGHTS 10
#define MAX_SPOT_LIGHTS 10

// --- input from vertex shader
in struct VertexData
{
    vec2 textureCoordinate;
    vec3 normal;
    //light stuff
    vec3 toCamera;
    vec3 toPointLight[MAX_POINT_LIGHTS];
    vec3 toSpotLight[MAX_SPOT_LIGHTS];
} vertexData;

// --- uniforms
uniform sampler2D materialDiff;
uniform sampler2D materialSpec;
uniform sampler2D materialEmit;
uniform float materialShininess;

uniform vec3 shadingColor;

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

// --- fragment shader output
out vec4 color;

vec3 getPointLightIntensity(vec3 color, vec3 toLightVector)
{
    // TODO 4.4.1 Lichtfarbe quadratisch abschwächen
    return color / pow(length(toLightVector),2);
}

vec3 getSpotLightIntensity(vec3 color, vec3 toLightVector, vec3 lightdir, vec2 cone)
{
    // x = innen cone
    // y = ausen cone
    // TODO 4.3.3 Lichtintensität entsprechend des Lichtkegels berechnen

    float I = clamp((dot(normalize(toLightVector), -lightdir) - cos(cone.y)) / (cos(cone.x) - cos(cone.y)),0.0f,1.0f);
    // TODO 4.4.1 Lichtfarbe quadratisch abschwächen
    return (color * I)/ pow(length(toLightVector),2);
}

vec3 shade(vec3 N, vec3 L, vec3 V, vec3 diffc, vec3 specc, float shn)
{
    // TODO 4.3.3 Phong Model (diff + spec) implementieren
    //diffuse
    vec3 diff = diffc * max(dot(N, L),0);

    //specular
    vec3 reflectDir = normalize(reflect(-L, N));
    vec3 specular = specc * pow(max(dot(V, reflectDir),0),shn);

    return (diff + specular);
}

vec3 shadeBlinn(vec3 N, vec3 L, vec3 V, vec3 diffc, vec3 specc, float shn)
{
    //return diffc;
    // TODO: 4.8 Blinn-Phong Model (diff + spec) implementieren
    float NdotL = max(dot(N, L),0);  // cos alpha of N and L
    vec3 H = normalize(L+V);  // normalized half vector between V and L
    float HdotN = dot(H, N);  // cos beta of H and N
    return diffc * NdotL + specc * pow(max(HdotN, 0),shn);
}

vec3 gammaCorrect(vec3 clinear)
{
    return pow(clinear, vec3(INV_GAMMA));
    // TODO 4.4.2 Konvertieren aus linearem in gamma-korrigierten Farbraum. Nutze die oben definierten Konstanten
}

vec3 invGammaCorrect(vec3 cgamma)
{
    return pow(cgamma,vec3(GAMMA));
    // TODO 4.4.2 Konvertieren aus gamma-korrigiertem in linearen Farbraum. Nutze die oben definierten Konstanten
}

void main(){
    // convert textures to linear color space
    vec3 diffColor = invGammaCorrect(texture(materialDiff, vertexData.textureCoordinate).rgb);
    vec3 specColor = invGammaCorrect(texture(materialSpec, vertexData.textureCoordinate).rgb);
    vec3 emitColor = invGammaCorrect(texture(materialEmit, vertexData.textureCoordinate).rgb);

    vec3 emit_term = emitColor * shadingColor;
    vec3 final_color = emit_term;  // this var collects the shading from all light sources

    vec3 N = normalize(vertexData.normal);
    vec3 V = normalize(vertexData.toCamera);

    // point lights
    for (int i = 0; i < numPointLights; i++) {
        vec3 Lpl = normalize(vertexData.toPointLight[i]);
        //vec3 pshade = shade(N, Lpl, V, diffColor, specColor, materialShininess);  // Phong shading
        // TODO 4.8 enable:
        vec3 pshade = shadeBlinn(N, Lpl, V, diffColor, specColor, materialShininess*2);  // Blinn-Phong shading
        vec3 intPointLight = getPointLightIntensity(pointLight[i].Color, vertexData.toPointLight[i]);

        // TODO: 4.2.3 enable:
        final_color += pshade * intPointLight;
    }

    // spot lights
    for (int i = 0; i < numSpotLights; i++) {
        vec3 Lsl = normalize(vertexData.toSpotLight[i]);
        //vec3 sshade = shade(N, Lsl, V, diffColor, specColor, materialShininess);  // Phong shading
        // TODO 4.8 enable:
        vec3 sshade = shadeBlinn(N, Lsl, V, diffColor, specColor, materialShininess*2);  // Blinn-Phong shading
        vec3 intSpotLight = getSpotLightIntensity(spotLight[i].Color, vertexData.toSpotLight[i], spotLight[i].Direction, spotLight[i].Cone);

        // TODO: 4.3.3 enable:
        final_color += sshade * intSpotLight;
    }

    // return gamma corrected color
    color = vec4(gammaCorrect(final_color), 1.0f);
}