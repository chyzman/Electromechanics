package com.chyzman.chyzyLogistics.logic.api;

import com.chyzman.chyzyLogistics.block.gate.GateStateStorage;
import com.chyzman.chyzyLogistics.block.gate.ProGateBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.RedstoneWireBlock;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.RedstoneView;

public class WorldGateContext extends GateContext {

    public final BlockPos position;
    public final RedstoneView world;

    public BlockState state;

    protected WorldGateContext(RedstoneView world, BlockPos position, GateStateStorage stateStorage, BlockState state) {
        super(stateStorage, state.get(Properties.HORIZONTAL_FACING).getOpposite());

        this.state = state;
        this.position = position;
        this.world = world;
    }

    public static WorldGateContext of(RedstoneView world, BlockPos pos) {
        var blockEntity = world.getBlockEntity(pos, ProGateBlockEntity.getBlockEntityType()).get();

        return new WorldGateContext(world, pos, blockEntity, world.getBlockState(pos));
    }

    public static WorldGateContext of(RedstoneView world, BlockPos pos, GateStateStorage stateStorage) {
        return new WorldGateContext(world, pos, stateStorage, world.getBlockState(pos));
    }

    public int getEmittedRedstonePower(Direction direction) {
        BlockPos blockPos = position.offset(direction);
        int i = world.getEmittedRedstonePower(blockPos, direction);
        if (i >= 15) {
            return i;
        } else {
            BlockState blockState = world.getBlockState(blockPos);
            return Math.max(i, (blockState.isOf(Blocks.REDSTONE_WIRE) || blockState.getBlock() instanceof RedstoneWireBlock) ? blockState.get(RedstoneWireBlock.POWER) : 0);
        }
    }
}
