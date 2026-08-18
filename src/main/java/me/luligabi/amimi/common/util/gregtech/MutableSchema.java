package me.luligabi.amimi.common.util.gregtech;

import aztech.modern_industrialization.machines.multiblocks.MultiblockMachineBlockEntity;
import brachy.modularui.drawable.schema.ISchema;
import brachy.modularui.drawable.schema.SchemaLevel;
import brachy.modularui.utils.BlockPosUtil;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.longs.Long2ReferenceMap;
import it.unimi.dsi.fastutil.longs.Long2ReferenceOpenHashMap;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.util.Iterator;
import java.util.Map;

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
public class MutableSchema implements ISchema {

    @Getter
    protected final Level level = new SchemaLevel();
    @Getter
    protected @NotNull BlockPos origin = BlockPos.ZERO;
    @Getter
    protected @NotNull Vector3f center = new Vector3f();
    @Getter
    private BlockPos controllerPos = BlockPos.ZERO;
    @Getter
    private Pair<BlockPos, BlockPos> bounds;

    @Getter
    protected final Long2ReferenceMap<BlockState> blocks = new Long2ReferenceOpenHashMap<>();

    public MutableSchema() {}

    public MutableSchema(Long2ReferenceMap<BlockState> blocks) {
        this();
        setBlocks(blocks);
    }

    public MutableSchema setBlocks(Long2ReferenceMap<BlockState> blocks) {
        // clear world
        for (Map.Entry<BlockPos, BlockState> entry : this) {
            getLevel().removeBlock(entry.getKey(), false);
        }
        this.blocks.clear();

        BlockPos.MutableBlockPos min = BlockPosUtil.MAX.mutable();
        BlockPos.MutableBlockPos max = BlockPosUtil.MIN.mutable();
        MultiblockMachineBlockEntity controller = null;
        //List<MultiblockPartMachine> parts = new ArrayList<>();

        for (long packed : blocks.keySet()) {
            BlockState block = blocks.get(packed);
            if (block.isAir()) continue;
            BlockPos pos = BlockPos.of(packed);

            // BE creation is already handled through here
            updateBlockState(pos, block);
            BlockPosUtil.setMin(min, pos);
            BlockPosUtil.setMax(max, pos);

            BlockEntity blockEntity = getLevel().getBlockEntity(pos);
            if (blockEntity instanceof MultiblockMachineBlockEntity mcm && controller == null) {
                controller = mcm;
                controllerPos = pos;
            }
        }
        this.origin = min.immutable();
        this.center = getCenterF(min, max);
        this.bounds = Pair.of(min, max);
        return this;
    }

    public MutableSchema updateBlockState(BlockPos pos, BlockState state) {
        this.blocks.put(pos.asLong(), state);
        getLevel().setBlockAndUpdate(pos, state);
        return this;
    }

    @Override
    public Vector3fc getFocus() {
        return center;
    }

    @Override
    public @NotNull Iterator<Map.Entry<BlockPos, BlockState>> iterator() {
        return blocks.long2ReferenceEntrySet().stream()
                .map(e -> Map.entry(BlockPos.of(e.getLongKey()), e.getValue()))
                .iterator();
    }

    // BlockPostUtil#getCenterF doesn't return half positions, so odd front width
    // multiblocks (basically all of them) wouldn't be properly centered
    private Vector3f getCenterF(BlockPos p1, BlockPos p2) {
        BlockPos min = BlockPosUtil.getMin(p1, p2);
        BlockPos max = BlockPosUtil.getMax(p1, p2);

        return new Vector3f(
                (min.getX() + max.getX() + 1) / 2f,
                (min.getY() + max.getY() + 1) / 2f,
                (min.getZ() + max.getZ() + 1) / 2f
        );
    }
}