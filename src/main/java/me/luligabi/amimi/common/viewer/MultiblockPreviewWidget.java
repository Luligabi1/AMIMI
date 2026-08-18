package me.luligabi.amimi.common.viewer;

import brachy.modularui.api.drawable.IDrawable;
import brachy.modularui.api.widget.IGuiAction;
import brachy.modularui.drawable.GuiTextures;
import brachy.modularui.integration.recipeviewer.RecipeSlotRole;
import brachy.modularui.integration.recipeviewer.RecipeViewerSlotWidget;
import brachy.modularui.utils.Alignment;
import brachy.modularui.value.sync.DynamicSyncHandler;
import brachy.modularui.widget.ParentWidget;
import brachy.modularui.widget.scroll.HorizontalScrollData;
import brachy.modularui.widgets.*;
import brachy.modularui.widgets.dynamic.DynamicHandler;
import brachy.modularui.widgets.dynamic.DynamicWidget;
import brachy.modularui.widgets.layout.Flow;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.datafixers.util.Pair;
import guideme.Guide;
import guideme.PageAnchor;
import guideme.internal.GuideMEClient;
import it.unimi.dsi.fastutil.objects.Reference2IntMap;
import it.unimi.dsi.fastutil.objects.Reference2IntOpenHashMap;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import me.luligabi.amimi.client.ClientConfig;
import me.luligabi.amimi.common.util.Lang;
import me.luligabi.amimi.common.util.render.MultiblockRenderer;
import me.luligabi.amimi.common.util.MultiblockSet;
import me.luligabi.amimi.common.util.render.widget.SuppliedLayerButton;
import me.luligabi.amimi.common.util.gregtech.BlockInfo;
import me.luligabi.amimi.common.util.gregtech.MultiblockSchemaInfo;
import me.luligabi.amimi.common.util.render.widget.SuppliedUITexture;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

/*
 * This file is adapted code originally part of GregTech:CEu, hosted at https://github.com/GregTechCEu/GregTech-Modern
 *
 * This file is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This file is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program. If not, see
 * <https://www.gnu.org/licenses/lgpl-3.0.html>.
 */
@Accessors(chain = true)
public class MultiblockPreviewWidget extends ParentWidget<MultiblockPreviewWidget> {

    private final MultiblockSet multiblockDefinition;
    private final int width;
    private final int height;

    private final DynamicHandler schemaHandler = new DynamicHandler();
    private final DynamicHandler partsHandler = new DynamicHandler();
    private final DynamicHandler selectedBlockHandler = new DynamicHandler();

    private final Reference2IntMap<Block> blockCounts = new Reference2IntOpenHashMap<>();

    @Getter
    @Setter
    private MultiblockSchemaInfo multiblockSchemaInfo;

    private SelectionInfo selectionInfo = SelectionInfo.empty();

    @Getter
    private static boolean renderHatches = true;

    @Setter
    private @Nullable Runnable onSchemaRefresh;

    private int activeIndex = -1;

    private final IGuiAction.MouseReleased setBlockOnClick = (ctx, m) -> {
        if (m == InputConstants.MOUSE_BUTTON_LEFT) {
            BlockHitResult rayTrace = this.multiblockSchemaInfo.getRenderer().lastRayTrace();
            if (rayTrace != null && rayTrace.getType() == HitResult.Type.BLOCK) {
                final BlockState state = this.multiblockSchemaInfo.getSchema(this.activeIndex).mapSchema().getLevel()
                        .getBlockState(rayTrace.getBlockPos());
                this.selectionInfo = SelectionInfo.of(rayTrace, state);
                this.selectedBlockHandler.notifyUpdate();
                return true;
            }
        }
        return false;
    };

    public MultiblockPreviewWidget(final MultiblockSet definition,
                                   final MultiblockSchemaInfo schemaInfo,
                                   final int width,
                                   final int height) {
        this.multiblockDefinition = definition;
        this.width = width;
        this.height = height;
        // FIXME if (!GTCEu.isClientThread()) return;

        this.multiblockSchemaInfo = schemaInfo == null ? new MultiblockSchemaInfo() : schemaInfo;
        this.initSchemas();
        this.changeViewedShape(true, false);

        this.schemaHandler.widgetProvider(() -> this.multiblockSchemaInfo.getMultiSchema());
        this.partsHandler.widgetProvider(() -> new ListWidget<>()
                .width(this.width)
                .height(20)
                .marginBottom(7)
                .scrollDirection(new HorizontalScrollData())
                .children(
                        this.multiblockSchemaInfo.getSchema(this.activeIndex).blockCounts(),
                        e -> {
                            ItemStack stack = new ItemStack(e.getFirst(), e.getSecond());

                            return Flow.row()
                                    .coverChildrenWidth()
                                    .height(20)
                                    .crossAxisAlignment(Alignment.CrossAxis.CENTER)
                                    .child(
                                            RecipeViewerSlotWidget.create()
                                                    .recipeSlotRole(RecipeSlotRole.OUTPUT)
                                                    .value(stack)
                                                    .background(IDrawable.EMPTY)
                                                    .size(16)
                                                    .tooltip(r -> r.addFromItem(stack))
                                    );
                })
        );

        this.coverChildren()
                .padding(4)
                .child(
                        Flow.row()
                                .coverChildren()
                                .crossAxisAlignment(Alignment.CrossAxis.START)
                                .child(
                                        Flow.col()
                                                .name("main")
                                                .coverChildren()
                                                .child(new DynamicWidget<>()
                                                        .name("schema_view")
                                                        .coverChildrenWidth()
                                                        .coverChildrenHeight()
                                                        .clientOnlyHandler(this.schemaHandler))
                                                .child(new DynamicWidget<>()
                                                        .name("parts_view")
                                                        .coverChildrenHeight()
                                                        .leftRel(0)
                                                        .clientOnlyHandler(this.partsHandler))
                                )
                                .child(
                                        Flow.col()
                                                .name("buttons")
                                                .coverChildren()
                                                .decoration()
                                                .top(0)
                                                .right(0)
                                                .childIf(
                                                    ClientConfig.INSTANCE.enableOpenGuidePageButton.getAsBoolean() && this.multiblockSchemaInfo.getGuidePage() != null,
                                                    () -> new ButtonWidget<>()
                                                            .onMousePressed((c, b) -> {
                                                                final Pair<Guide, PageAnchor> guidePage = this.multiblockSchemaInfo.getGuidePage();
                                                                GuideMEClient.openGuideAtAnchor(guidePage.getFirst(), guidePage.getSecond());
                                                                return true;
                                                            })
                                                            .overlay(GuiTextures.HELP)
                                                            .tooltip(r -> r.addLine(Lang.OPEN_GUIDE.text()))
                                                )
                                                .child(
                                                    new ButtonWidget<>()
                                                        .onMousePressed((c, b) -> {
                                                            renderHatches = !renderHatches;
                                                            return true;
                                                        })
                                                        .overlay(new SuppliedUITexture(() -> renderHatches ? GuiTextures.VISIBLE : GuiTextures.INVISIBLE))
                                                        .tooltip(r -> r.addLine(Lang.HIDE_HATCH_PLACEMENTS.text()))
                                                )
                                                .childIf(
                                                    this.multiblockSchemaInfo.schemaSize() > 1,
                                                    () -> new ButtonWidget<>()
                                                        .onMousePressed((c, b) -> {
                                                            this.changeViewedShape(false, !Screen.hasShiftDown());
                                                            return true;
                                                        })
                                                        .overlay(new SuppliedUITexture(() -> Screen.hasShiftDown() ? GuiTextures.LEFTLOAD : GuiTextures.RIGHTLOAD))
                                                        .tooltipDynamic(r -> {
                                                            r.addLine((Screen.hasShiftDown() ? Lang.SHOW_PREVIOUS_TIER : Lang.SHOW_NEXT_TIER).text());
                                                            r.markDirty();
                                                        })
                                                )
                                                .child(new SuppliedLayerButton(
                                                        () -> this.multiblockSchemaInfo,
                                                        () -> this.activeIndex
                                                ))

                                )
                );
    }

    @ApiStatus.Internal
    private void initSchemas() {
        this.multiblockSchemaInfo.initSchemas(multiblockDefinition, onSchemaRefresh);
    }

    private void changeViewedShape(final boolean init,
                                   final boolean forward) {
        final int schemaSize = this.multiblockSchemaInfo.schemaSize();
        if (!init && schemaSize == 1) return;

        final int previousIndex = this.activeIndex;
        if (init) {
            this.activeIndex = 0;
        } else {
            if (forward) {
                this.activeIndex = (this.activeIndex + 1) % schemaSize;
            } else {
                this.activeIndex = (this.activeIndex - 1 + schemaSize) % schemaSize;
            }
        }
        final MultiblockSchemaInfo.SchemaData data = this.multiblockSchemaInfo.getSchema(this.activeIndex);

        final float yaw;
        final float pitch;
        float scale = data.scale();
        if (this.multiblockSchemaInfo.getMultiSchema() != null) {
            yaw = this.multiblockSchemaInfo.getMultiSchema().getYaw();
            pitch = this.multiblockSchemaInfo.getMultiSchema().getPitch();

            // keep scale if tiers are the same size (i.e. EBF),
            // restore schema's scale if not (i.e. distillation tower)
            if (previousIndex != -1) {
                final MultiblockSchemaInfo.SchemaData previousData = this.multiblockSchemaInfo.getSchema(previousIndex);

                if (previousData.structureBlocks().size() == data.structureBlocks().size()) {
                    scale = previousData.scale();
                }
            }
        } else {
            yaw = MultiblockSchemaInfo.PREVIEW_YAW;
            pitch = MultiblockSchemaInfo.PREVIEW_PITCH;
            scale = data.scale();
        }

        this.multiblockSchemaInfo.setRenderer(new MultiblockRenderer(data));
        SchemaWidget schema = this.multiblockSchemaInfo.getRenderer().asWidget()
                .listenGuiAction(this.setBlockOnClick)
                .tooltipDynamic(text -> {
                    BlockHitResult hit = this.multiblockSchemaInfo.getRenderer().lastRayTrace();
                    if (hit != null && hit.getType() == HitResult.Type.BLOCK) {
                        BlockState state = data.mapSchema().getLevel()
                                .getBlockState(hit.getBlockPos());
                        ItemStack pickedItem = state.getCloneItemStack(hit,
                                data.mapSchema().getLevel(), hit.getBlockPos(),
                                this.getContext().getMC().player);
                        text.addFromItem(pickedItem);
                    }
                }).tooltipAutoUpdate(true)
                .size(this.width, this.height)
                .yaw(yaw)
                .pitch(pitch)
                .scale(scale);

        this.multiblockSchemaInfo.setMultiSchema(schema);
//        this.multiblockSchemaInfo.getMultiSchema().getSchemaRenderer().updateRenderFilter((pos, state) -> {
//            return pos.getY() >= yLevel;
//        });
        this.refreshViewWidget();
    }

    private void refreshViewWidget() {
        if (partsHandler != null) {
            this.partsHandler.notifyUpdate();
        }
        if (this.multiblockSchemaInfo.getRenderer() != null) {
            this.multiblockSchemaInfo.getRenderer().notifyRecompile();
            this.schemaHandler.notifyUpdate();
        }
    }



    /// ==== User Preference UI ======
//    private void setPredicateDefaultBlock(PatternPredicate predicate, BasePredicate basePredicate,
//                                          BlockInfo blockInfo) {
//        this.multiblockSchemaInfo.putPredicatePreference(predicate, basePredicate, blockInfo);
//        refreshSchema();
//        refreshViewWidget();
//    }
//
//    private void setUserDefinedBlockInfo(BlockPos pos, BlockInfo blockInfo) {
//        // todo validation testing?
//        this.multiblockSchemaInfo.getUserGlobalBlockPreferences().put(pos.asLong(), blockInfo);
//        refreshSchema();
//        refreshViewWidget();
//    }
//
//    private void createConstraintSliders(Flow parent, ExpandablePattern pattern) {
//        if (pattern.getBoundsConstraints() == null) {
//            return;
//        }
//        List<IntIntPair> constraints = pattern.getBoundsConstraints().apply();
//        for (int i = 0; i < constraints.size(); i++) {
//            IntIntPair value = constraints.get(i);
//            if (value.leftInt() != value.rightInt()) {
//                final int index = i;
//                parent.child(new SliderWidget()
//                        .background(GTGuiTextures.FLUID_SLOT)
//                        .bounds(value.leftInt(), value.rightInt())
//                        .height(16)
//                        .width(value.rightInt() * 12)
//                        .stopper(1.0f)
//                        .value(new IntValue.Dynamic(
//                                () -> this.getMultiblockSchemaInfo().getUserDimensions().getInt(index), v -> {
//                                    int oldValue = this.getMultiblockSchemaInfo().getUserDimensions().getInt(index);
//                                    if (oldValue == v) return;
//                                    this.getMultiblockSchemaInfo().getUserDimensions().set(index, v);
//                                    refreshSchema();
//                                    refreshViewWidget();
//                                })));
//            }
//        }
//    }
//
//    private void createSliceSliders(Flow col, BlockPattern blockPattern) {
//        int repeatSliceIndex = 0;
//        for (var patternSlice : blockPattern.getSlices()) {
//            if (patternSlice.getMinRepeats() == 1 && patternSlice.getMaxRepeats() == 1) {
//                repeatSliceIndex++;
//                continue;
//            }
//            if (!this.multiblockSchemaInfo.getUserSliceRepeats().containsKey(repeatSliceIndex)) {
//                this.multiblockSchemaInfo.getUserSliceRepeats().put(repeatSliceIndex, patternSlice.getMinRepeats());
//            }
//            if (patternSlice.getMinRepeats() != patternSlice.getMaxRepeats()) {
//                final int index = repeatSliceIndex;
//                col.child(new SliderWidget()
//                        .background(GTGuiTextures.FLUID_SLOT)
//                        .height(16)
//                        .width(patternSlice.getMaxRepeats() * 12)
//                        .stopper(1.0f)
//                        .bounds(patternSlice.getMinRepeats(), patternSlice.getMaxRepeats())
//                        .value(new IntValue.Dynamic(() -> {
//                            if (!this.multiblockSchemaInfo.getUserSliceRepeats().containsKey(index)) return 0;
//                            return this.multiblockSchemaInfo.getUserSliceRepeats().get(index);
//                        }, v -> {
//                            int oldValue = this.multiblockSchemaInfo.getUserSliceRepeats().getOrDefault(index, 0);
//                            if (oldValue == v) return;
//                            this.multiblockSchemaInfo.getUserSliceRepeats().put(index, v);
//                            refreshSchema();
//                            refreshViewWidget();
//                        })));
//            }
//            repeatSliceIndex++;
//        }
//    }

    protected record SelectionInfo(BlockPos pos, BlockInfo info) {

        protected static SelectionInfo empty() {
            return new SelectionInfo(BlockPos.ZERO, BlockInfo.EMPTY);
        }

        protected static SelectionInfo of(BlockHitResult result, BlockState state) {
            return new SelectionInfo(result.getBlockPos(), BlockInfo.fromBlockState(state));
        }

        public Block block() {
            return state().getBlock();
        }

        public BlockState state() {
            return info().getBlockState();
        }

        public ItemStack stack() {
            return info().getItemStackForm();
        }
    }
}