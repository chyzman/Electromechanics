package com.chyzman.electromechanics.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.ObserverBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldAccess;

public class ListenerBlock extends ObserverBlock {

    public ListenerBlock(Settings settings) {
        super(settings);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        return state;
    }
}