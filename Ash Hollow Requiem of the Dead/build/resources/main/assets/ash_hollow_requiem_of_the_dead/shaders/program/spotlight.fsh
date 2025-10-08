#version 150

uniform sampler2D DiffuseSampler;
uniform sampler2D DepthSampler; // Added depth sampler for 3D info
uniform vec2 ScreenSize;
uniform vec2 PlayerPos;
uniform float SpotlightRadius;
uniform float PlayerDepth; // Player's depth value
uniform float SphereFalloff; // How quickly the sphere fades out

in vec2 texCoord;
out vec4 fragColor;

// Constants for tuning the effect
#define UI_BRIGHTNESS_THRESHOLD 0.7
#define DEPTH_RANGE 30.0 // Maximum depth difference to consider
#define MIN_SPHERE_BRIGHTNESS 0.2 // Minimum brightness at sphere edge

// Utility functions
float luminance(vec3 rgb) {
    return dot(rgb, vec3(0.2126, 0.7152, 0.0722));
}

// Calculates 3D distance using screen space and depth
float sphereDistance(vec2 screenPos, float pixelDepth, vec2 playerScreenPos, float playerDepth) {
    // Calculate 2D distance in screen space
    float screenDist = distance(screenPos, playerScreenPos);
    
    // Calculate depth difference (Z distance)
    float depthDiff = abs(pixelDepth - playerDepth);
    
    // Scale depth difference to be comparable with screen distance
    depthDiff *= DEPTH_RANGE;
    
    // Calculate 3D Euclidean distance using Pythagorean theorem
    return sqrt(screenDist * screenDist + depthDiff * depthDiff);
}

void main() {
    // Sample color and depth
    vec4 color = texture(DiffuseSampler, texCoord);
    float pixelDepth = texture(DepthSampler, texCoord).r;
    
    // Calculate brightness for UI detection
    float brightness = luminance(color.rgb);
    
    // Detect UI elements (very high brightness or zero depth)
    bool isUI = brightness > UI_BRIGHTNESS_THRESHOLD || pixelDepth >= 1.0;
    
    // Calculate 3D distance for spherical effect
    float distFromPlayer = sphereDistance(texCoord * ScreenSize, pixelDepth, PlayerPos, PlayerDepth);
    
    // Create a spherical spotlight effect with smooth falloff
    float spotlightFactor = 1.0 - smoothstep(SpotlightRadius * 0.7, SpotlightRadius, distFromPlayer);
    
    // Enhance the spherical effect by varying intensity based on 3D distance
    float sphericalIntensity = mix(MIN_SPHERE_BRIGHTNESS, 1.0, spotlightFactor);
    
    if (isUI) {
        // Preserve UI with slight desaturation
        float gray = luminance(color.rgb);
        fragColor = vec4(mix(vec3(gray), color.rgb, 0.3), color.a);
    }
    else if (spotlightFactor > 0.0) {
        // Inside spherical spotlight - grayscale with 3D falloff
        float gray = luminance(color.rgb);
        
        // Dim grayscale toward edges of sphere with 3D falloff
        gray *= sphericalIntensity;
        
        // Add slight bluish tint to simulate night vision
        vec3 finalColor = mix(vec3(gray), vec3(gray * 0.8, gray * 0.9, gray), 0.2);
        
        fragColor = vec4(finalColor, color.a);
    }
    else {
        // Outside spherical spotlight - PURE BLACK
        fragColor = vec4(0.0, 0.0, 0.0, color.a);
    }
}
#version 150

uniform sampler2D DiffuseSampler;
uniform vec2 ScreenSize;
uniform vec2 PlayerPos;
uniform float PlayerDepth;
uniform float SpotlightRadius;
uniform float SphereFalloff;

in vec2 texCoord;
in vec2 oneTexel;

out vec4 fragColor;

void main() {
    vec4 color = texture(DiffuseSampler, texCoord);
    
    // Calculate distance from current pixel to player position
    vec2 screenPos = texCoord * ScreenSize;
    float distance = length(screenPos - PlayerPos);
    
    // Apply spotlight effect - brighter near player, darker away
    float spotlight = 1.0 - smoothstep(SpotlightRadius * 0.7, SpotlightRadius, distance);
    spotlight = pow(spotlight, 2.0) * SphereFalloff;
    
    // Desaturate the color based on the spotlight intensity
    float gray = dot(color.rgb, vec3(0.299, 0.587, 0.114));
    vec3 desaturated = mix(vec3(gray), color.rgb, spotlight * 0.5);
    
    // Darken based on distance from spotlight center
    vec3 darkened = desaturated * max(0.05, spotlight);
    
    fragColor = vec4(darkened, color.a);
}