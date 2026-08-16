package me.luligabi.amimi.common.util.render.widget;

import brachy.modularui.api.drawable.Text;
import brachy.modularui.drawable.SchemaRenderer;
import brachy.modularui.widgets.ButtonWidget;
import com.mojang.datafixers.util.Pair;
import me.luligabi.amimi.common.util.gregtech.MultiblockSchemaInfo;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;

import java.util.function.Supplier;

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
