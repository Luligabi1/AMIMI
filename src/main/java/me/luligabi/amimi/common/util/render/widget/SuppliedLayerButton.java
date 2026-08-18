package me.luligabi.amimi.common.util.render.widget;

import brachy.modularui.api.drawable.Text;
import brachy.modularui.drawable.SchemaRenderer;
import brachy.modularui.widgets.ButtonWidget;
import com.mojang.datafixers.util.Pair;
import me.luligabi.amimi.common.util.gregtech.MultiblockSchemaInfo;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;

import java.util.function.Supplier;

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
public class SuppliedLayerButton extends ButtonWidget<SuppliedLayerButton> {

    private final int minLayer;
    private final int maxLayer;
    private int currentLayer = Integer.MIN_VALUE;

    public SuppliedLayerButton(Supplier<MultiblockSchemaInfo> info, Supplier<Integer> activeIndex) {
        final MultiblockSchemaInfo.SchemaData data = info.get().getSchema(activeIndex.get());
        final SchemaRenderer renderer = info.get().getRenderer();

        final Pair<BlockPos, BlockPos> bounds = data.mapSchema().getBounds();
        this.minLayer = bounds.getFirst().getY();
        this.maxLayer = Math.max(bounds.getSecond().getY() - 1, minLayer);
        this.overlay(Text.dynamic(() -> currentLayer > Integer.MIN_VALUE ?
                Component.literal(Integer.toString(currentLayer)) : Component.literal("∀")));

        this.onMousePressed((context, button) -> {
            if (button == 0 || button == 1) {
                if (button == 0) {
                    if (currentLayer == Integer.MIN_VALUE) {
                        currentLayer = minLayer;
                    } else {
                        currentLayer++;
                    }
                } else {
                    if (currentLayer == Integer.MIN_VALUE) {
                        currentLayer = maxLayer;
                    } else {
                        currentLayer--;
                    }
                }
                if (currentLayer > maxLayer || currentLayer < minLayer) {
                    currentLayer = Integer.MIN_VALUE;
                }
                renderer.notifyRecompile();
                return true;
            }
            return false;
        });
        renderer.updateRenderFilter((blockPos, blockInfo) -> {
            return currentLayer == Integer.MIN_VALUE || currentLayer >= blockPos.getY();
        });
    }

}
