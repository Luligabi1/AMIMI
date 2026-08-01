package me.luligabi.amimi.common;

import me.luligabi.amimi.datagen.AMIMIDatagen;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

@Mod(AMIMI.ID)
public class AMIMI {

    public AMIMI(final IEventBus modEventBus,
                 final ModContainer modContainer) {
        modEventBus.addListener(AMIMIDatagen::onGatherData);
    }

    public static ResourceLocation id(final String path) {
        return ResourceLocation.fromNamespaceAndPath(ID, path);
    }

    public static final String ID = "amimi";
}
