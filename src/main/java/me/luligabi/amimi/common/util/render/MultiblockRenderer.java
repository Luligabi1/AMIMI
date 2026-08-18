package me.luligabi.amimi.common.util.render;

import brachy.modularui.drawable.SchemaRenderer;
import brachy.modularui.drawable.schema.BlockHighlight;
import brachy.modularui.screen.viewport.GuiContext;
import brachy.modularui.theme.WidgetTheme;
import brachy.modularui.utils.Color;
import brachy.modularui.widget.sizer.Area;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;
import me.luligabi.amimi.client.ClientConfig;
import me.luligabi.amimi.common.mixin.BaseSchemaRendererAccessor;
import me.luligabi.amimi.common.util.HatchUtils;
import me.luligabi.amimi.common.util.gregtech.BlockInfo;
import me.luligabi.amimi.common.util.gregtech.MultiblockSchemaInfo;
import me.luligabi.amimi.common.viewer.MultiblockPreviewWidget;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;

import java.util.*;

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
public class MultiblockRenderer extends SchemaRenderer {


    private final Map<BlockPos, BlockInfo> structureBlocks;

    private final Map<BlockPos, MultiblockSchemaInfo.HatchData> hatchData;

    private final BlockPos focus;

    public MultiblockRenderer(MultiblockSchemaInfo.SchemaData schemaData) {
        super(schemaData.mapSchema());
        this.structureBlocks = schemaData.structureBlocks();
        this.hatchData = schemaData.hatchData();
        this.addHighlightCache(schemaData.hatchData());
        this.highlightRenderer(new BlockHighlight(Color.withAlpha(Color.WHITE.main, 0.25f), 1));
//        this.cameraFunc((c, s) -> {
//            final Vector3fc center = s.getFocus();
//            c.setLookAtAndAngle(center.x(), center.y(), center.z(), c.dist(), c.yaw(), c.pitch());
//        });
        //this.captureDebugInfo(true);

        this.focus = new BlockPos((int) schemaData.mapSchema().getFocus().x(), (int) schemaData.mapSchema().getFocus().y(), (int) schemaData.mapSchema().getFocus().z());
    }

    private void drawTooltip(final GuiContext context,
                             final PoseStack poseStack,
                             final @NotNull BlockHitResult result) {
        final BlockInfo blockInfo = structureBlocks.getOrDefault(result.getBlockPos(), null);
        if (blockInfo == null) return;

        final List<Component> tooltip = Lists.newArrayList(blockInfo.getBlockState().getBlock().getName());

        final MultiblockSchemaInfo.HatchData data = hatchData.getOrDefault(result.getBlockPos(), null);
        if (data != null) {
            tooltip.addAll(data.tooltip());
        }

        context.updateZ(context.getCurrentDrawingZ() + 4000);
        context.getGraphics().renderTooltip(context.getFont(), tooltip, Optional.empty(), context.getMouseX(), context.getMouseY());
    }

    @Override
    public void draw(final GuiContext context,
                     final int x,
                     final int y,
                     final int width,
                     final int height,
                     final WidgetTheme widgetTheme) {
        int mouseX = context.getMouseX();
        int mouseY = context.getMouseY();

        context.getGraphics().flush();
        context.graphicsPose().pushPose();

        Area area = context.getScreenArea();
        int transformX = context.transformX(x, y) + area.x();
        int transformY = context.transformY(x, y) + area.y();
        ((BaseSchemaRendererAccessor) this).getViewport().calculateOpenGLViewportFromRectangle(transformX, transformY, width, height);
        ((BaseSchemaRendererAccessor) this).getViewport().applyViewport();

        onSetupCamera();
        setupCamera(width, height);
        renderWorld(context.getGraphics().bufferSource(), context.getRenderPartialTicks());

        PoseStack cameraPs = this.createWorldRenderPose();
        BlockHitResult result = null;
        if (doRayTrace() || captureDebugInfo()) {
            if (Area.isInside(x, y, width, height, mouseX, mouseY)) {
                result = rayTrace(mouseX, mouseY, width, height);
            }
            if (result == null || result.getType() != HitResult.Type.BLOCK) {
                if (this.lastRayTrace() != null) {
                    onRayTraceFailed();
                }
            } else {
                onSuccessfulRayTrace(cameraPs, result);
            }
            ((BaseSchemaRendererAccessor) this).setLastRayTrace(result);
        }

        if (MultiblockPreviewWidget.isRenderHatches()) {
//            highlightCache.entrySet().stream().findFirst().get().getValue()
//                    .renderHighlight(cameraPs, focus, null, camera().pos());

            for (Map.Entry<BlockPos, MultiblockSchemaInfo.HatchData> data : hatchData.entrySet()) {
                if (!this.renderFilter().shouldRender(data.getKey(), null)) continue;

                final CulledBlockHighlight highlight;
                if (ClientConfig.INSTANCE.uniqueHatchColors.getAsBoolean()) {
                    highlight = highlightCache.getOrDefault(data.getValue().color(), defaultHighlight);
                } else {
                    highlight = defaultHighlight;
                }

                highlight.renderHighlight(cameraPs, data.getKey(), null, camera().pos());
            }
        }

        resetCamera();
        if (result != null && result.getType() == HitResult.Type.BLOCK) {
            this.drawTooltip(context, context.graphicsPose(), result);
        }

        context.graphicsPose().popPose();

        if (this.captureDebugInfo()) {
            drawProjectedBlockPos(context.getGraphics(), width, height);
        }
    }

    private void addHighlightCache(final Map<BlockPos, MultiblockSchemaInfo.HatchData> hatchData) {
        if (hatchData == null) return;
        for (final MultiblockSchemaInfo.HatchData data : hatchData.values()) {
            highlightCache.computeIfAbsent(data.color(), hc -> {
                return new CulledBlockHighlight(Color.withAlpha(data.color(), 0.9f), true, 1 / 16f);
            });
        }
    }

    private static final CulledBlockHighlight defaultHighlight = new CulledBlockHighlight(Color.withAlpha(HatchUtils.DEFAULT_COLOR, 0.9f), true, 1 / 16f);;
    private static final Map<Integer, CulledBlockHighlight> highlightCache = new HashMap<>();

}
