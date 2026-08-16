package me.luligabi.amimi.common.util;


import aztech.modern_industrialization.compat.rei.machines.ReiMachineRecipes;
import aztech.modern_industrialization.machines.multiblocks.ShapeTemplate;
import net.minecraft.resources.ResourceLocation;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

public record MultiblockSet(ResourceLocation machine, List<ShapeTemplate> shapeTemplates) {

    public ResourceLocation getCategoryId() {
        final ResourceLocation id = this.machine();
        return ResourceLocation.fromNamespaceAndPath(id.getNamespace(), "/multi_info/" + id.getPath());
    }


    public static List<MultiblockSet> fromShapes(List<ReiMachineRecipes.MultiblockShape> shapes) {
        return shapes.stream()
                .sorted(Comparator.comparing(
                        ReiMachineRecipes.MultiblockShape::alternative,
                        Comparator.nullsFirst(String::compareTo)
                ))
                .collect(Collectors.groupingBy(
                        ReiMachineRecipes.MultiblockShape::machine,
                        LinkedHashMap::new,
                        Collectors.mapping(ReiMachineRecipes.MultiblockShape::shapeTemplate, Collectors.toList())
                ))
                .entrySet().stream()
                .map(entry -> new MultiblockSet(entry.getKey(), entry.getValue()))
                .toList();
    }
}
