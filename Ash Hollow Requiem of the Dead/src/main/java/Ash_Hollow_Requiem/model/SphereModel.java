package Ash_Hollow_Requiem.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import org.joml.Matrix4f;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.slf4j.Logger;
import com.mojang.logging.LogUtils;

/**
 * A model for rendering a 3D sphere
 */
public class SphereModel {
    private static final Logger LOGGER = LogUtils.getLogger();
    
    // Sphere resolution parameters
    private static final int LATITUDE_SEGMENTS = 16;
    private static final int LONGITUDE_SEGMENTS = 24;
    
    // Texture for the sphere (can be null for untextured)
    private final ResourceLocation texture;
    
    public SphereModel(ResourceLocation texture) {
        this.texture = texture;
    }
    
    /**
     * Render the sphere
     * 
     * @param poseStack Matrix stack for transformations
     * @param buffer Vertex consumer for rendering
     * @param packedLight Lighting value
     * @param radius Radius of the sphere
     * @param color Color of the sphere (r, g, b, a from 0-1)
     * @param renderType RenderType to use (can be null to use default)
     */
    public void render(PoseStack poseStack, VertexConsumer buffer, int packedLight, 
                       float radius, float[] color, RenderType renderType) {
        // Store and apply matrix transformations
        poseStack.pushPose();
        
        // Use resolution from config if available
        int latitudeSegments = LATITUDE_SEGMENTS;
        int longitudeSegments = LONGITUDE_SEGMENTS;
        
        try {
            if (Ash_Hollow_Requiem.Config.spotlightResolution > 0) {
                latitudeSegments = Ash_Hollow_Requiem.Config.spotlightResolution;
                longitudeSegments = latitudeSegments * 3 / 2; // 1.5x more longitude segments for better aspect ratio
            }
        } catch (Exception e) {
            LOGGER.warn("Could not access spotlight resolution config, using defaults");
        }
        
        // Get the model matrix after all transforms
        Matrix4f matrix = poseStack.last().pose();
        
        // Default color to white if not specified
        if (color == null) {
            color = new float[] {1.0f, 1.0f, 1.0f, 0.5f};
        }
        
        // Generate the sphere geometry
        for (int lat = 0; lat < latitudeSegments; lat++) {
            float theta1 = (float) lat * (float) Math.PI / latitudeSegments;
            float theta2 = (float) (lat + 1) * (float) Math.PI / latitudeSegments;
            
            for (int lon = 0; lon < longitudeSegments; lon++) {
                float phi1 = (float) lon * 2.0f * (float) Math.PI / longitudeSegments;
                float phi2 = (float) (lon + 1) * 2.0f * (float) Math.PI / longitudeSegments;
                
                // Calculate the four corners of the quad
                float x1 = Mth.sin(theta1) * Mth.cos(phi1);
                float y1 = Mth.cos(theta1);
                float z1 = Mth.sin(theta1) * Mth.sin(phi1);
                
                float x2 = Mth.sin(theta1) * Mth.cos(phi2);
                float y2 = Mth.cos(theta1);
                float z2 = Mth.sin(theta1) * Mth.sin(phi2);
                
                float x3 = Mth.sin(theta2) * Mth.cos(phi2);
                float y3 = Mth.cos(theta2);
                float z3 = Mth.sin(theta2) * Mth.sin(phi2);
                
                float x4 = Mth.sin(theta2) * Mth.cos(phi1);
                float y4 = Mth.cos(theta2);
                float z4 = Mth.sin(theta2) * Mth.sin(phi1);
                
                // Calculate texture coordinates
                float u1 = (float) lon / longitudeSegments;
                float v1 = (float) lat / latitudeSegments;
                float u2 = (float) (lon + 1) / longitudeSegments;
                float v2 = (float) (lat + 1) / latitudeSegments;
                
                // Add the first triangle of the quad (1-2-3)
                addVertex(buffer, matrix, x1 * radius, y1 * radius, z1 * radius, u1, v1, packedLight, color);
                addVertex(buffer, matrix, x2 * radius, y2 * radius, z2 * radius, u2, v1, packedLight, color);
                addVertex(buffer, matrix, x3 * radius, y3 * radius, z3 * radius, u2, v2, packedLight, color);
                
                // Add the second triangle of the quad (1-3-4)
                addVertex(buffer, matrix, x1 * radius, y1 * radius, z1 * radius, u1, v1, packedLight, color);
                addVertex(buffer, matrix, x3 * radius, y3 * radius, z3 * radius, u2, v2, packedLight, color);
                addVertex(buffer, matrix, x4 * radius, y4 * radius, z4 * radius, u1, v2, packedLight, color);
            }
        }
        
        // Restore matrix stack
        poseStack.popPose();
    }
    
    /**
     * Add a vertex to the buffer
     */
    private void addVertex(VertexConsumer buffer, Matrix4f matrix, 
                          float x, float y, float z, 
                          float u, float v, 
                          int packedLight, float[] color) {
        float nx = x; // Normalized position for normal
        float ny = y;
        float nz = z;
        float len = (float) Math.sqrt(nx * nx + ny * ny + nz * nz);
        if (len > 0) {
            nx /= len;
            ny /= len;
            nz /= len;
        }
        
        buffer.vertex(matrix, x, y, z)
              .color(color[0], color[1], color[2], color[3])
              .uv(u, v)
              .overlayCoords(OverlayTexture.NO_OVERLAY)
              .uv2(packedLight)
              .normal(nx, ny, nz) // Properly normalized normal vector
              .endVertex();
    }
    
    /**
     * Get the texture for this model
     */
    public ResourceLocation getTexture() {
        return texture;
    }
}
