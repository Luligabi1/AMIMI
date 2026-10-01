package me.luligabi.amimi.common.mixin;

import brachy.modularui.drawable.schema.DummyChunkSource;
import brachy.modularui.drawable.schema.SchemaLevel;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMaps;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//fixes switching between tiers leading to a crash because of thread safety issues of this class
@Mixin(DummyChunkSource.class)
public class DummyChunkSourceMixin {
    @Mutable
    @Final
    @Shadow
    private Long2ObjectMap chunks;

    @Inject(method = "<init>", at =  @At("TAIL"))
    private void amimi$synchronizeChunks(SchemaLevel level, CallbackInfo ci) {
        this.chunks = Long2ObjectMaps.synchronize(this.chunks);
    }
}
