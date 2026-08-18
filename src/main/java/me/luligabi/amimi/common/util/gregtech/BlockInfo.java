package me.luligabi.amimi.common.util.gregtech;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import com.mojang.serialization.Codec;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

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
public class BlockInfo {

    public static final FakeBlockTintGetter FAKE_LEVEL = new FakeBlockTintGetter();

    public static final BlockInfo EMPTY = new BlockInfo(Blocks.AIR);

    @Getter
    private final BlockState blockState;
    private final @Nullable ItemStack itemStack;
    @Getter
    private final @Nullable BlockEntity blockEntity;

    public BlockInfo(Block block) {
        this(block.defaultBlockState());
    }

    public BlockInfo(BlockState blockState) {
        this(blockState, null, null);
    }

    public BlockInfo(BlockState blockState, @Nullable BlockEntity blockEntity) {
        this(blockState, null, blockEntity);
    }

    public BlockInfo(BlockState blockState, @Nullable ItemStack itemStack,
                     @Nullable BlockEntity blockEntity) {
        this.blockState = blockState;
        this.itemStack = itemStack;
        this.blockEntity = blockEntity;

        FAKE_LEVEL.setState(blockState);
    }

    public static BlockInfo fromBlockState(BlockState state) {
        return new BlockInfo(state);
    }

    public static BlockInfo fromBlock(Block block) {
        return fromBlockState(block.defaultBlockState());
    }

    public ItemStack getItemStackForm() {
        return itemStack == null ? new ItemStack(blockState.getBlock()) : itemStack;
    }

    public void apply(Level level, BlockPos pos) {
        level.setBlockAndUpdate(pos, blockState);
        if (blockEntity != null) {
            level.setBlockEntity(blockEntity);
        }
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        BlockInfo blockInfo = (BlockInfo) o;
        return Objects.equals(this.blockState, blockInfo.blockState);
    }

    @Override
    public int hashCode() {
        return this.blockState.hashCode();
    }
}