varying vec2  v_texCoords;
varying float v_lightIntensity;

uniform sampler2D u_texture;

void main()
{
    vec4 texCol  = texture( u_texture, v_texCoords.st );
    gl_FragColor = vec4( texCol.rgb * v_lightIntensity, 1.0 );
}