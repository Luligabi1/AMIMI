package me.luligabi.amimi.common.util.gregtech;

import aztech.modern_industrialization.machines.multiblocks.HatchFlags;
import aztech.modern_industrialization.machines.multiblocks.ShapeTemplate;
import com.mojang.datafixers.util.Pair;
import guideme.Guide;
import guideme.PageAnchor;
import guideme.indices.ItemIndex;
import guideme.internal.GuideRegistry;
import it.unimi.dsi.fastutil.longs.Long2ReferenceMap;
import it.unimi.dsi.fastutil.longs.Long2ReferenceOpenHashMap;
import me.luligabi.amimi.common.util.HatchUtils;
import me.luligabi.amimi.common.util.MultiblockSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Block;

import brachy.modularui.drawable.SchemaRenderer;
import brachy.modularui.widgets.SchemaWidget;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.*;
import java.util.stream.Collectors;

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
public class MultiblockSchemaInfo {

    @Getter
    @Setter
    private SchemaWidget multiSchema;

    @Getter
    @Setter
    private SchemaRenderer renderer;

    @Getter
    @Setter
    private Pair<Guide, PageAnchor> guidePage;

    private List<SchemaData> multiblockSchemas = new ArrayList<>();

    public SchemaData getSchema(final int index) {
        return this.multiblockSchemas.get(index);
    }

    public int schemaSize() {
        return multiblockSchemas.size();
    }

    @ApiStatus.Internal
    public void initSchemas(MultiblockSet multiblockDefinition, @Nullable Runnable onSchemaRefresh) {
        final Block controller = BuiltInRegistries.BLOCK.get(multiblockDefinition.machine());

        for (final ShapeTemplate st : multiblockDefinition.shapeTemplates()) {
            final Map<BlockPos, BlockInfo> structureBlocks = new HashMap<>();
            structureBlocks.put(BlockPos.ZERO, BlockInfo.fromBlock(controller));

            for (var entry : st.simpleMembers.entrySet()) {
                structureBlocks.put(entry.getKey(), BlockInfo.fromBlockState(entry.getValue().getPreviewState()));
            }

            final Long2ReferenceMap<BlockState> schemaMap = new Long2ReferenceOpenHashMap<>();
            final Map<Block, Integer> countsByBlock = new LinkedHashMap<>();
            final Map<BlockPos, HatchData> hatchData = new HashMap<>();

            for (var entry : structureBlocks.entrySet()) {
                final BlockState state = entry.getValue().getBlockState();
                schemaMap.put(entry.getKey().asLong(), state);
                countsByBlock.merge(state.getBlock(), 1, Integer::sum);

                final HatchFlags hatchFlags = st.hatchFlags.get(entry.getKey());
                if (hatchFlags != null && !hatchFlags.values().isEmpty()) {
                    hatchData.put(entry.getKey(), HatchUtils.parseHatchFlags(hatchFlags));
                }
            }

            final MutableSchema mapSchema = new MutableSchema(schemaMap);
            final List<Pair<Block, Integer>> blockCounts = countsByBlock.entrySet().stream()
                    .map(e -> Pair.of(e.getKey(), e.getValue()))
                    .sorted((a, b) -> {
                        if (a.getFirst() == controller) return -1;
                        if (b.getFirst() == controller) return 1;
                        return Integer.compare(b.getSecond(), a.getSecond());
                    })
                    .collect(Collectors.toList());

            if (onSchemaRefresh != null) {
                onSchemaRefresh.run();
            }

            this.multiblockSchemas.add(new SchemaData(mapSchema, blockCounts, structureBlocks, hatchData, computeInitialZoom(mapSchema.getBounds())));
        }

        this.guidePage = getGuidePage(multiblockDefinition.machine());
    }

    private static Pair<Guide, PageAnchor> getGuidePage(final ResourceLocation machineId) {
        for (final Guide guide : GuideRegistry.getAll()) {
            final ItemIndex itemIndex = guide.getIndex(ItemIndex.class);
            final PageAnchor page = itemIndex.get(machineId);
            if (page != null) {
                return new Pair<>(guide, page);
            }
        }
        return null;
    }

    private static float computeInitialZoom(Pair<BlockPos, BlockPos> bounds) {
        BlockPos a = bounds.getFirst();
        BlockPos b = bounds.getSecond();

        Vector3f min = new Vector3f(Math.min(a.getX(), b.getX()), Math.min(a.getY(), b.getY()), Math.min(a.getZ(), b.getZ()));
        Vector3f max = new Vector3f(Math.max(a.getX(), b.getX()) + 1, Math.max(a.getY(), b.getY()) + 1, Math.max(a.getZ(), b.getZ()) + 1);
        Vector3f center = new Vector3f((min.x + max.x) / 2f, (min.y + max.y) / 2f, (min.z + max.z) / 2f);

        Vector3f u = new Vector3f(Mth.cos(PREVIEW_YAW), 0, Mth.sin(PREVIEW_YAW));
        u.y = (float) Math.tan(PREVIEW_PITCH) * u.length();
        u.normalize();
        Vector3f forward = new Vector3f(-u.x, -u.y, -u.z); // eye -> lookAt

        Vector3f right = new Vector3f();
        forward.cross(new Vector3f(0, 1, 0), right);
        right.normalize();
        Vector3f up = new Vector3f();
        right.cross(forward, up);

        float hFovHalf = (float) Math.atan(Math.tan(Math.toRadians(PREVIEW_VFOV_DEGREES) / 2.0) * ASPECT_RATIO);
        float vFovHalf = (float) Math.toRadians(PREVIEW_VFOV_DEGREES) / 2f;

        float requiredDist = 0f;
        for (int cx = 0; cx <= 1; cx++)
            for (int cy = 0; cy <= 1; cy++)
                for (int cz = 0; cz <= 1; cz++) {
                    float px = cx == 0 ? min.x : max.x;
                    float py = cy == 0 ? min.y : max.y;
                    float pz = cz == 0 ? min.z : max.z;
                    Vector3f d = new Vector3f(px - center.x, py - center.y, pz - center.z);

                    float zc = d.dot(forward);
                    float distH = Math.abs(d.dot(right)) / (float) Math.tan(hFovHalf) - zc;
                    float distV = Math.abs(d.dot(up))    / (float) Math.tan(vFovHalf) - zc;

                    requiredDist = Math.max(requiredDist, Math.max(distH, distV));
                }

        return requiredDist * ZOOM_MARGIN;
    }

    public record SchemaData(MutableSchema mapSchema,
                             List<Pair<Block, Integer>> blockCounts,
                             Map<BlockPos, BlockInfo> structureBlocks,
                             Map<BlockPos, HatchData> hatchData,
                             float scale) {}

    public record HatchData(List<Component> tooltip,
                            int color) {}

    public static final float PREVIEW_YAW = -Mth.HALF_PI - (5f * Mth.PI / 36f); // -115°
    public static final float PREVIEW_PITCH = Mth.PI / 18f;                       // 10°

    private static final float PREVIEW_VFOV_DEGREES = 70f;
    private static final float ASPECT_RATIO = 1.0f;
    private static final float ZOOM_MARGIN = 1.1f;

}