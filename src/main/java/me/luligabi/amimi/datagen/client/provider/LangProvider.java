package me.luligabi.amimi.datagen.client.provider;

import me.luligabi.amimi.common.AMIMI;
import me.luligabi.amimi.common.util.Lang;
import net.neoforged.neoforge.common.data.LanguageProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;

public class LangProvider extends LanguageProvider {

    public LangProvider(final GatherDataEvent event) {
        super(event.getGenerator().getPackOutput(), AMIMI.ID, "en_us");
    }

    @Override
    protected void addTranslations() {
        Lang.TRANSLATIONS.forEach(this::add);
    }

}
