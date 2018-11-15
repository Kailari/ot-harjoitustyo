#version 150

in vec2 in_pos;
in vec2 in_uv;

uniform mat4 projection;
uniform mat4 model;
uniform mat4 view;

out vec2 v_uv;

void main(void) {
    mat4 mvp = projection * view * model;
    gl_Position = mvp * vec4(in_pos.x, in_pos.y, 0.0, 1.0);

    v_uv = in_uv;
}