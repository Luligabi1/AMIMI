package me.luligabi.amimi.common.viewer.emi;

import aztech.modern_industrialization.MIItem;
import aztech.modern_industrialization.compat.rei.machines.ReiMachineRecipes;
import brachy.modularui.integration.emi.recipe.ModularUIEmiRecipe;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import me.luligabi.amimi.common.AMIMI;
import me.luligabi.amimi.common.util.Lang;
import me.luligabi.amimi.common.util.MultiblockSet;
import me.luligabi.amimi.common.viewer.MultiblockPreviewWidget;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class MultiblockInfoEmiCategory extends EmiRecipeCategory {

    public static final MultiblockInfoEmiCategory INSTANCE = new MultiblockInfoEmiCategory();

    private MultiblockInfoEmiCategory() {
        super(AMIMI.id("multiblock_info"), EmiStack.of(MIItem.WRENCH.asItem()));
    }

    public static void registerDisplays(EmiRegistry registry) {
        MultiblockSet.fromShapes(ReiMachineRecipes.multiblockShapes).stream()
                .filter(ms -> !registry.isStackDisabled(EmiStack.of(BuiltInRegistries.ITEM.get(ms.machine()))))
                .map(MultiblockInfoEmiWrapper::new)
                .forEach(registry::addRecipe);
    }

    @Override
    public Component getName() {
        return Component.translatable(Lang.CATEGORY_NAME);
    }

    public static class MultiblockInfoEmiWrapper extends ModularUIEmiRecipe {

        private final MultiblockSet multiblockSet;
        private final EmiStack controllerStack;
        private final List<EmiIngredient> containedBlocks = new ArrayList<>();

        public MultiblockInfoEmiWrapper(MultiblockSet multiblockSet) {
            super(multiblockSet.machine(), () -> new MultiblockPreviewWidget(multiblockSet, null, 150, 135));
            this.multiblockSet = multiblockSet;
            controllerStack = EmiStack.of(BuiltInRegistries.ITEM.get(multiblockSet.machine()).getDefaultInstance());
            initializeContainedBlocks();
        }

        private void initializeContainedBlocks() {
//            Map<BlockPos, BlockInfo> resultStructure = new HashMap<>();
//            IBlockPattern pattern = multiblockSet.getStructurePatterns().get(DEFAULT_STRUCTURE).get();
//            AbstractStructureHelper structureHelper = null;
//            if (pattern instanceof BlockPattern blockPattern) {
//                var sliceRepeats = new Int2IntArrayMap();
//                for (int i = 0; i < blockPattern.getSlices().length; i++) {
//                    sliceRepeats.put(i, blockPattern.getSlices()[i].getMinRepeats());
//                }
//                structureHelper = AbstractStructureHelper.blockPattern(sliceRepeats);
//            } else if (pattern instanceof ExpandablePattern expandablePattern) {
//                var userDimensions = new IntArrayList();
//                expandablePattern.getBoundsConstraints().apply().stream()
//                        .mapToInt(Pair::left)
//                        .forEach(userDimensions::add);
//                structureHelper = AbstractStructureHelper.expandable(userDimensions);
//            }
//            if (structureHelper != null) {
//                structureHelper.populate(resultStructure, pattern, null,
//                        Direction.NORTH, Direction.UP, false);
//
//                Object2IntMap<Block> blockCount = new Object2IntOpenHashMap<>();
//                resultStructure.forEach(
//                        (pos, state) -> blockCount.mergeInt(state.getBlockState().getBlock(), 1, Integer::sum));
//                blockCount.forEach((block, count) -> containedBlocks.add(EmiStack.of(block.asItem(), count)));
//            }
//
//
//            for(Map.Entry<BlockPos, SimpleMember> entry : multiblockSet.shapeTemplate().simpleMembers.entrySet()) {
//                BlockState state = ((SimpleMember)entry.getValue()).getPreviewState();
//                Item item = state.getBlock().asItem();
//                if (item != Items.AIR) {
//                    materials.put(item, 1 + materials.getOrDefault(item, 0));
//                }
//            }
//
//            for(Map.Entry<Item, Integer> entry : materials.entrySet()) {
//                this.materials.add(new ItemStack(entry.getKey(), entry.getValue()));
//            }
        }

        @Override
        public EmiRecipeCategory getCategory() {
            return INSTANCE;
        }

        @Override
        public @Nullable ResourceLocation getId() {
            return this.multiblockSet.getCategoryId();
        }

        @Override
        public List<EmiIngredient> getInputs() {
            return List.of(controllerStack); // FIXME
        }

        @Override
        public List<EmiStack> getOutputs() {
            return List.of(controllerStack);
        }
    }
}