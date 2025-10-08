#version 150

// DiffuseSampler: The main color texture of the scene.
//   - This is the rendered image before post-processing.
uniform sampler2D DiffuseSampler;

// DarknessLevel: Controls how dark the outside area gets (0.0 = no darkness, 1.0 = fully black).
//   - Higher values make the darkness outside the spotlight stronger.
//   - Negative values will brighten instead of darken (not recommended).
uniform float DarknessLevel;

// SpotlightRadius: The radius of the spotlight in normalized screen-space units (0.0 to 1.0).
//   - Controls how far from the player the light reaches.
//   - Larger values = bigger lit area; smaller = tighter spotlight.
//   - Negative values invert the effect (darkness at center, light outside).
uniform float SpotlightRadius;

// SpotlightIntensity: Controls the strength of the spotlight (how much it "punches" through darkness).
//   - Higher values make the spotlight brighter and more focused.
//   - Negative values invert the spotlight (dark at center, light at edges).
uniform float SpotlightIntensity;

// texCoord: The current fragment's texture coordinate (0.0 to 1.0).
//   - Used to determine where on the screen this pixel is.
in vec2 texCoord;

// oneTexel: The size of one texel in texture coordinates (not used here).
in vec2 oneTexel;

// fragColor: The output color of this pixel after all effects.
out vec4 fragColor;

void main() {
    // Get the original pixel color from the scene.
    vec4 color = texture(DiffuseSampler, texCoord);
    
    // Calculate distance from center of screen (approximate player position).
    //   - center is assumed to be the player's position in normalized screen space.
    //   - If you want to use the actual player position, pass it as a uniform.
    vec2 center = vec2(0.5, 0.5);
    float distance = length(texCoord - center);
    
    // Create spotlight effect:
    //   - 1.0 at center (player), 0.0 at edges (outside spotlight radius).
    //   - smoothstep creates a smooth transition at the edge of the spotlight.
    //   - Negative SpotlightRadius inverts the effect (spotlight outside, darkness at center).
    float spotlight = 1.0 - smoothstep(0.0, SpotlightRadius, distance);
    spotlight = pow(spotlight, 2.0) * SpotlightIntensity;
    
    w// Calculate darkness:
    //   - Inside the spotlight (near player): darkness is low.
    //   - Outside the spotlight: darkness approaches DarknessLevel.
    //   - darkness = DarknessLevel when spotlight = 0 (outside radius).
    //   - darkness = 0 when spotlight = 1 (at player).
    float darkness = mix(DarknessLevel, 0.0, spotlight);
    
    // Apply darkness effect:
    //   - Multiplies the color by (1.0 - darkness).
    //   - At full darkness (darkness=1), color is black.
    //   - At no darkness (darkness=0), color is unchanged.
    vec3 darkened = color.rgb * (1.0 - darkness);
    
    // Desaturate based on distance from center:
    //   - More desaturated (grayscale) as you move away from the player.
    //   - gray is the luminance value.
    //   - spotlight * 0.7 controls how much color is preserved inside the spotlight.
    float gray = dot(darkened, vec3(0.299, 0.587, 0.114));
    vec3 desaturated = mix(vec3(gray), darkened, spotlight * 0.7);
    
    // Output the final color.
    fragColor = vec4(desaturated, color.a);
}
