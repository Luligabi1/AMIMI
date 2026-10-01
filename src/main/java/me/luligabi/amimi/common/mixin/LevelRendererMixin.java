package me.luligabi.amimi.common.mixin;

import brachy.modularui.drawable.schema.RenderLevel;
import brachy.modularui.drawable.schema.SchemaLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin {
    @Unique
    private static boolean amimi$isSchemaLevel(BlockAndTintGetter level) {
        return level instanceof SchemaLevel || level instanceof RenderLevel;
    }

    @Inject(
            method = "getLightColor(Lnet/minecraft/world/level/BlockAndTintGetter;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;)I",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void amimi$fullBrightState(
            BlockAndTintGetter level,
            BlockState state,
            BlockPos pos,
            CallbackInfoReturnable<Integer> cir) {
        if (amimi$isSchemaLevel(level)) cir.setReturnValue(LightTexture.FULL_BRIGHT);
    }

    @Inject(
            method = "getLightColor(Lnet/minecraft/world/level/BlockAndTintGetter;Lnet/minecraft/core/BlockPos;)I",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void amimi$fullBrightPos(
            BlockAndTintGetter level,
            BlockPos pos,
            CallbackInfoReturnable<Integer> cir) {
        if (amimi$isSchemaLevel(level)) cir.setReturnValue(LightTexture.FULL_BRIGHT);
    }
}
