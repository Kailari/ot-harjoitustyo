#version 150

in vec2 v_uv;

uniform sampler2D in_texture;

out vec4 out_fragColor;

void main(void) {
    out_fragColor = texture(in_texture, v_uv);
}