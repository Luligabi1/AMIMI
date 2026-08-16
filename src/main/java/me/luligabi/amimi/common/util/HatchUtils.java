package me.luligabi.amimi.common.util;

import aztech.modern_industrialization.MIText;
import aztech.modern_industrialization.api.energy.CableTier;
import aztech.modern_industrialization.machines.multiblocks.HatchFlags;
import aztech.modern_industrialization.machines.multiblocks.HatchType;
import aztech.modern_industrialization.machines.multiblocks.HatchTypes;
import brachy.modularui.utils.Color;
import me.luligabi.amimi.common.util.gregtech.MultiblockSchemaInfo;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class HatchUtils {

    public static MultiblockSchemaInfo.HatchData parseHatchFlags(final HatchFlags flags) {
        return new MultiblockSchemaInfo.HatchData(getTooltip(flags), getColor(flags));
    }

    private static List<Component> getTooltip(final HatchFlags flags) {
        final List<Component> tooltip = new ArrayList<>();
        tooltip.add(MIText.AcceptsHatches.text());
        for (var type : flags.values()) {
            tooltip.add(Component.literal("- ").append(type.description()));
        }
        return tooltip;
    }

    private static Integer getColor(final HatchFlags flags) {
        final Set<Integer> foundColors = flags.values().stream()
                .map(HatchUtils::getHatchTypeColor)
                .collect(Collectors.toSet());
        if (foundColors.size() > 1) return DEFAULT_COLOR;
        return foundColors.stream().findFirst().get();
    }

    private static int getHatchTypeColor(final HatchType type) {
        return switch (type.id().getPath()) {
            case "item_input",
                 "item_output" -> ITEM_COLOR;
            case "fluid_input",
                 "fluid_output",
                 "large_tank" -> FLUID_COLOR;
            case "energy_input",
                 "energy_output" -> ENERGY_COLOR;
            case "nuclear_item",
                 "nuclear_fluid" -> NUCLEAR_COLOR;
            default -> guessHatchTypeColor(type.id().getPath());
        };
    }

    private static int guessHatchTypeColor(final String id) {
        return switch (id) {
            case String s when s.contains("nuclear") -> NUCLEAR_COLOR;
            case String s when s.contains("item") -> ITEM_COLOR;
            case String s when s.contains("fluid") -> FLUID_COLOR;
            case String s when s.contains("energy") ||
                               s.contains("storage_unit") || // YAI! compat
                               CableTier.allTiers().stream().anyMatch(ct -> s.contains(ct.name)) -> ENERGY_COLOR;
            default -> UNKNOWN_COLOR;
        };
    }

    private static final int ITEM_COLOR = Color.rgb(255, 128, 64);
    private static final int FLUID_COLOR = Color.rgb(57, 70, 219);
    private static final int ENERGY_COLOR = Color.rgb(235, 224, 20);
    private static final int NUCLEAR_COLOR = Color.rgb(35, 158, 33);

    private static final int DEFAULT_COLOR = Color.rgb(111, 225, 111);
    private static final int UNKNOWN_COLOR = Color.rgb(188, 50, 205);
}
