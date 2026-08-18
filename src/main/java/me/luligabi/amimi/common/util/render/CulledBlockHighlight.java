package me.luligabi.amimi.common.util.render;

import brachy.modularui.drawable.schema.BlockHighlight;
import brachy.modularui.utils.Color;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import org.joml.Vector3f;

/*
 * This file is adapted code originally part of Modular UI Modern, hosted at https://github.com/brachy84/ModularUI-Modern
 *
 * This file is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This file is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program. If not, see
 * <https://www.gnu.org/licenses/lgpl-3.0.html>.
 */
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