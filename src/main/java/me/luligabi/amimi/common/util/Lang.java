package me.luligabi.amimi.common.util;

import aztech.modern_industrialization.MI;
import lombok.Getter;
import me.luligabi.amimi.common.AMIMI;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.List;

public enum Lang {
    CATEGORY_NAME("Multiblock Information"),
    SHOW_NEXT_TIER("Show next tier"),
    SHOW_PREVIOUS_TIER("Show previous tier"),
    HIDE_HATCH_PLACEMENTS("Hide Hatch Placements"),
    SHOW_HATCH_PLACEMENTS("Show Hatch Placements"),
    OPEN_GUIDE("Open guide page");

    private final String root;
    @Getter
    private final String englishText;
    @Getter
    private final List<String> additionalTranslationsKey;

    Lang(String englishText, String... additionalTranslationKey) {
        this.root = "text." + AMIMI.ID;
        this.englishText = englishText;
        this.additionalTranslationsKey = List.of(additionalTranslationKey);
    }

    public String getTranslationKey() {
        return this.root + '.' + name();
    }

    public MutableComponent text() {
        return Component.translatable(getTranslationKey());
    }

    public MutableComponent text(Object... args) {
        return Component.translatable(getTranslationKey(), args);
    }
}
