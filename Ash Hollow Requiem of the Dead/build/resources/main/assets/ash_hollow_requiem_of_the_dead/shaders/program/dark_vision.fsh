#version 150

uniform sampler2D DiffuseSampler;
uniform float Saturation;
uniform float DarkGamma;
uniform float SpotlightRadius; // 0.0-0.5 (screen space, 0.5 = full screen)
uniform float FadeWidth; // width of the smooth fade region

in vec2 texCoord;
out vec4 fragColor;

// Convert RGB to luminance (perceived brightness)
float luminance(vec3 rgb) {
    return dot(rgb, vec3(0.2126, 0.7152, 0.0722));
}

void main() {
    // Sample the texture
    vec4 color = texture(DiffuseSampler, texCoord);

    // Get luminance (brightness)
    float lum = luminance(color.rgb);

    // Calculate distance from center of screen
    vec2 center = vec2(0.5, 0.5);
    float dist = distance(texCoord, center);

    // Fade: 1.0 inside radius, 0.0 outside (with smoothstep for soft edge)
    float fade = 1.0 - smoothstep(SpotlightRadius, SpotlightRadius + FadeWidth, dist);

    // Desaturate: mix original color and grayscale based on Saturation
    vec3 grey = vec3(lum * DarkGamma);
    vec3 desatColor = mix(grey, color.rgb, Saturation);

    // Fade to black outside the radius
    vec3 finalColor = mix(vec3(0.0), desatColor, fade);
    float finalAlpha = color.a;
    fragColor = vec4(finalColor, finalAlpha);
}
