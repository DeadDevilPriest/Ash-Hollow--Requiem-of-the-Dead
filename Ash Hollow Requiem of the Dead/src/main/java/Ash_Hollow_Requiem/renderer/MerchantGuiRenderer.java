package Ash_Hollow_Requiem.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.util.RenderUtils;

/**
 * Renders merchant models in GUI without spawning entities
 * Similar to how the player is rendered in inventory
 */
public class MerchantGuiRenderer {

    private static final ResourceLocation TOKEN_MERCHANT_MODEL = new ResourceLocation("ash_hollow_requiem_of_the_dead", "geo/token_merchant.geo.json");
    private static final ResourceLocation TOKEN_MERCHANT_TEXTURE = new ResourceLocation("ash_hollow_requiem_of_the_dead", "textures/entities/token_merchant.png");

    private static final ResourceLocation LOOT_MERCHANT_MODEL = new ResourceLocation("ash_hollow_requiem_of_the_dead", "geo/loot_merchant.geo.json");
    private static final ResourceLocation LOOT_MERCHANT_TEXTURE = new ResourceLocation("ash_hollow_requiem_of_the_dead", "textures/entities/loot_merchant.png");

    /**
     * Simple class to hold rotation data
     */
    public static class MerchantRotation {
        public float bodyYaw = 0.0F;
        public float headYaw = 0.0F;

        public void updateFromMouse(int mouseX, int mouseY, int merchantX, int merchantY) {
            float deltaX = mouseX - merchantX;
            float angleToMouse = (float) Math.toDegrees(Math.atan2(deltaX, 50));

            // Body rotates slightly (max ±30 degrees)
            bodyYaw = Math.max(-30.0F, Math.min(30.0F, angleToMouse * 0.3F));

            // Head rotates more (max ±60 degrees)
            headYaw = Math.max(-60.0F, Math.min(60.0F, angleToMouse));
        }
    }

    /**
     * Renders a merchant model in the GUI
     * @param graphics GuiGraphics context
     * @param x Screen X position
     * @param y Screen Y position
     * @param scale Scale multiplier
     * @param rotation Rotation data (body and head)
     * @param isTokenMerchant True for token merchant, false for loot merchant
     */
    public static void renderMerchant(GuiGraphics graphics, int x, int y, float scale,
                                      MerchantRotation rotation, boolean isTokenMerchant) {
        PoseStack poseStack = graphics.pose();
        poseStack.pushPose();

        // Setup 3D rendering context
        poseStack.translate(x, y, 1050.0F);
        poseStack.scale(1.0F, 1.0F, -1.0F);

        RenderSystem.enableDepthTest();
        PoseStack mvStack = RenderSystem.getModelViewStack();
        mvStack.pushPose();
        mvStack.mulPoseMatrix(poseStack.last().pose());
        RenderSystem.applyModelViewMatrix();

        poseStack.pushPose();
        poseStack.scale(scale, scale, scale);

        // Apply body rotation
        poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(rotation.bodyYaw));

        // Get model and texture
        ResourceLocation modelLoc = isTokenMerchant ? TOKEN_MERCHANT_MODEL : LOOT_MERCHANT_MODEL;
        ResourceLocation textureLoc = isTokenMerchant ? TOKEN_MERCHANT_TEXTURE : LOOT_MERCHANT_TEXTURE;

        try {
            // Load the GeckoLib model
            BakedGeoModel model = software.bernie.geckolib.cache.GeckoLibCache.getBakedModels().get(modelLoc);

            if (model != null) {
                MultiBufferSource.BufferSource bufferSource = graphics.bufferSource();
                RenderType renderType = RenderType.entityCutoutNoCull(textureLoc);
                VertexConsumer buffer = bufferSource.getBuffer(renderType);

                // Find and rotate head bone if it exists
                GeoBone headBone = model.getBone("head").orElse(null);
                if (headBone != null) {
                    // Apply additional head rotation
                    float additionalHeadRotation = rotation.headYaw - rotation.bodyYaw;
                    headBone.setRotY((float) Math.toRadians(additionalHeadRotation));
                }

                // Render the model
                for (var bone : model.topLevelBones()) {
                    renderBone(poseStack, buffer, bone, 0xFFFFFFFF, 15728880);
                }

                bufferSource.endBatch();
            }
        } catch (Exception e) {
            // If GeckoLib rendering fails, render a simple fallback
            System.err.println("Failed to render merchant model: " + e.getMessage());
        }

        poseStack.popPose();

        mvStack.popPose();
        RenderSystem.applyModelViewMatrix();
        RenderSystem.disableDepthTest();
        poseStack.popPose();
    }

    /**
     * Recursively renders a bone and its children
     */
    private static void renderBone(PoseStack poseStack, VertexConsumer buffer,
                                   GeoBone bone, int packedColor, int packedLight) {
        poseStack.pushPose();

        RenderUtils.prepMatrixForBone(poseStack, bone);

        // Render cubes in this bone
        for (var cube : bone.getCubes()) {
            poseStack.pushPose();
            RenderUtils.translateToPivotPoint(poseStack, cube);

            for (var quad : cube.quads()) {
                var vertices = quad.vertices();

                // Calculate normal from the quad's vertices (cross product of two edges)
                var v0 = vertices[0].position();
                var v1 = vertices[1].position();
                var v2 = vertices[2].position();

                // Edge vectors
                float e1x = v1.x() - v0.x();
                float e1y = v1.y() - v0.y();
                float e1z = v1.z() - v0.z();

                float e2x = v2.x() - v0.x();
                float e2y = v2.y() - v0.y();
                float e2z = v2.z() - v0.z();

                // Cross product for normal
                float nx = e1y * e2z - e1z * e2y;
                float ny = e1z * e2x - e1x * e2z;
                float nz = e1x * e2y - e1y * e2x;

                // Normalize
                float length = (float) Math.sqrt(nx * nx + ny * ny + nz * nz);
                if (length > 0.0001f) {
                    nx /= length;
                    ny /= length;
                    nz /= length;
                }

                // Render all vertices with the calculated normal
                for (var vertex : vertices) {
                    var pos = vertex.position();
                    buffer.vertex(poseStack.last().pose(), pos.x(), pos.y(), pos.z())
                            .color(packedColor)
                            .uv(vertex.texU(), vertex.texV())
                            .overlayCoords(0)
                            .uv2(packedLight)
                            .normal(poseStack.last().normal(), nx, ny, nz)
                            .endVertex();
                }
            }

            poseStack.popPose();
        }

        // Render child bones recursively
        for (var childBone : bone.getChildBones()) {
            renderBone(poseStack, buffer, childBone, packedColor, packedLight);
        }

        poseStack.popPose();
    }
}