package me.luligabi.amimi.common.viewer.emi;

import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;

@EmiEntrypoint
public class EMIInit implements EmiPlugin {

    @Override
    public void register(EmiRegistry registry) {
        registry.addCategory(MultiblockInfoEmiCategory.CATEGORY);
        MultiblockInfoEmiCategory.registerDisplays(registry);
    }
}
