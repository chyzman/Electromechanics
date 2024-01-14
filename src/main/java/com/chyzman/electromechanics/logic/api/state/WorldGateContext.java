package com.chyzman.electromechanics.logic.api.state;

import com.chyzman.electromechanics.block.gate.GateBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.RedstoneWireBlock;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.RedstoneView;
import net.minecraft.world.World;

public class WorldGateContext extends GateContext {

    public final BlockPos position;
    public final World world;

    public BlockState state;

    protected WorldGateContext(World world, BlockPos position, GateStateStorage stateStorage, BlockState state) {
        super(stateStorage, state.get(Properties.HORIZONTAL_FACING).getOpposite());

        this.state = state;
        this.position = position;
        this.world = world;
    }

    public static WorldGateContext of(World world, BlockPos pos) {
        var blockEntity = world.getBlockEntity(pos, GateBlockEntity.getBlockEntityType()).get();

        return new WorldGateContext(world, pos, blockEntity.storage(), world.getBlockState(pos));
    }

    public static WorldGateContext of(World world, BlockPos pos, GateStateStorage stateStorage) {
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

    public long getTime(){
        return this.world.getTime();
    }
}
