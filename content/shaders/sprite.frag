#version 150

in vec2 v_uv;

uniform sampler2D in_texture;

out vec4 out_fragColor;

void main(void) {
    out_fragColor = texture(in_texture, v_uv);
    //out_fragColor = vec4(v_uv.x, v_uv.y, 0.0, 1.0);
}