package me.luligabi.amimi.datagen;

import me.luligabi.amimi.datagen.client.ClientDatagen;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.data.event.GatherDataEvent;

public class AMIMIDatagen {

    @SubscribeEvent
    public static void onGatherData(final GatherDataEvent event) {
        ClientDatagen.onGatherData(event);
    }

}
