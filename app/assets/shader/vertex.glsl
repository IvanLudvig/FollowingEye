attribute vec3 a_position;
attribute vec3 a_normal;
attribute vec2 a_texCoord0;

uniform mat4 u_worldTrans;
uniform mat4 u_projViewTrans;

varying vec2 v_texCoords;
varying vec3 v_vertPosWorld;
varying vec3 v_vertNVWorld;

void main()
{
    vec4 vertPos   = u_worldTrans * vec4(a_position, 1.0);
    v_vertPosWorld = vertPos.xyz;
    v_vertNVWorld  = normalize(mat3(u_worldTrans) * a_normal);
    v_texCoords    = a_texCoord0;
    gl_Position    = u_projViewTrans * vertPos;
}