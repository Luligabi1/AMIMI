package me.luligabi.amimi.common.util.render;

import brachy.modularui.drawable.schema.BlockHighlight;
import brachy.modularui.utils.Color;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import org.joml.Vector3f;

public class CulledBlockHighlight extends BlockHighlight {

    public CulledBlockHighlight(int color, boolean allSides, float frameThickness) {
        super(color, allSides, frameThickness);
    }

    @Override
    public void renderHighlight(PoseStack poseStack, BlockPos pos, Direction direction, Vector3f camera) {
        RenderSystem.enableBlend();
        RenderSystem.setShader(GameRenderer::getPositionShader);
        RenderSystem.setShaderColor(1, 1, 1, 1);
        Color.setGlColor(this.color());

        poseStack.pushPose();
        poseStack.translate(pos.getX(), pos.getY(), pos.getZ());

        float distance = camera.distance(pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f);
        this.doRender(poseStack, direction, distance);

        poseStack.popPose();

        RenderSystem.enableCull();
    }
}