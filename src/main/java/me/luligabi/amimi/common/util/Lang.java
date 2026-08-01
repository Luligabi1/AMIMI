package me.luligabi.amimi.common.util;

import me.luligabi.amimi.common.AMIMI;

import java.util.HashMap;
import java.util.Map;

public class Lang {

    public static final String CATEGORY_NAME = "category.%s.multiblock_info".formatted(AMIMI.ID);

    public static final String SHOW_NEXT_TIER = "text.%s.show_next_tier".formatted(AMIMI.ID);
    public static final String SHOW_PREVIOUS_TIER = "text.%s.show_previous_tier".formatted(AMIMI.ID);

    public static final String HIDE_HATCH_PLACEMENTS = "text.%s.hide_hatches".formatted(AMIMI.ID);
    public static final String SHOW_HATCH_PLACEMENTS = "text.%s.show_hatches".formatted(AMIMI.ID);

    public static final String OPEN_GUIDE = "text.%s.open_guide".formatted(AMIMI.ID);


    public static final HashMap<String, String> TRANSLATIONS = new HashMap<>(Map.ofEntries(
            Map.entry(CATEGORY_NAME, "Multiblock Information"),

            Map.entry(SHOW_NEXT_TIER, "Show next tier"),
            Map.entry(SHOW_PREVIOUS_TIER, "Show previous tier"),

            Map.entry(HIDE_HATCH_PLACEMENTS, "Hide Hatch Placements"),
            Map.entry(SHOW_HATCH_PLACEMENTS, "Show Hatch Placements"),

            Map.entry(OPEN_GUIDE, "Open guide page")
    ));

}
