varying vec2 v_texCoords;
varying vec3 v_vertPosWorld;
varying vec3 v_vertNVWorld;

uniform sampler2D u_texture;

struct PointLight
{
    vec3 color;
    vec3 position;
    float intensity;
};
uniform PointLight u_pointLights[1];

void main()
{
    vec3  toLightVector  = normalize(u_pointLights[0].position - v_vertPosWorld.xyz);
    float lightIntensity = max( 0.0, dot(v_vertNVWorld, toLightVector));
    vec4  texCol         = texture( u_texture, v_texCoords.st );
    vec3  finalCol       = texCol.rgb * lightIntensity * u_pointLights[0].color;
    gl_FragColor         = vec4( finalCol.rgb * lightIntensity, 1.0 );
}