attribute vec3 a_position;
attribute vec3 a_normal;
attribute vec2 a_texCoord0;

uniform mat4 u_worldTrans;
uniform mat4 u_projViewTrans;
uniform vec3 lightPosition;

varying vec2  v_texCoords;
varying float v_lightIntensity;

void main()
{
    vec4 vertPos       = u_worldTrans * vec4(a_position, 1.0);
    vec3 normal        = normalize(mat3(u_worldTrans) * a_normal);
    vec3 toLightVector = normalize(lightPosition - vertPos.xyz);
    v_lightIntensity   = max( 0.3, dot(normal, toLightVector));
    v_texCoords        = a_texCoord0;
    gl_Position        = u_projViewTrans * vertPos;
}