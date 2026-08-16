package me.luligabi.amimi.common.mixin;

import aztech.modern_industrialization.client.compat.viewer.abstraction.ViewerCategory;
import aztech.modern_industrialization.client.compat.viewer.usage.MultiblockCategory;
import aztech.modern_industrialization.client.compat.viewer.usage.ViewerSetup;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin(ViewerSetup.class)
public class ViewerSetupMixin {

    @Redirect(
        method = "setup",
        at = @At(
            value = "INVOKE",
            target = "Ljava/util/List;add(Ljava/lang/Object;)Z"
        )
    )
    private static boolean onAdd(final List<ViewerCategory<?>> registry,
                                 final Object category) {
        if (category instanceof MultiblockCategory) return false;
        return registry.add((ViewerCategory<?>) category);
    }
}