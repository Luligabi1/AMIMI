package me.luligabi.amimi.common.mixin;

import brachy.modularui.drawable.schema.BaseSchemaRenderer;
import brachy.modularui.drawable.schema.Viewport;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BaseSchemaRenderer.class)
public interface BaseSchemaRendererAccessor {

    @Accessor
    Viewport getViewport();

    @Accessor
    void setLastRayTrace(BlockHitResult lastRayTrace);
}
