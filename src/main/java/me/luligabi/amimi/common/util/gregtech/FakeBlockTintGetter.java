package me.luligabi.amimi.common.util.gregtech;

import lombok.Setter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;
import java.util.stream.Stream;

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
public class FakeBlockTintGetter implements BlockAndTintGetter {

    @Setter
    public BlockAndTintGetter parent;
    @Setter
    public BlockPos pos;
    @Setter
    public BlockState state;
    @Setter
    public BlockEntity blockEntity;

    @Override
    public float getShade(Direction direction, boolean b) {
        return parent.getShade(direction, b);
    }

    @Override
    public LevelLightEngine getLightEngine() {
        return parent.getLightEngine();
    }

    @Override
    public int getBlockTint(BlockPos blockPos, ColorResolver colorResolver) {
        return parent.getBlockTint(blockPos, colorResolver);
    }

    @Override
    public int getBrightness(LightLayer lightType, BlockPos blockPos) {
        return parent.getBrightness(lightType, blockPos);
    }

    @Override
    public int getRawBrightness(BlockPos blockPos, int amount) {
        return parent.getRawBrightness(blockPos, amount);
    }

    @Override
    public boolean canSeeSky(BlockPos blockPos) {
        return parent.canSeeSky(blockPos);
    }

    @Override
    public @Nullable BlockEntity getBlockEntity(BlockPos blockPos) {
        return blockPos.equals(pos) ? blockEntity : parent.getBlockEntity(blockPos);
    }

    @Override
    public BlockState getBlockState(BlockPos blockPos) {
        return blockPos.equals(pos) ? state : parent.getBlockState(blockPos);
    }

    @Override
    public FluidState getFluidState(BlockPos blockPos) {
        return parent.getFluidState(blockPos);
    }

    @Override
    public int getLightEmission(BlockPos pos) {
        return parent.getLightEmission(pos);
    }

    @Override
    public int getMaxLightLevel() {
        return parent.getMaxLightLevel();
    }

    @Override
    public int getHeight() {
        return parent.getHeight();
    }

    @Override
    public int getMinBuildHeight() {
        return parent.getMinBuildHeight();
    }

    @Override
    public int getMaxBuildHeight() {
        return parent.getMaxBuildHeight();
    }

    @Override
    public int getSectionsCount() {
        return parent.getSectionsCount();
    }

    @Override
    public int getMinSection() {
        return parent.getMinSection();
    }

    @Override
    public int getMaxSection() {
        return parent.getMaxSection();
    }

    @Override
    public boolean isOutsideBuildHeight(BlockPos pos) {
        return parent.isOutsideBuildHeight(pos);
    }

    @Override
    public boolean isOutsideBuildHeight(int y) {
        return parent.isOutsideBuildHeight(y);
    }

    @Override
    public int getSectionIndex(int y) {
        return parent.getSectionIndex(y);
    }

    @Override
    public int getSectionIndexFromSectionY(int sectionIndex) {
        return parent.getSectionIndexFromSectionY(sectionIndex);
    }

    @Override
    public int getSectionYFromSectionIndex(int sectionIndex) {
        return parent.getSectionYFromSectionIndex(sectionIndex);
    }

    @Override
    public Stream<BlockState> getBlockStates(AABB area) {
        return parent.getBlockStates(area);
    }

    @Override
    public BlockHitResult isBlockInLine(ClipBlockStateContext context) {
        return parent.isBlockInLine(context);
    }

    @Override
    public BlockHitResult clip(ClipContext context) {
        return parent.clip(context);
    }

    @Override
    public @Nullable BlockHitResult clipWithInteractionOverride(Vec3 startVec, Vec3 endVec, BlockPos pos,
                                                                VoxelShape shape, BlockState state) {
        return parent.clipWithInteractionOverride(startVec, endVec, pos, shape, state);
    }

    @Override
    public double getBlockFloorHeight(VoxelShape shape, Supplier<VoxelShape> belowShapeSupplier) {
        return parent.getBlockFloorHeight(shape, belowShapeSupplier);
    }

    @Override
    public double getBlockFloorHeight(BlockPos pos) {
        return parent.getBlockFloorHeight(pos);
    }
}