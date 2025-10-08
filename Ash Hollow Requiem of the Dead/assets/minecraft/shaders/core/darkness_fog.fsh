#version 120

// DiffuseSampler: The main color texture of the scene.
uniform sampler2D DiffuseSampler;

// InSize: The size of the input texture (width, height). Not used in this shader but can be used for screen-space calculations.
uniform vec2 InSize;

// PlayerPos: The player's position in normalized screen-space coordinates (0.0 to 1.0).
//   - (0.0, 0.0) is bottom-left, (1.0, 1.0) is top-right.
//   - Values outside [0,1] will move the spotlight off-screen.
uniform vec2 PlayerPos;

// SpotlightRadius: The radius of the spotlight in normalized screen-space units.
//   - Typical value: 0.15 (covers a small area around the player).
//   - Larger values make the spotlight bigger; smaller values make it tighter.
//   - Negative values invert the smoothstep, causing the spotlight to appear everywhere except near the player.
uniform float SpotlightRadius;

// SPOTLIGHT_RADIUS_BLOCKS: Spotlight radius in world blocks.
//   - Controls how far from the player (in blocks) the spotlight reaches.
//   - Larger values = bigger lit area in the world.
uniform float SPOTLIGHT_RADIUS_BLOCKS;

// SPOTLIGHT_RADIUS_PIXELS: Spotlight radius in screen pixels.
//   - Controls the radius of the spotlight in pixel units on the screen.
//   - Useful for UI or fixed-size effects regardless of zoom/FOV.
uniform float SPOTLIGHT_RADIUS_PIXELS;

varying vec2 texCoord; // The current fragment's texture coordinate (0.0 to 1.0).

void main() {
    vec4 color = texture2D(DiffuseSampler, texCoord);

    // depth: Approximate world-space depth for the current fragment.
    //   - Used to determine how far the pixel is from the camera.
    //   - Larger values mean farther away.
    float depth = gl_FragCoord.z / gl_FragCoord.w;

    // fogStart: Where fog begins (in world units, blocks). Set to 0.0 for immediate fog.
    // fogEnd: Where fog fully obscures (in world units, blocks). Set to 100.0 for full fog at 100 blocks.
    //   - Increasing fogEnd makes fog fade out farther away.
    //   - Decreasing fogEnd brings fog closer.
    //   - Negative values invert the fade direction (not recommended).
    float fogStart = 0.0;
    float fogEnd = 100.0;

    // fogFactor: 1.0 = no fog (close), 0.0 = full fog (far).
    //   - Values <0 clamp to 0 (full fog), >1 clamp to 1 (no fog).
    float fogFactor = clamp((fogEnd - depth) / (fogEnd - fogStart), 0.0, 1.0);

    // Calculate distance from this fragment to the player's position in screen space (normalized 0-1)
    float distNorm = distance(texCoord, PlayerPos);

    // Convert normalized distance to pixels
    float distPixels = distNorm * length(InSize);

    // Calculate spotlight mask using both block and pixel radii:
    // - Use world radius for in-game lighting, pixel radius for UI/fixed effects.
    // - Here, we combine both: spotlight is visible if within either radius.
    float spotlightBlock = 1.0 - smoothstep(0.0, SPOTLIGHT_RADIUS_BLOCKS / 100.0, distNorm); // assuming 100 blocks = full screen
    float spotlightPixel = 1.0 - smoothstep(0.0, SPOTLIGHT_RADIUS_PIXELS, distPixels);

    // Combine both spotlight effects (max: lit if inside either radius)
    float spotlight = max(spotlightBlock, spotlightPixel);

    // fogColor: The color of the fog/sky. Set to black (0,0,0).
    vec3 fogColor = vec3(0.0, 0.0, 0.0);

    // combinedFog: Blends fog and spotlight.
    //   - Inside spotlight: less fog (closer to fogFactor).
    //   - Outside spotlight: more fog (closer to 0, i.e., full fog).
    float combinedFog = mix(fogFactor, 0.0, 1.0 - spotlight);

    // finalColor: Blends the scene color with fog based on combinedFog.
    //   - combinedFog = 1.0: show scene color.
    //   - combinedFog = 0.0: show fog color (black).
    vec3 finalColor = mix(fogColor, color.rgb, combinedFog);

    gl_FragColor = vec4(finalColor, color.a);
}
