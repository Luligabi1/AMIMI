package me.luligabi.amimi.datagen.client;

import me.luligabi.amimi.datagen.client.provider.LangProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;

public class ClientDatagen {

    public static void onGatherData(final GatherDataEvent event) {
        event.getGenerator().addProvider(event.includeClient(), new LangProvider(event));
    }

}
